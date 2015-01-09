package ru.yt.reminderreader.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.domain.StateType;

/**
 * Created by Yustos on 07.01.2015.
 */
public class Mappers {
    public static RecordDetail DeserializeDetail(String json) {
        try {
            JSONObject record = new JSONObject(json);
            Double time = record.getDouble("date");
            Date date = new Date((long)(time * 1000));
            //TODO: add server record status
            return new RecordDetail(
                    record.getString("id"),
                    StateType.Server,
                    date,
                    record.getString("title"),
                    record.getString("body"));
        }
        catch (JSONException e)
        {
            Log.d("Deserialize", e.getLocalizedMessage());
            return null;
        }
    }

    public static Record[] DeserializeRecords(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray records = jsonObject.getJSONArray("items");
            final Integer count = records.length();
            Record[] result = new Record[count];
            for (Integer i = 0; i < count; i++) {
                JSONObject record = records.getJSONObject(i);
                Double time = record.getDouble("date");
                Date date = new Date((long)(time * 1000));
                //TODO: add server record status
                Record r = new Record(record.getString("id"),
                        StateType.Server,
                        date,
                        record.getString("title"));
                result[i] = r;
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
