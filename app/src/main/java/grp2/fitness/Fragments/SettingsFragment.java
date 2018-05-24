package grp2.fitness.Fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import grp2.fitness.NavigationActivity;
import grp2.fitness.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getContext() != null){
            PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener((NavigationActivity)getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getContext() != null){
            PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener((NavigationActivity)getActivity());
        }
    }
}
