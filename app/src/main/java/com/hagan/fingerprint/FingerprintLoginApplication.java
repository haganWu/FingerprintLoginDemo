package com.hagan.fingerprint;

import android.app.Application;
import android.content.Context;

import com.hagan.fingerprint.constants.Constants;
import com.hagan.fingerprint.utils.SPUtil;

public class FingerprintLoginApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SPUtil.init(this, Constants.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
}
