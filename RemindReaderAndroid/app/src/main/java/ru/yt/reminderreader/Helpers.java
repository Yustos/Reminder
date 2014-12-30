package ru.yt.reminderreader;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Yustos on 30.12.2014.
 */
public final class Helpers {

    public static String GetServiceUrl(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.ReminderServiceUrl), "");
    }
}
