package ru.yt.reminderreader.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yustos on 04.01.2015.
 */
public enum StateType {
    Server(-1), Unknown(0), Normal(1), Modified(2), Added(3), Deleted(4);

    private int numVal;

    StateType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }

    private static final Map<Integer, StateType> intToTypeMap = new HashMap<Integer, StateType>();
    static {
        for (StateType type : StateType.values()) {
            intToTypeMap.put(type.numVal, type);
        }
    }

    public static StateType fromInt(int i) {
        StateType type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null)
            return StateType.Unknown;
        return type;
    }
}
