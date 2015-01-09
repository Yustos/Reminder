package ru.yt.reminderreader.services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.domain.RecordSync;

/**
 * Created by Yustos on 06.01.2015.
 */
public class SyncTask extends AsyncTask<List<RecordSync>, RecordSync, Void> {
    private final SyncService _service;
    private final Date _syncDate;

    public SyncTask(SyncService service, Date syncDate)
    {
        _service = service;
        _syncDate = syncDate;
    }

    @Override
    protected Void doInBackground(List<RecordSync>... params) {
        syncRecords(params[0]);
        syncRecords(_service.getDeltaFromDate(_syncDate));
        return null;
    }

    @Override
    protected void onProgressUpdate(RecordSync... record)
    {
        _service.onProgress(record[0]);
    }

    @Override
    protected void onPostExecute(Void result)
    {
        _service.onTaskCompleted();
    }

    private void syncRecords(Iterable<RecordSync> records)
    {
        for (Record record: records)
        {
            try {
                RecordDetail resultRecord = _service.syncRecord(record);
                publishProgress(new RecordSync(resultRecord, true));
            }
            catch (Exception e)
            {
                String error = e.getLocalizedMessage();
                RecordSync errorProgress = new RecordSync(record, false);
                errorProgress.setError(error);
                Log.d("SyncTask", e.toString());
                publishProgress(errorProgress);
            }
        }
    }
}
