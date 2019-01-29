package com.hagan.fingerprint.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.hagan.fingerprint.constants.Constants;
import com.hagan.fingerprint.utils.SPUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;


public class FingerprintHelper extends FingerprintManager.AuthenticationCallback {

    private FingerprintManager manager;
    private CancellationSignal mCancellationSignal;
    private SimpleAuthenticationCallback callback;
    private FingerprintSharedPreference mLocalSharedPreference;
    private FingerprintAndroidKeyStore mLocalAndroidKeyStore;
    //PURPOSE_ENCRYPT,则表示生成token，否则为取出token
    private int purpose = KeyProperties.PURPOSE_ENCRYPT;
    private static FingerprintHelper instance = null;


    private FingerprintHelper() {
    }

    public static FingerprintHelper getInstance() {
        if (instance == null) {
            synchronized (FingerprintHelper.class) {
                if (instance == null) {
                    instance = new FingerprintHelper();
                }
            }
        }
        return instance;
    }


    public void init(Context context) {
        if (manager == null) {
            manager = context.getSystemService(FingerprintManager.class);
        }
        if (mLocalSharedPreference == null) {
            mLocalSharedPreference = new FingerprintSharedPreference(context);
        }
        if (mLocalAndroidKeyStore == null) {
            mLocalAndroidKeyStore = new FingerprintAndroidKeyStore();
        }
    }

    public void generateKey() {
        //在keystore中生成加密密钥
        mLocalAndroidKeyStore.generateKey(FingerprintAndroidKeyStore.keyName);
        setPurpose(KeyProperties.PURPOSE_ENCRYPT);
    }

    public boolean isKeyProtectedEnforcedBySecureHardware() {
        return mLocalAndroidKeyStore.isKeyProtectedEnforcedBySecureHardware();
    }

    /**
     * @description 0 支持指纹但是没有录入指纹； 1：有可用指纹； -1，手机不支持指纹
     * @author HaganWu
     * @date 2019/1/24-13:58
     */
    public int checkFingerprintAvailable(Context ctx) {
        if (!isKeyProtectedEnforcedBySecureHardware()) {
            return -1;
        } else if (!manager.isHardwareDetected()) {
            Toast.makeText(ctx, "该设备尚不支持指纹认证", Toast.LENGTH_SHORT).show();
            return -1;
        } else if (!manager.hasEnrolledFingerprints()) {
            Toast.makeText(ctx, "该设备未录入指纹，请去系统->设置中添加指纹", Toast.LENGTH_SHORT).show();
            return 0;
        }
        return 1;
    }


    public void closeAuthenticate() {
        mLocalSharedPreference.storeData(mLocalSharedPreference.dataKeyName, "");
        mLocalSharedPreference.storeData(mLocalSharedPreference.IVKeyName, "");
    }

    public void setCallback(SimpleAuthenticationCallback callback) {
        this.callback = callback;
    }

    public void setPurpose(int purpose) {
        this.purpose = purpose;
    }

    public boolean authenticate() {
        try {
            FingerprintManager.CryptoObject object;
            if (purpose == KeyProperties.PURPOSE_DECRYPT) {
                String IV = mLocalSharedPreference.getData(mLocalSharedPreference.IVKeyName);
                object = mLocalAndroidKeyStore.getCryptoObject(Cipher.DECRYPT_MODE, Base64.decode(IV, Base64.URL_SAFE));
                if (object == null) {
                    return false;
                }
            } else {
                object = mLocalAndroidKeyStore.getCryptoObject(Cipher.ENCRYPT_MODE, null);
            }
            mCancellationSignal = new CancellationSignal();
            manager.authenticate(object, mCancellationSignal, 0, this, null);
            return true;
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stopAuthenticate() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        if (callback == null) {
            return;
        }
        if (result.getCryptoObject() == null) {
            callback.onAuthenticationFail();
            return;
        }
        final Cipher cipher = result.getCryptoObject().getCipher();
        if (purpose == KeyProperties.PURPOSE_DECRYPT) {
            //取出secret key并返回
            String data = mLocalSharedPreference.getData(mLocalSharedPreference.dataKeyName);
            if (TextUtils.isEmpty(data)) {
                callback.onAuthenticationFail();
                return;
            }
            try {
                byte[] decrypted = cipher.doFinal(Base64.decode(data, Base64.URL_SAFE));
                callback.onAuthenticationSucceeded(new String(decrypted));
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
                callback.onAuthenticationFail();
            }
        } else {
            //将账号密码包装成secret key，存入沙盒
            try {
                String ap = SPUtil.getInstance().getString(Constants.SP_A_P);
                byte[] encrypted = cipher.doFinal(ap.getBytes());
                byte[] IV = cipher.getIV();
                String se = Base64.encodeToString(encrypted, Base64.URL_SAFE);
                String siv = Base64.encodeToString(IV, Base64.URL_SAFE);
                if (mLocalSharedPreference.storeData(mLocalSharedPreference.dataKeyName, se) &&
                        mLocalSharedPreference.storeData(mLocalSharedPreference.IVKeyName, siv)) {
                    callback.onAuthenticationSucceeded(se);
                } else {
                    callback.onAuthenticationFail();
                }
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
                callback.onAuthenticationFail();
            }
        }
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Log.e("hagan", "onAuthenticationError-> errorCode:" + errorCode + ",errString:" + errString);
        if (callback != null) {
            callback.onAuthenticationError(errorCode, errString);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Log.e("hagan", "onAuthenticationHelp->helpCode:>" + helpCode + ",helpString:" + helpString.toString());
        if (callback != null) {
            callback.onAuthenticationHelp(helpCode, helpString);
        }
    }

    @Override
    public void onAuthenticationFailed() {
        Log.e("hagan", "onAuthenticationFailed->" + "onAuthenticationFailed");
        if (callback != null) {
            callback.onAuthenticationFail();
        }
    }

    public interface SimpleAuthenticationCallback {
        void onAuthenticationSucceeded(String value);

        void onAuthenticationFail();

        void onAuthenticationError(int errorCode, CharSequence errString);

        void onAuthenticationHelp(int helpCode, CharSequence helpString);
    }

}
