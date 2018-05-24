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

public class DailyDataManager {

    public enum DailyDataColumn{DATE, HEART_RATE, ENERGY, STEPS}

    private DynamoDBMapper dynamoDBMapper;
    private DailyDataDO dailyData;
    private ArrayList<DailyDataDO> allDailyData;

    private String userId;
    private String todayDate;

    public DailyDataManager(String userId){
        this.userId = userId;

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        todayDate = dateFormat.format(today);

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        syncDailyData();
    }

    public void syncDailyData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dailyData = dynamoDBMapper.load(
                        DailyDataDO.class,
                        todayDate,
                        userId);

                if(dailyData == null){
                    createDailyData();
                }

                //TODO - Add callback?
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

    private void createDailyData() {
        dailyData = new DailyDataDO();

        dailyData.setUserId(userId);
        dailyData.setDate(todayDate);

        saveDailyData();
    }

    private void saveDailyData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(dailyData);
            }
        }).start();
    }

    private void syncAllDailyData(final String date) {
        allDailyData = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DynamoDBQueryExpression<DailyDataDO> query = new DynamoDBQueryExpression<DailyDataDO>();
                DailyDataDO hashObject = new DailyDataDO();
                hashObject.setDate(date);
                query.setHashKeyValues(hashObject);

                PaginatedList<DailyDataDO> result = dynamoDBMapper.query(DailyDataDO.class, query);

                Gson gson = new Gson();
                JSONObject reader;

                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    String jsonFormOfItem = gson.toJson(result.get(i));

                    try {
                        reader = new JSONObject(jsonFormOfItem);

                        DailyDataDO dailyData = new DailyDataDO();

                        dailyData.setDate(reader.getString("_date"));
                        dailyData.setUserId(reader.getString("_userId"));
                        dailyData.setEnergy(Double.parseDouble(reader.getString("_energy")));
                        dailyData.setAverageHeartRate(Double.parseDouble(reader.getString("_averageHeartRate")));
                        dailyData.setSteps(Double.parseDouble(reader.getString("_steps")));

                        allDailyData.add(dailyData);
                    } catch (Exception e) {
                        //TODO
                    }
                }
            }
        }).start();
    }

    public ArrayList<DailyDataDO> getAllDailyData() {
        return allDailyData;
    }
}
