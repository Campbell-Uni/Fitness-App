package grp2.fitness.handlers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.models.nosql.DailyDataDO;

import java.util.ArrayList;

import grp2.fitness.R;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.DailyDataView> {

    private ArrayList<DailyDataDO> leaderboard;

    public static class DailyDataView extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView stepsTV;
        public TextView energyTV;
        public TextView heartRateTV;

        public DailyDataView(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            stepsTV = view.findViewById(R.id.steps);
            energyTV = view.findViewById(R.id.energy);
            heartRateTV = view.findViewById(R.id.heart_rate);
        }
    }

    public LeaderboardAdapter(ArrayList<DailyDataDO> leaderboard){
        this.leaderboard = leaderboard;
    }

    @Override
    public DailyDataView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_leaderboard_item, parent, false);
        return new DailyDataView(view);
    }

    @Override
    public void onBindViewHolder(DailyDataView holder, int position) {
        holder.title.setText(leaderboard.get(position).getNickname());
        holder.stepsTV.setText(String.valueOf(leaderboard.get(position).getSteps()));
        holder.energyTV.setText(String.valueOf(leaderboard.get(position).getEnergy()));
        holder.heartRateTV.setText(String.valueOf(leaderboard.get(position).getAverageHeartRate()));
    }

    @Override
    public int getItemCount() {
        return leaderboard.size();
    }


}
