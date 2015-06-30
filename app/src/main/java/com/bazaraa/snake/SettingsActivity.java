package com.bazaraa.snake;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_prefs);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_control_key)));
        //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_theme_key)));
    }

    private void bindPreferenceSummaryToValue(Preference pref) {
        pref.setOnPreferenceChangeListener(this);

        onPreferenceChange(
                pref,
                PreferenceManager.getDefaultSharedPreferences(pref.getContext()).getString(pref.getKey(), "")
        );
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        String stringValue = newValue.toString();

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            int prefIndex = listPref.findIndexOfValue(stringValue);

            if (prefIndex > -1) {
                pref.setSummary(listPref.getEntries()[prefIndex]);
            }
        }
        else {
            pref.setSummary(stringValue);
        }

        return true;
    }

    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
