package com.hagan.fingerprint.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SPUtil {

    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    public Context mContext;

    private static SPUtil instance = null;

    public static SPUtil getInstance() {
        if (instance == null) {
            synchronized (SPUtil.class) {
                if (instance == null) {
                    instance = new SPUtil();
                }
            }
        }
        return instance;
    }


    public static void init(Context context, String prefsname, int mode) {
        instance = getInstance();
        instance.mContext = context;
        instance.prefs = instance.mContext.getSharedPreferences(prefsname, mode);
        instance.editor = instance.prefs.edit();
    }

    private SPUtil() {
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return this.prefs.getBoolean(key, defaultVal);
    }

    public boolean getBoolean(String key) {
        return this.prefs.getBoolean(key, false);
    }

    public String getString(String key, String defaultVal) {
        return this.prefs.getString(key, defaultVal);
    }

    public String getString(String key) {
        return this.prefs.getString(key, null);
    }

    public int getInt(String key, int defaultVal) {
        return this.prefs.getInt(key, defaultVal);
    }

    public int getInt(String key) {
        return this.prefs.getInt(key, 0);
    }

    public float getFloat(String key, float defaultVal) {
        return this.prefs.getFloat(key, defaultVal);
    }

    public float getFloat(String key) {
        return this.prefs.getFloat(key, 0f);
    }

    public long getLong(String key, long defaultVal) {
        return this.prefs.getLong(key, defaultVal);
    }

    public long getLong(String key) {
        return this.prefs.getLong(key, 0L);
    }


    public SPUtil putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
        return this;
    }

    public SPUtil putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
        return this;
    }

    public SPUtil putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
        return this;
    }

    public SPUtil putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
        return this;
    }

    public SPUtil putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
        return this;
    }

    public SPUtil putStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value);
        editor.commit();
        return this;
    }

    public Set<String> getStringSet(String key) {
        return this.prefs.getStringSet(key, null);
    }


}
