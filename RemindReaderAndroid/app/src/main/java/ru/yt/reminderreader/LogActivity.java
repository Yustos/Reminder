package ru.yt.reminderreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.yt.reminderreader.adapters.LogListAdapter;
import ru.yt.reminderreader.domain.LogRecord;
import ru.yt.reminderreader.services.LogReader;
import ru.yt.reminderreader.services.LogService;


public class LogActivity extends ActionBarActivity implements LogReader {
    public List<LogRecord> _log = new ArrayList<>();
    private Date _lastDate = new Date();
    private int _source = 0;

    private LogListAdapter _adapter;

    private ProgressDialog _progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // filter init
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this,
            R.array.source_list, android.R.layout.simple_spinner_dropdown_item);

        ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                _lastDate = new Date();
                _source = position;
                _log.clear();
                LoadLog();
                return true;
            }
        };

        actionBar.setListNavigationCallbacks(spinnerAdapter, navigationListener);

        // init progress dialog
        _progressDialog = new ProgressDialog(this);
        _progressDialog.setMessage("Loading...");
        _progressDialog.setIndeterminate(false);
        _progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDialog.setCancelable(true);

        // init list view adapter
        _adapter = new LogListAdapter(this, _log);

        ListView listViewRecords = (ListView)findViewById(R.id.listViewLogRecords);
        listViewRecords.setAdapter(_adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_load_more) {
            LoadLog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getServiceUrl() {
        return Helpers.GetServiceUrl(this);
    }

    @Override
    public void onTaskCompleted(List<LogRecord> result) {
        _log.addAll(result);
        _adapter.notifyDataSetChanged();
        _progressDialog.dismiss();
    }

    @Override
    public void onFailure(String message) {
        _progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void LoadLog()
    {
        _progressDialog.show();
        LogService service = new LogService(this);
        service.getLog(_lastDate, 50, _source);
    }
}
