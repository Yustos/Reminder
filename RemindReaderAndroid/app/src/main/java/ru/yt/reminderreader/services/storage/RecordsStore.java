package ru.yt.reminderreader.services.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<RecordDetail> getRecords()
    {
        Cursor cursor = getReadableDatabase().query(TABLE_RECORDS, null, null, null, null, null, null, null);

        Log.d("RecordsStore", String.format("Loaded %s", cursor.getCount()));

        ArrayList<RecordDetail> result = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            int posId = cursor.getColumnIndex("id");
            int posDate = cursor.getColumnIndex("date");
            int posTitle = cursor.getColumnIndex("title");
            int posBody = cursor.getColumnIndex("body");

            do
            {
                RecordDetail record = new RecordDetail(
                    cursor.getString(posId),
                    new Date(cursor.getLong(posDate)),
                    cursor.getString(posTitle),
                    cursor.getString(posBody)
                );
                result.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public void addOrUpdateRecord(RecordDetail record)
    {
        ContentValues cv = new ContentValues();
        cv.put("id", record.id);
        cv.put("date", record.date.getTime());
        cv.put("title", record.title);
        cv.put("body", record.body);
        long res = getWritableDatabase().insertWithOnConflict(TABLE_RECORDS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("RecordsStore", String.format("%s", res));
    }
}
