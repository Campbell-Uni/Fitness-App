package grp2.fitness.Fragments;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import grp2.fitness.Handlers.DiaryManager;
import grp2.fitness.NavigationActivity;
import grp2.fitness.R;
import grp2.fitness.SetupGoals;

public class DiaryFragment extends Fragment implements DiaryManager.DiaryManagerListener{

    private int calGoal;
    private int calCurrent;
    private int calRemaining;
    private int calCut;

    private NavigationActivity activity;

    private EditText energy;
    private EditText description;

    private ArrayAdapter<DiaryDO> diaryAdapter;
    private DiaryManager diaryManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getActivity() == null || getContext() == null){
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        activity = (NavigationActivity) getActivity();

        energy          = view.findViewById(R.id.energy);
        description     = view.findViewById(R.id.description);
        Button submit   = view.findViewById(R.id.submit);
        ListView list   = view.findViewById(R.id.list);

        ArrayList<DiaryDO> diary = new ArrayList<>();
        diaryManager = new DiaryManager(activity.getCredentialsProvider().getIdentityId(), this);

        diaryAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                diary
        );

        list.setAdapter(diaryAdapter);
        diaryManager.syncDiary();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaryManager.addDiaryEntry(Double.parseDouble(energy.getText().toString()), description.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onDiarySynced(final ArrayList<DiaryDO> diary) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                diaryAdapter.clear();
                diaryAdapter.addAll(diary);
                diaryAdapter.notifyDataSetChanged();
            }
        });
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
