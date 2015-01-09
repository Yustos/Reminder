package ru.yt.reminderreader;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.domain.StateType;
import ru.yt.reminderreader.services.RecordDetailReader;
import ru.yt.reminderreader.services.RecordDetailService;
import ru.yt.reminderreader.services.storage.RecordsStore;


public class EditActivity extends ActionBarActivity implements RecordDetailReader {
    private EditText _editTextTitle;
    private EditText _editTextBody;
    private TextView _viewTextDate;
    private TextView _viewTextState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        _editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        _editTextBody = (EditText) findViewById(R.id.editTextBody);
        _viewTextDate = (TextView)findViewById(R.id.textViewDate);
        _viewTextState = (TextView)findViewById(R.id.textViewState);

        Intent intent = getIntent();
        if (intent.hasExtra("RecordId")) {
            String id = intent.getStringExtra("RecordId");
            intent.putExtra("isLoading", true);
            //RecordDetailService service = new RecordDetailService(this);
            //service.GetRecordDetail(id);
            RecordsStore store = RecordsStore.get(EditActivity.this);
            RecordDetail record = store.getRecordDetail(id);
            bindRecordDetail(record);
        }

        findViewById(R.id.buttonDelete).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String id;
                Intent currentIntent = getIntent();
                if (currentIntent.hasExtra("RecordId"))
                {
                    id = currentIntent.getStringExtra("RecordId");
                }
                else
                {
                    return;
                }
                currentIntent.putExtra("idDeleting", true);
                RecordsStore store = RecordsStore.get(EditActivity.this);
                store.markAsDeleted(id);
                setResult(RESULT_OK);
                finish();
                //store.delete(id);
                //RecordDetailService service = new RecordDetailService(EditActivity.this);
                //service.DeleteRecordDetail(id);
            }
        });

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String id;
                StateType state;
                Intent currentIntent = getIntent();
                if (currentIntent.hasExtra("RecordId"))
                {
                    id = currentIntent.getStringExtra("RecordId");
                    state = StateType.Modified;
                }
                else
                {
                    id = UUID.randomUUID().toString().replace("-", "");
                    state = StateType.Added;
                }

                RecordDetail record = new RecordDetail(
                        id,
                        state,
                        new Date(),
                        _editTextTitle.getText().toString(),
                        _editTextBody.getText().toString());

                RecordsStore store = RecordsStore.get(EditActivity.this);
                store.addOrUpdateRecord(record);

                //RecordDetailService service = new RecordDetailService(EditActivity.this);
                //service.SaveRecordDetail(record);

                Intent result = new Intent();
                result.putExtra("RecordId", id);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
    public void onTaskCompleted(RecordDetail result) {
        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra("idDeleting"))
        {
            currentIntent.removeExtra("idDeleting");
            finish();
        }
        else if (currentIntent.hasExtra("RecordId") && currentIntent.hasExtra("isLoading"))
        {
            bindRecordDetail(result);
            currentIntent.removeExtra("isLoading");
        }
        else
        {
            finish();
        }
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void bindRecordDetail(RecordDetail record)
    {
        _editTextTitle.setText(record.title);
        _viewTextState.setText(String.format("%s", record.state));
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        _viewTextDate.setText(dateFormat.format(record.date));
        _editTextBody.setText(record.body);
    }
}
