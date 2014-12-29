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
import ru.yt.reminderreader.domain.Record;

/**
 * Created by Yustos on 25.12.2014.
 */
public class RecordListAdapter extends ArrayAdapter<Record> {
    private final Context context;
    private final List<Record> items;

    public RecordListAdapter(Context context, List<Record> items) {
        super(context, R.layout.record_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.record_item, parent, false);

        //View v = convertView;
        //if (v == null) {
        //    LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //    v = vi.inflate(android.R.layout.simple_list_item_activated_1, null);
        //}
        Record o = items.get(position);
        if (o != null) {
            TextView tt = (TextView) v.findViewById(R.id.label);
            if (tt != null) {
                tt.setText(o.title);
            }

            TextView ttDate = (TextView) v.findViewById(R.id.labelDate);
            if (ttDate != null) {
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                ttDate.setText(dateFormat.format(o.date));
            }
        }
        return v;
    }
}
