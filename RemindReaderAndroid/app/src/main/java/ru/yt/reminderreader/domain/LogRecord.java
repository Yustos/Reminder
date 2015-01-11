package ru.yt.reminderreader.domain;

import java.util.Date;

/**
 * Created by Yustos on 11.01.2015.
 */
public class LogRecord {
    private Date date;
    private String client;
    private String message;
    private LogSource source;

    public LogRecord(Date date, String client, String message, LogSource source) {
        this.date = date;
        this.client = client;
        this.message = message;
        this.source = source;
    }

    public Date getDate() {
        return date;
    }

    public String getClient() {
        return client;
    }

    public String getMessage() {
        return message;
    }

    public LogSource getSource() {
        return source;
    }
}
