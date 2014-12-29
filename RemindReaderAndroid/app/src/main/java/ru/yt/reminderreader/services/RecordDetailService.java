package ru.yt.reminderreader.services;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.RecordDetail;

/**
 * Created by Yustos on 26.12.2014.
 */
public class RecordDetailService extends ServiceBase implements OnDataReceiver {
    private final OnRecordDetailReader _callback;

    public RecordDetailService(OnRecordDetailReader callback)
    {
        _callback = callback;
    }

    public void GetRecordDetail(String id) {
        ReadDataTask t = new ReadDataTask(this);
        t.execute(String.format("http://10.0.2.2:9359/api/detail/%s", id));
    }

    public void SaveRecordDetail(RecordDetail record)
    {
        try {
            JSONObject json = new JSONObject();
            if (record.id != null)
            {
                json.put("id", record.id);
            }
            json.put("title", record.title);
            json.put("body", record.body);

            String data = json.toString();

            SaveDataTask t = new SaveDataTask(data, this);
            t.execute("http://10.0.2.2:9359/api/detail/");
        }
        catch (Exception e)
        {
            Log.d("Failed to save", e.getLocalizedMessage());
        }
    }

    public void DeleteRecordDetail(String id)
    {
        try {
            DeleteDataTask t = new DeleteDataTask(this);
            t.execute(String.format("http://10.0.2.2:9359/api/detail/%s", id));
        }
        catch (Exception e)
        {
            Log.d("Failed to save", e.getLocalizedMessage());
        }
    }

    @Override
    public void onDataReceived(String result) {
        try {
            RecordDetail record = Deserialize(result);
            _callback.onTaskCompleted(record);
        } catch (Exception e) {
            Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
        }
    }

    private RecordDetail Deserialize(String json) {
        try {
            JSONObject record = new JSONObject(json);
            Double time = record.getDouble("date");
            Date date = new Date((long)(time * 1000));
            RecordDetail r = new RecordDetail(
                record.getString("id"),
                date,
                record.getString("title"),
                record.getString("body"));
            return r;
        }
        catch (JSONException e)
        {
            Log.d("Deserialize", e.getLocalizedMessage());
            return null;
        }
    }
}
