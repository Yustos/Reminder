package ru.yt.reminderreader;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.yt.reminderreader.adapters.RecordListAdapter;
import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.services.OnRecordsReaded;
import ru.yt.reminderreader.services.RecordsService;


public class MainActivity extends ActionBarActivity implements OnRecordsReaded {
    public List<Record> RecordsList = new ArrayList<Record>();

    private RecordListAdapter _adapter;

    private ListView _listViewRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _adapter = new RecordListAdapter(
                this,
                RecordsList);

        _listViewRecords = (ListView) findViewById(R.id.listViewRecords);
        _listViewRecords.setAdapter(_adapter);

        _listViewRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Record record = _adapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("RecordId", record.id);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    //@Override
    public void onTaskCompleted(Record[] result) {
        Log.d("Complete", "it");
        RecordsList.clear();

        RecordsList.addAll(Arrays.asList(result));
        _adapter.notifyDataSetChanged();
    }

    public void ButtonLoadClick(View v)
    {
        RecordsService service = new RecordsService(this);
        service.GetRecords();
    }

    public void ButtonAddClick(View v)
    {
        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
        startActivity(intent);
    }
}
