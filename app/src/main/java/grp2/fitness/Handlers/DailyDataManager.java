package grp2.fitness.Handlers;

import android.icu.util.Calendar;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.models.nosql.DailyDataDO;
import com.amazonaws.models.nosql.DiaryDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import grp2.fitness.Helpers.StringUtils;

public class DailyDataManager {

    public interface DailyDataListener{
        void onAllDailyDataSynced(ArrayList<DailyDataDO> allDailyData);
        void onDailyDataSaved(DailyDataDO dailyData);
    }

    public enum DailyDataColumn{DATE, HEART_RATE, ENERGY, STEPS}

    private String userId;
    private String todayDate;

    private DynamoDBMapper dynamoDBMapper;

    private DailyDataDO dailyData;
    private ArrayList<DailyDataDO> allDailyData;
    private DailyDataListener callback;

    public DailyDataManager(String userId, DailyDataListener callback){
        this.userId = userId;
        this.callback = callback;

        todayDate = StringUtils.getCurrentDateFormatted();

        dailyData = new DailyDataDO();
        dailyData.setUserId(userId);
        dailyData.setDate(todayDate);

        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider()))
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        syncDailyData();
    }

    private void syncDailyData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dailyData = dynamoDBMapper.load(
                        DailyDataDO.class,
                        todayDate,
                        userId);

                saveDailyData();
            }
        }).start();
    }

    public String getColumn(DailyDataColumn column){
        String result = "";

        switch (column){
            case DATE:
                result = dailyData.getDate();
                break;
            case STEPS:
                result = dailyData.getSteps().toString();
                break;
            case ENERGY:
                result = dailyData.getEnergy().toString();
                break;
            case HEART_RATE:
                result = dailyData.getAverageHeartRate().toString();
                break;
        }

        return result;
    }

    public void setColumn(DailyDataColumn column, String value){
        switch (column){
            case DATE:
                dailyData.setDate(value);
                break;
            case STEPS:
                dailyData.setSteps(Double.parseDouble(value));
                break;
            case ENERGY:
                dailyData.setEnergy(Double.parseDouble(value));
                break;
            case HEART_RATE:
                dailyData.setAverageHeartRate(Double.parseDouble(value));
                break;
        }

        saveDailyData();
    }

    private void saveDailyData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(dailyData);
                callback.onDailyDataSaved(dailyData);
            }
        }).start();
    }

    public void syncAllDailyData(final String date) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                allDailyData = new ArrayList<>(dynamoDBMapper.query(DailyDataDO.class, getAllDailyDataQuery(date)));
                callback.onAllDailyDataSynced(allDailyData);
            }
        }).start();
    }

    private DynamoDBQueryExpression<DailyDataDO> getAllDailyDataQuery(String date){
        DynamoDBQueryExpression<DailyDataDO> query = new DynamoDBQueryExpression<>();
        DailyDataDO hashObject = new DailyDataDO();
        hashObject.setDate(date);
        query.setHashKeyValues(hashObject);

        return query;
    }

    public void setCallback(DailyDataListener callback){
        this.callback = callback;
    }
}
