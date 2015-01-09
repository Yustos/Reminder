package ru.yt.reminderreader.domain;

import java.util.Date;

/**
 * Created by Yustos on 25.12.2014.
 */
public class Record {
    public String id;
    public StateType state;
    public Date date;
    public String title;

    public Record(String id, StateType state, Date date, String title) {
        this.id = id;
        this.state = state;
        this.date = date;
        this.title = title;
    }
}
