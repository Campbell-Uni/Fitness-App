package grp2.fitness.Handlers;

import android.icu.util.Calendar;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.models.nosql.DiaryDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import grp2.fitness.Helpers.StringUtils;

public class DiaryManager {

    public interface DiaryManagerListener{
        void onDiarySynced(ArrayList<DiaryDO> diary);
    }

    private String userId;
    private String currentDate;

    private DynamoDBMapper dynamoDBMapper;

    private DiaryManagerListener callback;
    private ArrayList<DiaryDO> diary;

    public DiaryManager(String userId, DiaryManagerListener callback){
        this.userId = userId;
        this.callback = callback;

        diary = new ArrayList<>();
        currentDate = StringUtils.getCurrentDateFormatted();

        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider()))
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();
    }

    public void addDiaryEntry(Double energy, String description){
        final DiaryDO diaryEntry = new DiaryDO();

        diaryEntry.setUserId(userId);
        diaryEntry.setEntryId(currentDate + "|" + diary.size());
        diaryEntry.setEnergy(energy);
        diaryEntry.setDescription(description);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(diaryEntry);
                syncDiary();
            }
        }).start();
    }

    public void syncDiary(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                diary.clear();
                diary.addAll(dynamoDBMapper.query(DiaryDO.class, getDiaryQuery()));
                callback.onDiarySynced(diary);

            }
        }).start();
    }

    private DynamoDBQueryExpression<DiaryDO> getDiaryQuery(){
        DiaryDO entry = new DiaryDO();
        entry.setUserId(userId);

        Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.BEGINS_WITH)
                .withAttributeValueList(new AttributeValue().withS(currentDate));

        return new DynamoDBQueryExpression<DiaryDO>()
                .withHashKeyValues(entry)
                .withRangeKeyCondition("entryId", rangeKeyCondition)
                .withConsistentRead(false);
    }

    public void setCallback(DiaryManagerListener callback){
        this.callback = callback;
    }
}
