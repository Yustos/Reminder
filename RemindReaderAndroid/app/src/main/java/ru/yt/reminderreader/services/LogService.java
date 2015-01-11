package ru.yt.reminderreader.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.yt.reminderreader.Helpers;
import ru.yt.reminderreader.domain.LogRecord;
import ru.yt.reminderreader.domain.LogSource;
import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.StateType;

/**
 * Created by Yustos on 11.01.2015.
 */
public class LogService extends ServiceBase implements DataReceiver {
    private final LogReader _callback;

    public LogService(LogReader callback)
    {
        super(callback.getServiceUrl());
        _callback = callback;
    }

    public void getLog(Date date, int limit, int source) {
        ReadDataTask t = new ReadDataTask(this);
        List<String> p = new ArrayList<String>();
        p.add(String.format("date=%s", date.getTime() / 1000.0));
        p.add(String.format("limit=%s", limit));
        if (source != 0)
        {
            p.add(String.format("source=%s", source));
        }
        t.execute(String.format("log?%s", Helpers.join(p, "&")));
    }

    @Override
    public void onDataReceived(String result) {
        List<LogRecord> records = Deserialize(result);
        _callback.onTaskCompleted(records);
    }

    @Override
    public void onFailure(String message) {
        _callback.onFailure(message);
    }

    private List<LogRecord> Deserialize(String json) {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray records = jsonObject.getJSONArray("items");
            final Integer count = records.length();
            List<LogRecord> result = new ArrayList<>(count);
            for (Integer i = 0; i < count; i++) {
                JSONObject record = records.getJSONObject(i);
                Double time = record.getDouble("date");
                Date date = new Date((long)(time * 1000));
                LogRecord r = new LogRecord(
                        date,
                        record.getString("client"),
                        record.getString("message"),
                        LogSource.fromInt(record.optInt("source", 0))
                        );
                result.add(r);
            }
            return result;
        }
        catch (JSONException e)
        {
            Log.d("Deserialize", e.getLocalizedMessage());
            return null;
        }
    }
}
