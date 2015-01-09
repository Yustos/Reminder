package ru.yt.reminderreader;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import java.util.Date;

/**
 * Created by Yustos on 07.01.2015.
 */
public class LongEditTextPreference extends EditTextPreference {

    public LongEditTextPreference(Context context) {
        super(context);
    }

    public LongEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LongEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedLong(new Date().getTime()));
    }

    @Override
    protected boolean persistString(String value) {
        return persistLong(Long.valueOf(value));
    }
}
