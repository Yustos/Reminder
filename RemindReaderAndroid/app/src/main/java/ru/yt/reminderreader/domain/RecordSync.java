package ru.yt.reminderreader.domain;

import java.util.Date;

/**
 * Created by Yustos on 07.01.2015.
 */
public class RecordSync extends Record {
    private final Boolean isSuccess;
    private String error;

    public RecordSync(Record record, Boolean isSuccess) {
        super(record.id, record.state, record.date, record.title);
        this.isSuccess = isSuccess;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
