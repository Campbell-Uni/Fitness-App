package grp2.fitness.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.amazonaws.models.nosql.DailyDataDO;
import com.amazonaws.models.nosql.DiaryDO;

import java.util.ArrayList;

import grp2.fitness.Handlers.DailyDataManager;
import grp2.fitness.handlers.LeaderboardAdapter;
import grp2.fitness.Handlers.DiaryManager;
import grp2.fitness.Helpers.StringUtils;
import grp2.fitness.NavigationActivity;
import grp2.fitness.R;

public class LeaderboardFragment extends Fragment implements DailyDataManager.DailyDataListener{

    private DailyDataManager dailyDataManager;
    private NavigationActivity activity;
    private LeaderboardAdapter leaderboardAdapter;
    private ArrayList<DailyDataDO> leaderboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getActivity() == null || getContext() == null){
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        activity = (NavigationActivity) getActivity();

        RecyclerView leaderboardRV = view.findViewById(R.id.leaderboard_list);
        leaderboardRV.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        leaderboard = new ArrayList<>();

        leaderboardRV.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        leaderboardRV.setLayoutManager(layoutManager);

        leaderboardAdapter = new LeaderboardAdapter(leaderboard);
        leaderboardRV.setAdapter(leaderboardAdapter);

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
    public void onAllDailyDataSynced(final ArrayList<DailyDataDO> allDailyData) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.hideLoadingIcon();
                leaderboard.clear();
                leaderboard.addAll(allDailyData);
                leaderboardAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDailyDataSaved(DailyDataDO dailyData) {

    }
}
