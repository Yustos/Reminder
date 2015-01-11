package ru.yt.reminderreader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import ru.yt.reminderreader.R;
import ru.yt.reminderreader.domain.LogRecord;
import ru.yt.reminderreader.domain.Record;

/**
 * Created by Yustos on 11.01.2015.
 */
public class LogListAdapter  extends ArrayAdapter<LogRecord> {
    private final Context context;
    private final List<LogRecord> items;

    public LogListAdapter(Context context, List<LogRecord> items) {
        super(context, R.layout.log_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.log_item, parent, false);
        LogRecord o = items.get(position);
        if (o != null) {
            TextView ttDate = (TextView) v.findViewById(R.id.labelDate);
            if (ttDate != null) {
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                ttDate.setText(dateFormat.format(o.getDate()));
            }

            TextView ttSource = (TextView)v.findViewById(R.id.labelSource);
            if (ttSource != null)
            {
                ttSource.setText(String.format("%s", o.getSource()));
            }

            TextView ttClient = (TextView) v.findViewById(R.id.labelClient);
            if (ttClient != null) {
                ttClient.setText(o.getClient());
            }

            TextView ttMessage = (TextView) v.findViewById(R.id.labelMessage);
            if (ttMessage != null) {
                ttMessage.setText(o.getMessage());
            }
        }
        return v;
    }
}
