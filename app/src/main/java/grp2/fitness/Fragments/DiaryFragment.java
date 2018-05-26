package grp2.fitness.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.amazonaws.models.nosql.DiaryDO;

import java.util.ArrayList;

import grp2.fitness.Handlers.DiaryManager;
import grp2.fitness.NavigationActivity;
import grp2.fitness.R;

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
