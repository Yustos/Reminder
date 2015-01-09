package ru.yt.reminderreader.domain;

import java.util.Date;

/**
 * Created by Yustos on 26.12.2014.
 */
public class RecordDetail extends Record {
    public String body;


    public RecordDetail(String id, StateType state, Date date, String title, String body) {
        super(id, state, date, title);
        this.body = body;
    }
}
