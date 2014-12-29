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
public class RecordsService extends ServiceBase implements OnDataReceiver {
    private final OnRecordsReaded _callback;

    public RecordsService(OnRecordsReaded callback)
    {
        _callback = callback;
    }

    public void GetRecords() {
        ReadDataTask t = new ReadDataTask(this);
        t.execute("http://10.0.2.2:9359/api/list");
    }

    @Override
    public void onDataReceived(String result) {
        try {
            Record[] records = Deserialize(result);
            _callback.onTaskCompleted(records);
        } catch (Exception e) {
            Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
        }
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
