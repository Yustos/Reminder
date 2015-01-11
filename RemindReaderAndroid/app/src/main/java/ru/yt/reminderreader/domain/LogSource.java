package ru.yt.reminderreader.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yustos on 11.01.2015.
 */
public enum LogSource {
    Unknown(0), Api(1);

    private int numVal;

    LogSource(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }

    private static final Map<Integer, LogSource> intToTypeMap = new HashMap<>();
    static {
        for (LogSource type : LogSource.values()) {
            intToTypeMap.put(type.numVal, type);
        }
    }

    public static LogSource fromInt(int i) {
        LogSource type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null)
            return LogSource.Unknown;
        return type;
    }
}
