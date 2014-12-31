package ru.yt.reminderreader.services.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.yt.reminderreader.domain.RecordDetail;

/**
 * Created by Yustos on 31.12.2014.
 */
public class RecordsStore extends SQLiteOpenHelper {
    private static final String DB_NAME = "records.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_RECORDS = "records";
    private static RecordsStore sRunManager;

    public RecordsStore(Context context)
    {
        super(context, DB_NAME, null, VERSION);
    }

    public static RecordsStore get(Context c) {
        if (sRunManager == null) {
            sRunManager = new RecordsStore(c.getApplicationContext());
        }
        return sRunManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table records ("
            + "id text primary key, date integer, title text, body text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addRecord(RecordDetail record)
    {
        ContentValues cv = new ContentValues();
        cv.put("id", record.id);
        cv.put("date", record.date.getTime());
        cv.put("title", record.title);
        cv.put("body", record.body);
        long res = getWritableDatabase().insert(TABLE_RECORDS, null, cv);
        Log.d("RecordsStore", String.format("%s", res));
    }
}
