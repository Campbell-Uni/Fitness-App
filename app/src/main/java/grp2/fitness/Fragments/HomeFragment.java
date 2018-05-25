package grp2.fitness.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.models.nosql.DailyDataDO;
import com.amazonaws.models.nosql.DiaryDO;

import java.util.ArrayList;
import java.util.Objects;

import grp2.fitness.Handlers.DailyDataManager;
import grp2.fitness.Handlers.DiaryManager;
import grp2.fitness.Helpers.StringUtils;
import grp2.fitness.NavigationActivity;
import grp2.fitness.R;

public class HomeFragment extends Fragment implements
        View.OnClickListener,
        DiaryManager.DiaryManagerListener,
        DailyDataManager.DailyDataListener{

    private Double goalEnergy;

    private TextView currentEnergyTV;
    private TextView remainingEnergyTV;
    private TextView stepsTV;

    private NavigationActivity activity;
    private DailyDataManager dailyDataManager;
    private ArrayAdapter<DailyDataDO> leaderboardAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getActivity() == null || getContext() == null){
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = (NavigationActivity) getActivity();

        CardView diaryCard = view.findViewById(R.id.diarycard);
        CardView pedometerCard = view.findViewById(R.id.pedometercard);
        CardView leaderboardCard = view.findViewById(R.id.leaderboardcard);

        TextView goalEnergyTV = view.findViewById(R.id.goalcal);
        ListView leaderboardList = view.findViewById(R.id.leaderboard);
        currentEnergyTV     = view.findViewById(R.id.currentcal);
        remainingEnergyTV   = view.findViewById(R.id.remainingcal);
        stepsTV             = view.findViewById(R.id.pedometer_text);

        diaryCard.setOnClickListener(this);
        pedometerCard.setOnClickListener(this);
        leaderboardCard.setOnClickListener(this);

        DiaryManager diaryManager = new DiaryManager(activity.getCredentialsProvider().getIdentityId(), this);
        diaryManager.syncDiary();

        goalEnergy = getGoalEnergy();
        goalEnergyTV.setText(goalEnergy.toString());

        ArrayList<DailyDataDO> leaderboard = new ArrayList<>();

        dailyDataManager = activity.getDailyDataManager();
        dailyDataManager.setCallback(this);

        leaderboardAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                leaderboard
        );

        leaderboardList.setAdapter(leaderboardAdapter);
        dailyDataManager.syncAllDailyData(StringUtils.getCurrentDateFormatted());

        return view;
    }

    @Override
    public void onPause() {
        dailyDataManager.setCallback(activity);
        super.onPause();
    }

    @Override
    public void onClick(View view) {

        if(getActivity() == null){
            return;
        }

        switch(view.getId()){
            case R.id.diarycard :
                ((NavigationActivity)getActivity()).updateView(DiaryFragment.class);
                break;
        }

    }

    @Override
    public void onDiarySynced(final ArrayList<DiaryDO> diary) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Double consumedEnergy = 0.0;
                Double remainingEnergy = 0.0;

                for(DiaryDO diaryEntry : diary){
                    consumedEnergy += diaryEntry.getEnergy();
                }

                remainingEnergy = goalEnergy - consumedEnergy;

                currentEnergyTV.setText(consumedEnergy.toString());
                remainingEnergyTV.setText(remainingEnergy.toString());
            }
        });
    }

    @Override
    public void onAllDailyDataSynced(final ArrayList<DailyDataDO> allDailyData) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leaderboardAdapter.clear();
                leaderboardAdapter.addAll(allDailyData);
                leaderboardAdapter.notifyDataSetChanged();

                for(DailyDataDO dailyDataDO : allDailyData){
                    if(dailyDataDO.getUserId().equals(activity.getCredentialsProvider().getIdentityId())){
                        String stepString = "Steps: " + dailyDataDO.getSteps().toString();
                        stepsTV.setText(stepString);
                    }
                }
            }
        });
    }

    private double getGoalEnergy() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences();
        String energyGoalKey = activity.getString(R.string.pref_key_personal_energy);
        return Double.parseDouble(sharedPreferences.getString(energyGoalKey, "0"));
    }

    @Override
    public void onDailyDataSaved(DailyDataDO dailyData) {}
}
