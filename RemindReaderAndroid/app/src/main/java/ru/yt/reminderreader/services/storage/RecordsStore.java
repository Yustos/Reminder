package ru.yt.reminderreader.services.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ru.yt.reminderreader.Helpers;
import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.domain.StateType;

/**
 * Created by Yustos on 31.12.2014.
 */
public class RecordsStore extends SQLiteOpenHelper {
    private static final String DB_NAME = "records.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_RECORDS = "records";
    private static RecordsStore sRunManager;

    public RecordsStore(Context context) {
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
                        + "id text primary key, state integer, date integer, title text, body text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void purgeDatabase()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_RECORDS);
        onCreate(db);
    }

    public List<Record> getRecords(Boolean includeDeleted) {
        Cursor cursor = getReadableDatabase().query(TABLE_RECORDS, new String[] { "id", "state", "date", "title" },
                null, null, null, null, "date desc", null);
        Log.d("RecordsStore", String.format("Loaded %s", cursor.getCount()));

        ArrayList<Record> result = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            int posId = cursor.getColumnIndex("id");
            int posState = cursor.getColumnIndex("state");
            int posDate = cursor.getColumnIndex("date");
            int posTitle = cursor.getColumnIndex("title");

            do
            {
                Record record = new Record(
                        cursor.getString(posId),
                        cursor.isNull(posState) ? StateType.Unknown : StateType.fromInt(cursor.getInt(posState)),
                        new Date(cursor.getLong(posDate)),
                        cursor.getString(posTitle)
                );
                result.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public List<Record> getRecordsToSync() {
        //TODO: copy pasted getRecords
        List<Integer> states = new ArrayList<>();
        states.add(StateType.Added.getNumVal());
        states.add(StateType.Modified.getNumVal());
        states.add(StateType.Deleted.getNumVal());
        String whereClause = String.format("state in (%s)", Helpers.join(states, ","));

        Cursor cursor = getReadableDatabase().query(TABLE_RECORDS, new String[] { "id", "state", "date", "title" },
                whereClause, null, null, null, "date desc", null);
        Log.d("RecordsStore", String.format("Loaded %s", cursor.getCount()));

        ArrayList<Record> result = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            int posId = cursor.getColumnIndex("id");
            int posState = cursor.getColumnIndex("state");
            int posDate = cursor.getColumnIndex("date");
            int posTitle = cursor.getColumnIndex("title");

            do
            {
                Record record = new Record(
                        cursor.getString(posId),
                        cursor.isNull(posState) ? StateType.Unknown : StateType.fromInt(cursor.getInt(posState)),
                        new Date(cursor.getLong(posDate)),
                        cursor.getString(posTitle)
                );
                result.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public RecordDetail getRecordDetail(String id) {
        Cursor cursor = getReadableDatabase().query(TABLE_RECORDS, null, String.format("id='%s'", id), null, null, null, null, null);
        Log.d("RecordsStore", String.format("Loaded %s", cursor.getCount()));

        RecordDetail result = null;

        if (cursor.moveToFirst())
        {
            int posId = cursor.getColumnIndex("id");
            int posState = cursor.getColumnIndex("state");
            int posDate = cursor.getColumnIndex("date");
            int posTitle = cursor.getColumnIndex("title");
            int posBody = cursor.getColumnIndex("body");
            result = new RecordDetail(
                cursor.getString(posId),
                cursor.isNull(posState) ? StateType.Unknown : StateType.fromInt(cursor.getInt(posState)),
                new Date(cursor.getLong(posDate)),
                cursor.getString(posTitle),
                cursor.getString(posBody)
            );
        }
        else
        {
            Log.d("RecordsStore", String.format("Record not found by id: %s", id));
        }
        cursor.close();
        return result;
    }


    public void addOrUpdateRecord(RecordDetail record)
    {
        ContentValues cv = new ContentValues();
        cv.put("id", record.id);
        cv.put("state", record.state.getNumVal());
        cv.put("date", record.date.getTime());
        cv.put("title", record.title);
        cv.put("body", record.body);
        long res = getWritableDatabase().insertWithOnConflict(TABLE_RECORDS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("RecordsStore", String.format("Updated %s", res));
    }

    public void markAsDeleted(String id)
    {
        ContentValues cv = new ContentValues();
        cv.put("state", StateType.Deleted.getNumVal());
        int res = getWritableDatabase().update(TABLE_RECORDS, cv, "id=?", new String[] { id });
        Log.d("RecordsStore", String.format("Marked as deleted %s", res));
    }

    public void delete(String id)
    {
        int res = getWritableDatabase().delete(TABLE_RECORDS, "id=?", new String[] { id });
        Log.d("RecordsStore", String.format("Deleted %s", res));
    }
}
