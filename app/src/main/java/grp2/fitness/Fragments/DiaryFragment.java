package grp2.fitness.Fragments;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.models.nosql.DailyDataDO;
import com.amazonaws.models.nosql.DiaryDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import grp2.fitness.NavigationActivity;
import grp2.fitness.R;
import grp2.fitness.SetupGoals;

public class DiaryFragment extends Fragment{

    private int calGoal;
    private int calCurrent;
    private int calRemaining;
    private int calCut;

    private String userId;
    private String todayDate;
    private DynamoDBMapper dynamoDBMapper;
    private ArrayList<DiaryDO> diary;

    private EditText energy;
    private EditText description;
    private Button submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getActivity() == null){
            return null;
        }

        userId = ((NavigationActivity)getActivity()).getCredentialsProvider().getIdentityId();
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        todayDate = dateFormat.format(today);

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        energy = view.findViewById(R.id.energy);
        description = view.findViewById(R.id.description);
        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDiaryEntry(Double.parseDouble(energy.getText().toString()), description.getText().toString());
            }
        });

        diary = new ArrayList<>();
        syncDiary();

        return view;
    }

    public void addDiaryEntry(Double energy, String description){
        final DiaryDO diaryEntry = new DiaryDO();

        diaryEntry.setUserId(userId);
        diaryEntry.setEntryId(todayDate + diary.size());
        diaryEntry.setEnergy(energy);
        diaryEntry.setDescription(description);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(diaryEntry);
            }
        }).start();

        syncDiary();
    }

    private void syncDiary(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DiaryDO entry = new DiaryDO();
                entry.setUserId(userId);

                Condition rangeKeyCondition = new Condition()
                        .withComparisonOperator(ComparisonOperator.BEGINS_WITH)
                        .withAttributeValueList(new AttributeValue().withS(todayDate));

                DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                        .withHashKeyValues(entry)
                        .withRangeKeyCondition("entryId", rangeKeyCondition)
                        .withConsistentRead(false);

                PaginatedList<DiaryDO> result = dynamoDBMapper.query(DiaryDO.class, queryExpression);

                Gson gson = new Gson();
                JSONObject reader;
                diary.clear();

                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    String jsonFormOfItem = gson.toJson(result.get(i));

                    try {
                        reader = new JSONObject(jsonFormOfItem);

                        DiaryDO diaryEntry = new DiaryDO();

                        diaryEntry.setUserId(reader.getString("_energy"));
                        diaryEntry.setEntryId(reader.getString("_energy"));
                        diaryEntry.setEnergy(Double.parseDouble(reader.getString("_energy")));
                        diaryEntry.setDescription(reader.getString("_energy"));

                        diary.add(diaryEntry);
                    }catch (Exception e) {
                        //TODO
                    }
                }
            }
        }).start();
    }

    public int getCalGoal() {
        return calGoal;
    }

    public void setCalGoal(int calGoal) {
        this.calGoal = calGoal;
    }

    public int getCalCurrent() {
        return calCurrent;
    }

    public void setCalCurrent(int calCurrent) {
        this.calCurrent = calCurrent;
    }

    public int getCalRemaining() {
        return calRemaining;
    }

    public void setCalRemaining(int calRemaining) {
        this.calRemaining = calRemaining;
    }

    public int getCalCut() { return calCut; }

    public void setCalCut(int calCut) { this.calCut = calCut; }

}
