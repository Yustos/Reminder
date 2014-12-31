package ru.yt.reminderreader.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import ru.yt.reminderreader.domain.Record;

/**
 * Created by Yustos on 25.12.2014.
 */
public class RecordsService extends ServiceBase implements DataReceiver {
    private final RecordsReader _callback;

    public RecordsService(RecordsReader callback)
    {
        super(callback.getServiceUrl());
        _callback = callback;
    }

    public void GetRecords() {
        ReadDataTask t = new ReadDataTask(this);
        t.execute("list");
    }

    @Override
    public void onDataReceived(String result) {
        Record[] records = Deserialize(result);
        _callback.onTaskCompleted(records);
    }

    @Override
    public void onFailure(String message) {
        _callback.onFailure(message);
    }

    private Record[] Deserialize(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray records = jsonObject.getJSONArray("items");
            final Integer count = records.length();
            Record[] result = new Record[count];
            for (Integer i = 0; i < count; i++) {
                JSONObject record = records.getJSONObject(i);
                Double time = record.getDouble("date");
                Date date = new Date((long)(time * 1000));
                Record r = new Record(record.getString("id"),
                    date,
                    record.getString("title"));
                result[i] = r;
            }
            Arrays.sort(result, RecordsComparator);
            return result;
        }
        catch (JSONException e)
        {
            Log.d("Deserialize", e.getLocalizedMessage());
            return null;
        }
    }

    public static Comparator<Record> RecordsComparator = new Comparator<Record>() {
        @Override
        public int compare(Record ld1, Record ld2) {
            return -ld1.date.compareTo(ld2.date);
        }
    };

}
