package ru.yt.reminderreader;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Yustos on 30.12.2014.
 */
public final class Helpers {

    public static String GetServiceUrl(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.ReminderServiceUrl), "");
    }

    public static Date GetSyncDate(Context context)
    {
        try {
            return new Date(PreferenceManager.getDefaultSharedPreferences(context)
                    .getLong(context.getString(R.string.SyncDate), new Date().getTime()));
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Failed to read sync date", Toast.LENGTH_SHORT).show();
            return new Date();
        }
    }

    public static void SetSyncDate(Context context, Date syncDate)
    {
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(context.getString(R.string.SyncDate), syncDate.getTime())
                .commit())
        {
            Toast.makeText(context, "Failed to write sync date", Toast.LENGTH_SHORT).show();
        }
    }

    public static String join(Iterable<?> items, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = items.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (!iterator.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
}
