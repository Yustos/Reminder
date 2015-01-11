package ru.yt.reminderreader.services;

import java.util.List;

import ru.yt.reminderreader.domain.LogRecord;

/**
 * Created by Yustos on 11.01.2015.
 */
public interface LogReader {
    String getServiceUrl();

    void onTaskCompleted(List<LogRecord> result);

    void onFailure(String message);

}
