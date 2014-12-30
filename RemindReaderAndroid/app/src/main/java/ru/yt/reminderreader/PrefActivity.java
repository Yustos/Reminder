package ru.yt.reminderreader;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Yustos on 30.12.2014.
 */
public class PrefActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.reminder_pref);
    }
}
