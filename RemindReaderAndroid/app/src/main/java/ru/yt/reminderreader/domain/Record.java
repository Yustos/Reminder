package ru.yt.reminderreader.domain;

import java.util.Date;

/**
 * Created by Yustos on 25.12.2014.
 */
public class Record {
    public String id;
    public Date date;
    public String title;

    public Record(String id, Date date, String title) {
        this.id = id;
        this.date = date;
        this.title = title;
    }
}
