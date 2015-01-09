package ru.yt.reminderreader.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.StateType;

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
        Record[] result = Mappers.DeserializeRecords(json);
        Arrays.sort(result, RecordsComparator);
        return result;
    }

    public static Comparator<Record> RecordsComparator = new Comparator<Record>() {
        @Override
        public int compare(Record ld1, Record ld2) {
            return -ld1.date.compareTo(ld2.date);
        }
    };

}
