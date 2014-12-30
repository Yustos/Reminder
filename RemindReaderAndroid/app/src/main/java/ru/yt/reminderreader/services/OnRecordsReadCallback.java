package ru.yt.reminderreader.services;

import ru.yt.reminderreader.domain.Record;

/**
 * Created by Yustos on 25.12.2014.
 */
public interface OnRecordsReadCallback {
    void onTaskCompleted(Record[] result);

    void onFailure(String message);
}
