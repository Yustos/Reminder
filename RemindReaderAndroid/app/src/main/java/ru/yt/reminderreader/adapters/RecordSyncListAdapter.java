package ru.yt.reminderreader.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import ru.yt.reminderreader.R;
import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.RecordSync;

/**
 * Created by Yustos on 07.01.2015.
 */
public class RecordSyncListAdapter extends ArrayAdapter<RecordSync> {
    private final Context context;
    private final List<RecordSync> items;

    public RecordSyncListAdapter(Context context, List<RecordSync> items) {
        super(context, R.layout.record_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.record_sync_item, parent, false);
        RecordSync o = items.get(position);
        if (o != null) {
            TextView ttTitle = (TextView) v.findViewById(R.id.labelTitle);
            if (ttTitle != null) {
                ttTitle.setText(o.title);
            }

            TextView ttDate = (TextView) v.findViewById(R.id.labelDate);
            if (ttDate != null) {
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                ttDate.setText(dateFormat.format(o.date));
            }

            TextView ttState = (TextView)v.findViewById(R.id.labelState);
            if (ttState != null)
            {
                ttState.setText(String.format("%s", o.state));
            }

            TextView tvIsSuccess = (TextView)v.findViewById(R.id.labelIsSuccess);
            tvIsSuccess.setText(String.format("%s", o.getIsSuccess()));
            tvIsSuccess.setTextColor(context.getResources().getColor(o.getIsSuccess()
                    ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));

            TextView tvError = (TextView)v.findViewById(R.id.labelError);
            tvError.setText(o.getError());
        }
        return v;
    }
}
