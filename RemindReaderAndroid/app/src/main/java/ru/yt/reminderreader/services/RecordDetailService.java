package ru.yt.reminderreader.services;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.domain.StateType;

/**
 * Created by Yustos on 26.12.2014.
 */
public class RecordDetailService extends ServiceBase implements DataReceiver {
    private final RecordDetailReader _callback;

    public RecordDetailService(RecordDetailReader callback)
    {
        super(callback.getServiceUrl());
        _callback = callback;
    }

    public void GetRecordDetail(String id) {
        ReadDataTask t = new ReadDataTask(this);
        t.execute(String.format("detail?id=%s", id));
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
            t.execute("detail/");
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
            t.execute(String.format("detail/%s", id));
        }
        catch (Exception e)
        {
            Log.d("Failed to save", e.getLocalizedMessage());
        }
    }

    @Override
    public void onDataReceived(String result) {
        try {
            RecordDetail record = Mappers.DeserializeDetail(result);
            _callback.onTaskCompleted(record);
        } catch (Exception e) {
            Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
        }
    }

    @Override
    public void onFailure(String message) {
        _callback.onFailure(message);
    }


}
