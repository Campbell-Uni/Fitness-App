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
import android.widget.Spinner;

import com.amazonaws.models.nosql.DiaryDO;

import java.util.ArrayList;

import grp2.fitness.Handlers.DiaryManager;
import grp2.fitness.NavigationActivity;
import grp2.fitness.R;

public class DiaryFragment extends Fragment implements DiaryManager.DiaryManagerListener{

    private enum EntryType{FOOD, EXERCISE}

    private NavigationActivity activity;

    private EditText energy;
    private EditText description;
    private Button submit;

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

        energy              = view.findViewById(R.id.energy);
        description         = view.findViewById(R.id.description);
        submit              = view.findViewById(R.id.submit);
        Spinner entryType   = view.findViewById(R.id.type_spinner);
        ListView list       = view.findViewById(R.id.list);

        submit.setEnabled(false);

        initialiseLeaderboard(list);

        entryType.setAdapter(
                new ArrayAdapter<>(
                        activity,
                        android.R.layout.simple_spinner_item,
                        EntryType.values()
                )
        );

        submit.setOnClickListener(v -> {
            activity.showLoadingIcon();
            Double energyValue = Double.parseDouble(energy.getText().toString());

            if(entryType.getSelectedItem() == EntryType.EXERCISE && energyValue > 0){
                energyValue *= -1;
            }

            diaryManager.addDiaryEntry(energyValue, description.getText().toString());
        });

        return view;
    }

    private void initialiseLeaderboard(ListView list){
        ArrayList<DiaryDO> diary = new ArrayList<>();
        diaryManager = new DiaryManager(activity.getCredentialsProvider().getIdentityId(), this);

        diaryAdapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_list_item_1,
                diary
        );

        list.setAdapter(diaryAdapter);
        diaryManager.syncDiary();
    }

    @Override
    public void onDiarySynced(final ArrayList<DiaryDO> diary) {
        activity.runOnUiThread(() -> {
            submit.setEnabled(true);
            diaryAdapter.clear();
            diaryAdapter.addAll(diary);
            diaryAdapter.notifyDataSetChanged();
            activity.hideLoadingIcon();
        });
    }
}
