package grp2.fitness.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.amazonaws.models.nosql.DailyDataDO;
import com.amazonaws.models.nosql.DiaryDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import grp2.fitness.Handlers.DailyDataManager;
import grp2.fitness.handlers.LeaderboardAdapter;
import grp2.fitness.Handlers.DiaryManager;
import grp2.fitness.Helpers.StringUtils;
import grp2.fitness.NavigationActivity;
import grp2.fitness.R;
import grp2.fitness.helpers.UnitConverter;

public class LeaderboardFragment extends Fragment implements DailyDataManager.DailyDataListener{

    private enum Columns{STEPS, ENERGY, HEART_RATE}

    private DailyDataManager dailyDataManager;
    private NavigationActivity activity;

    private LeaderboardAdapter leaderboardAdapter;
    private ArrayList<DailyDataDO> leaderboard;

    private Spinner sortSP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getActivity() == null || getContext() == null){
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        activity = (NavigationActivity) getActivity();

        RecyclerView leaderboardRV = view.findViewById(R.id.leaderboard_list);
        sortSP = view.findViewById(R.id.sort_spinner);

        setupLeaderboard(leaderboardRV);
        setupSortSpinner();

        dailyDataManager = activity.getDailyDataManager();
        dailyDataManager.setCallback(this);
        dailyDataManager.syncAllDailyData(StringUtils.getCurrentDateFormatted());

        return view;
    }

    private void setupLeaderboard(RecyclerView leaderboardRV){
        leaderboard = new ArrayList<>();

        leaderboardRV.setHasFixedSize(true);
        leaderboardRV.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        leaderboardRV.setLayoutManager(layoutManager);

        leaderboardAdapter = new LeaderboardAdapter(leaderboard);
        leaderboardRV.setAdapter(leaderboardAdapter);
    }

    private void setupSortSpinner(){
        sortSP.setAdapter(
                new ArrayAdapter<>(
                activity,
                android.R.layout.simple_spinner_item,
                Columns.values()
        ));
        sortSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortLeaderboard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void sortLeaderboard(){
        ArrayList<DailyDataDO> tempLeaderboard = new ArrayList<>(leaderboard);

        switch ((Columns)sortSP.getSelectedItem()){
            case STEPS:
                tempLeaderboard.sort(Comparator.comparing(DailyDataDO::getSteps).reversed());
                break;
            case ENERGY:
                tempLeaderboard.sort(Comparator.comparing(DailyDataDO::getEnergy).reversed());
                break;
            case HEART_RATE:
                tempLeaderboard.sort(Comparator.comparing(DailyDataDO::getAverageHeartRate).reversed());
                break;
        }
        leaderboard.clear();
        leaderboard.addAll(tempLeaderboard);
        leaderboardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        dailyDataManager.setCallback(activity);
        super.onPause();
    }

    @Override
    public void onAllDailyDataSynced(final ArrayList<DailyDataDO> allDailyData) {
        activity.runOnUiThread(() -> {
            activity.hideLoadingIcon();
            leaderboard.clear();
            leaderboard.addAll(allDailyData);
            sortLeaderboard();
        });
    }

    @Override
    public void onDailyDataSaved(DailyDataDO dailyData) {

    }
}
