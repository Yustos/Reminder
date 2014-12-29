package ru.yt.reminderreader.domain;

import java.util.Date;

/**
 * Created by Yustos on 26.12.2014.
 */
public class RecordDetail extends Record {
    public String body;


    public RecordDetail(String id, Date date, String title, String body) {
        super(id, date, title);
        this.body = body;
    }
}
