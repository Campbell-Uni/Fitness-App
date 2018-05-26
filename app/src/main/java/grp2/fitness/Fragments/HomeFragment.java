package grp2.fitness.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.models.nosql.DailyDataDO;
import com.amazonaws.models.nosql.DiaryDO;

import java.util.ArrayList;
import java.util.Comparator;

import grp2.fitness.Handlers.DailyDataManager;
import grp2.fitness.Handlers.DiaryManager;
import grp2.fitness.handlers.LeaderboardAdapter;
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
    private LeaderboardAdapter leaderboardAdapter;
    private ArrayList<DailyDataDO> leaderboard;

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
        RecyclerView leaderboardList = view.findViewById(R.id.menu_leaderboard);
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

        leaderboard = new ArrayList<>();

        leaderboardList.setHasFixedSize(true);
        leaderboardList.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        leaderboardList.setLayoutManager(layoutManager);

        leaderboardAdapter = new LeaderboardAdapter(leaderboard);
        leaderboardList.setAdapter(leaderboardAdapter);

        dailyDataManager = activity.getDailyDataManager();
        dailyDataManager.setCallback(this);
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

        switch(view.getId()){
            case R.id.diarycard :
                activity.onNavigationItemSelected(activity.getNavigationView().getMenu().findItem(R.id.diary));
                getActivity().setTitle("Diary");
                break;
            case R.id.pedometercard :
                activity.onNavigationItemSelected(activity.getNavigationView().getMenu().findItem(R.id.pedometer));
                getActivity().setTitle("Pedometer");
                break;
            case R.id.leaderboardcard :
                activity.onNavigationItemSelected(activity.getNavigationView().getMenu().findItem(R.id.menu_leaderboard));
                getActivity().setTitle("Leaderboard");
                break;
        }

    }

    @Override
    public void onDiarySynced(final ArrayList<DiaryDO> diary) {
        activity.runOnUiThread(() -> {
            Double consumedEnergy = 0.0;
            Double remainingEnergy;



                for(DiaryDO diaryEntry : diary){
                    consumedEnergy += diaryEntry.getEnergy();
                }

            remainingEnergy = goalEnergy - consumedEnergy;

            currentEnergyTV.setText(consumedEnergy.toString());
            remainingEnergyTV.setText(remainingEnergy.toString());
        });
    }

    @Override
    public void onAllDailyDataSynced(final ArrayList<DailyDataDO> allDailyData) {
        activity.runOnUiThread(() -> {
            dailyDataSynced = true;
            if(diarySynced){
                activity.hideLoadingIcon();
            }

            leaderboard.clear();
            leaderboard.addAll(allDailyData);
            leaderboard.sort(Comparator.comparing(DailyDataDO::getSteps).reversed());
            leaderboardAdapter.notifyDataSetChanged();

            for(DailyDataDO dailyDataDO : allDailyData){
                if(dailyDataDO.getUserId().equals(activity.getCredentialsProvider().getIdentityId())){
                    String stepString = "Steps: " + dailyDataDO.getSteps().toString();
                    stepsTV.setText(stepString);
                }
            }
        });
    }

    private double getGoalEnergy() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences();
        String energyGoalKey = activity.getString(R.string.pref_key_goal_energy);
        return Double.parseDouble(sharedPreferences.getString(energyGoalKey, "0"));
    }

    @Override
    public void onDailyDataSaved(DailyDataDO dailyData) {}
}
