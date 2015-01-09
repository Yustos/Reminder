package ru.yt.reminderreader.services;

import java.util.Date;

import ru.yt.reminderreader.domain.Record;
import ru.yt.reminderreader.domain.RecordDetail;
import ru.yt.reminderreader.domain.RecordSync;
import ru.yt.reminderreader.services.storage.RecordsStore;

/**
 * Created by Yustos on 06.01.2015.
 */
public interface SyncReader {
    String getServiceUrl();

    RecordsStore getStore();

    Date getSyncDate();

    void setSyncDate(Date syncDate);

    void onProgress(RecordSync record);

    void onTaskCompleted();

    void onFailure(String message);
}
