package ru.yt.reminderreader.services;

import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.domain.RecordSync;
import ru.yt.reminderreader.domain.StateType;
import ru.yt.reminderreader.services.storage.RecordsStore;

/**
 * Created by Yustos on 06.01.2015.
 */
public class SyncService extends ServiceBase {
    private final SyncReader _callback;
    private Date _syncDate;

    public SyncService(SyncReader callback)
    {
        super(callback.getServiceUrl());
        _callback = callback;
    }

    public List<Record> getRecordsToSync()
    {
        RecordsStore store = _callback.getStore();
        return store.getRecordsToSync();
    }

    public void syncRecords(List<RecordSync> records) {
        _syncDate = new Date();
        SyncTask task = new SyncTask(this, _callback.getSyncDate());
        task.execute(records);
    }

    public RecordDetail syncRecord(Record r) throws Exception {
        RecordsStore store = _callback.getStore();
        RecordDetail record = store.getRecordDetail(r.id);
        RecordDetail result;
        if (record == null)
        {
            String json = callService(String.format("detail?id=%s", r.id), new HttpGet());
            result = Mappers.DeserializeDetail(json);
        }
        else {
            JSONObject json = new JSONObject();
            if (record.id != null) {
                json.put("id", record.id);

            }
            json.put("title", record.title);
            json.put("body", record.body);
            json.put("state", record.state.getNumVal());

            String data = json.toString();

            String resultJson = callService("sync", createPostRequest(data));

            result = Mappers.DeserializeDetail(resultJson);
        }
        store.addOrUpdateRecord(result);
        return result;
    }

    public List<RecordSync> getDeltaFromDate(Date syncDate) {
        try {
            Log.d("SyncService", String.format("Get delta from %s", syncDate));

            Double date = syncDate.getTime() / 1000.0;
            String json = callService(String.format("sync?date=%s", date), new HttpGet());
            Record[] records = Mappers.DeserializeRecords(json);

            List<RecordSync> result = new ArrayList<RecordSync>();
            for (Record record : records) {
                result.add(new RecordSync(record, false));
            }
            return result;
        }
        catch (Exception e){
            //TODO: to ui. Missing item, no show.
            Log.d("SyncService", e.getLocalizedMessage());
            return new ArrayList<RecordSync>();
        }
    }

    public void onTaskCompleted() {
        _callback.setSyncDate(_syncDate);
        _callback.onTaskCompleted();
    }

    public void onFailure(String message) {
        _callback.onFailure(message);
    }

    public void onProgress(RecordSync record)
    {
        _callback.onProgress(record);
    }
}
