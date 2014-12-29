package ru.yt.reminderreader;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.services.OnRecordDetailReader;
import ru.yt.reminderreader.services.RecordDetailService;


public class DetailActivity extends ActionBarActivity implements OnRecordDetailReader {
    private TextView _textBoxTitle;
    private TextView _textBoxDate;
    private TextView _textBoxBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        _textBoxTitle = (TextView)findViewById(R.id.textBoxTitle);
        _textBoxDate = (TextView)findViewById(R.id.textBoxDate);
        _textBoxBody = (TextView)findViewById(R.id.textBoxBody);

        Intent intent = getIntent();
        String id = intent.getStringExtra("RecordId");

        RecordDetailService service = new RecordDetailService(this);
        service.GetRecordDetail(id);
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
    public void onTaskCompleted(RecordDetail result) {
        _textBoxTitle.setText(result.title);
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        _textBoxDate.setText(dateFormat.format(result.date));
        _textBoxBody.setText(result.body);
    }

    public void ButtonEditClick(View v)
    {
        String id = getIntent().getStringExtra("RecordId");

        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
        intent.putExtra("RecordId", id);
        startActivity(intent);
    }
}
