package ru.yt.reminderreader.services;

/**
 * Created by Yustos on 26.12.2014.
 */
public interface DataReceiver {
    void onDataReceived(String result);

    void onFailure(String message);
}
