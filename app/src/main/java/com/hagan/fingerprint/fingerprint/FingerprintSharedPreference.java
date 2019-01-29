package com.hagan.fingerprint.fingerprint;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class FingerprintSharedPreference {

    final String dataKeyName = "data";
    final String IVKeyName = "IV";
    private SharedPreferences preferences;

    FingerprintSharedPreference(Context context) {
        preferences = context.getSharedPreferences("fingerprint", Activity.MODE_PRIVATE);
    }

    String getData(String keyName) {
        return preferences.getString(keyName, "");
    }

    boolean storeData(String key, String data) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, data);
        return editor.commit();
    }
}
