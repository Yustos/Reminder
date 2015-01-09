package ru.yt.reminderreader;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yt.reminderreader.adapters.RecordListAdapter;
import ru.yt.reminderreader.adapters.RecordSyncListAdapter;
import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.domain.RecordSync;
import ru.yt.reminderreader.services.SyncReader;
import ru.yt.reminderreader.services.SyncService;
import ru.yt.reminderreader.services.storage.RecordsStore;


public class SyncActivity extends ActionBarActivity implements SyncReader {

    private List<RecordSync> _recordsList = new ArrayList<>();
    private Map<String, RecordSync> _recordsMap = new HashMap<>();

    private RecordSyncListAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        SyncService service = new SyncService(this);
        List<Record> records = service.getRecordsToSync();
        for (Record r: records) {
            RecordSync syncItem = new RecordSync(r, false);
            _recordsList.add(syncItem);
            _recordsMap.put(r.id, syncItem);
        }

        _adapter = new RecordSyncListAdapter(this, _recordsList);

        ListView listViewRecords = (ListView)findViewById(R.id.listViewRecords);
        listViewRecords.setAdapter(_adapter);

        findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncService service = new SyncService(SyncActivity.this);
                service.syncRecords(_recordsList);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getServiceUrl()  {
        return Helpers.GetServiceUrl(this);
    }

    @Override
    public RecordsStore getStore() {
        return RecordsStore.get(this);
    }

    @Override
    public Date getSyncDate() {
        return Helpers.GetSyncDate(this);
    }

    @Override
    public void setSyncDate(Date syncDate) {
        Helpers.SetSyncDate(this, syncDate);
    }

    @Override
    public void onProgress(RecordSync result) {
        if (_recordsMap.containsKey(result.id))
        {
            RecordSync record = _recordsMap.get(result.id);
            _recordsList.remove(record);
        }
        _recordsList.add(result);
        _recordsMap.put(result.id, result);

        _adapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskCompleted()
    {
        setResult(RESULT_OK);
        Toast.makeText(this, "Finish", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String message) {
        setResult(RESULT_OK);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
