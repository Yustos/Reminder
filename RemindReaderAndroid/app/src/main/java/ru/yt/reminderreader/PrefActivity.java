package ru.yt.reminderreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

import java.util.Date;

/**
 * Created by Yustos on 30.12.2014.
 */
public class PrefActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_window);

        getFragmentManager().beginTransaction().replace(R.id.displayPrefs,
                new PrefsFragment()).commit();
        PreferenceManager.setDefaultValues(PrefActivity.this, R.xml.reminder_pref, false);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.reminder_pref);

            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
                initSummary(getPreferenceScreen().getPreference(i));
            }
        }

        @Override
        public void onResume(){
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefSummary(findPreference(key));
        }

        private void initSummary(Preference p){
            if (p instanceof PreferenceCategory){
                PreferenceCategory pCat = (PreferenceCategory)p;
                for(int i=0;i<pCat.getPreferenceCount();i++){
                    initSummary(pCat.getPreference(i));
                }
            }else{
                updatePrefSummary(p);
            }

        }

        private void updatePrefSummary(Preference p){
            if (p instanceof ListPreference) {
                ListPreference listPref = (ListPreference) p;
                p.setSummary(listPref.getEntry());
            }
            if (p instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p;
                p.setSummary(editTextPref.getText());
            }
        }
    }
}
