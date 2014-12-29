package ru.yt.reminderreader.services;

import ru.yt.reminderreader.domain.RecordDetail;

/**
 * Created by Yustos on 25.12.2014.
 */
public interface OnRecordDetailReader {
    void onTaskCompleted(RecordDetail result);
}
