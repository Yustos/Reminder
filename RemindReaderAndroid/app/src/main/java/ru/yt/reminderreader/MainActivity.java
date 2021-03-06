package ru.yt.reminderreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.yt.reminderreader.adapters.RecordListAdapter;
import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.services.RecordsReader;
import ru.yt.reminderreader.services.SyncReader;
import ru.yt.reminderreader.services.SyncService;
import ru.yt.reminderreader.services.storage.RecordsStore;


public class MainActivity extends ActionBarActivity implements RecordsReader {
    public List<Record> RecordsList = new ArrayList<>();

    private RecordListAdapter _adapter;

    private ProgressDialog _progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init progress dialog
        _progressDialog = new ProgressDialog(this);
        _progressDialog.setMessage("Loading...");
        _progressDialog.setIndeterminate(false);
        _progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDialog.setCancelable(true);

        //load records from cache
        RecordsStore store = RecordsStore.get(this);
        RecordsList.addAll(store.getRecords(false));

        // init list view adapter
        _adapter = new RecordListAdapter(this, RecordsList);

        ListView listViewRecords = (ListView)findViewById(R.id.listViewRecords);
        listViewRecords.setAdapter(_adapter);

        listViewRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Record record = _adapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("RecordId", record.id);
                startActivityForResult(intent, 0);
            }
        });

        // init buttons
        findViewById(R.id.buttonSync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SyncActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        findViewById(R.id.buttonPurge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordsStore store = RecordsStore.get(MainActivity.this);
                store.purgeDatabase();
                RecordsList.clear();
                _adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, PrefActivity.class));
            return true;
        }
        if (id == R.id.action_log) {
            startActivity(new Intent(this, LogActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            RecordsStore store = RecordsStore.get(this);
            if (data != null && data.hasExtra("RecordId"))
            {
                String id = data.getStringExtra("RecordId");
                Record record = (Record)store.getRecordDetail(id);
                RecordsList.add(0, record);
            }
            else
            {
                List<Record> records = store.getRecords(false);
                RecordsList.clear();
                RecordsList.addAll(records);
            }
            _adapter.notifyDataSetChanged();
        }
    }

    @Override
    public String getServiceUrl() {
        return Helpers.GetServiceUrl(this);
    }

    @Override
    public void onTaskCompleted(Record[] result) {
        _progressDialog.dismiss();
        RecordsList.clear();
        RecordsList.addAll(Arrays.asList(result));
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String message) {
        _progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
