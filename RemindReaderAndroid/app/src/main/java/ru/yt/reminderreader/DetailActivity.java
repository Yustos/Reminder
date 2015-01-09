package ru.yt.reminderreader;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;

import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.services.RecordDetailReader;
import ru.yt.reminderreader.services.RecordDetailService;
import ru.yt.reminderreader.services.storage.RecordsStore;


public class DetailActivity extends ActionBarActivity implements RecordDetailReader {
    private TextView _textBoxTitle;
    private TextView _textBoxState;
    private TextView _textBoxDate;
    private TextView _textBoxBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        _textBoxTitle = (TextView)findViewById(R.id.textBoxTitle);
        _textBoxState = (TextView)findViewById(R.id.textBoxState);
        _textBoxDate = (TextView)findViewById(R.id.textBoxDate);
        _textBoxBody = (TextView)findViewById(R.id.textBoxBody);

        Intent intent = getIntent();
        String id = intent.getStringExtra("RecordId");

        RecordsStore store = RecordsStore.get(this);
        RecordDetail record = store.getRecordDetail(id);
        if (record == null)
        {
            Toast.makeText(this, "Load from server", Toast.LENGTH_SHORT).show();
            RecordDetailService service = new RecordDetailService(this);
            service.GetRecordDetail(id);
        }
        else {
            bindRecordDetail(record);
        }

        findViewById(R.id.buttonEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recordId = getIntent().getStringExtra("RecordId");

                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("RecordId", recordId);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            setResult(RESULT_OK);
            if (data != null && data.hasExtra("RecordId"))
            {
                String id = data.getStringExtra("RecordId");
                RecordsStore store = RecordsStore.get(this);
                RecordDetail record = store.getRecordDetail(id);
                bindRecordDetail(record);
            }
            else
            {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
    public String getServiceUrl() {
        return Helpers.GetServiceUrl(this);
    }

    @Override
    public void onTaskCompleted(RecordDetail record) {
        bindRecordDetail(record);

        RecordsStore store = RecordsStore.get(this);
        store.addOrUpdateRecord(record);
        Toast.makeText(this, "Cached", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void bindRecordDetail(RecordDetail record)
    {
        _textBoxTitle.setText(record.title);
        _textBoxState.setText(String.format("%s", record.state));
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        _textBoxDate.setText(dateFormat.format(record.date));
        _textBoxBody.setText(record.body);
    }
}
