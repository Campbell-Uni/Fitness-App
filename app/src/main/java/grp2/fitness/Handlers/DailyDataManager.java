package grp2.fitness.Handlers;

import android.icu.util.Calendar;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.DailyDataDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyDataManager {

    public enum DailyDataColumn{DATE, HEART_RATE, ENERGY, STEPS}

    private DynamoDBMapper dynamoDBMapper;
    private DailyDataDO dailyData;

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
                        userId,
                        todayDate);

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
}
