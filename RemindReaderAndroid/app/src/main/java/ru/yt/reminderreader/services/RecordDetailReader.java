package ru.yt.reminderreader.services;

import ru.yt.reminderreader.domain.RecordDetail;

/**
 * Created by Yustos on 25.12.2014.
 */
public interface RecordDetailReader {
    String getServiceUrl();

    void onTaskCompleted(RecordDetail result);

    void onFailure(String message);
}
