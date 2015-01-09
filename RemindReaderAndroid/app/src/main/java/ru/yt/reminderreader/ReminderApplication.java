package ru.yt.reminderreader;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Yustos on 02.01.2015.
 */
public class ReminderApplication extends Application {
    public void onCreate ()
    {
        // Setup handler for uncaught exceptions.
        /*Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                Log.e("ReminderApplication", e.getLocalizedMessage());
            }
        });*/
    }
}
