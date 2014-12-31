package ru.yt.reminderreader;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.services.RecordDetailReader;
import ru.yt.reminderreader.services.RecordDetailService;


public class EditActivity extends ActionBarActivity implements RecordDetailReader {
    private EditText _editTextTitle;
    private EditText _editTextBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        _editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        _editTextBody = (EditText) findViewById(R.id.editTextBody);

        Intent intent = getIntent();
        if (intent.hasExtra("RecordId")) {
            String id = intent.getStringExtra("RecordId");
            intent.putExtra("isLoading", true);
            RecordDetailService service = new RecordDetailService(this);
            service.GetRecordDetail(id);
        }
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

    public void ButtonSaveClick(View v)
    {
        String id = null;
        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra("RecordId"))
        {
            id = currentIntent.getStringExtra("RecordId");
        }

        RecordDetailService service = new RecordDetailService(this);
        RecordDetail record = new RecordDetail(
            id,
            new Date(),
            _editTextTitle.getText().toString(),
            _editTextBody.getText().toString());
        service.SaveRecordDetail(record);
    }

    public void ButtonDeleteClick(View v)
    {
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
        RecordDetailService service = new RecordDetailService(this);
        service.DeleteRecordDetail(id);
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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else if (currentIntent.hasExtra("RecordId") && currentIntent.hasExtra("isLoading"))
        {
            _editTextTitle.setText(result.title);
            _editTextBody.setText(result.body);
            currentIntent.removeExtra("isLoading");
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra("RecordId", result.id);
            startActivity(intent);
        }
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
