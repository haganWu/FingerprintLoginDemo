package com.hagan.fingerprint.utils;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.text.TextUtils;
import android.util.Log;

import com.hagan.fingerprint.bean.FingerprintBean;
import com.hagan.fingerprint.constants.Constants;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.util.List;

public class FingerprintUtil {

    /**
     * @description 返回最新的指纹信息(根据jsonStr转换成自定义FingerprintBean集合)
     * @author HaganWu
     * @date 2019/1/29-13:35
     */
    public static List<FingerprintBean> getFingerprintInfo(Context context) {
        String objStr = getFingerprintInfoString(context);
        if (!TextUtils.isEmpty(objStr)) {
            return (List<FingerprintBean>) JsonUtils.fromJson(objStr, new TypeToken<List<FingerprintBean>>() {
            }.getType());
        }
        return null;
    }

    /**
     * @description 实时获取最新的指纹信息封装成json String
     * @author HaganWu
     * @date 2019/1/29-13:35
     */
    public static String getFingerprintInfoString(Context context) {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        try {
            Class clz = Class.forName("android.hardware.fingerprint.FingerprintManager");
            Method method = clz.getDeclaredMethod("getEnrolledFingerprints", new Class[]{});
            method.setAccessible(true);
            Object obj = method.invoke(fingerprintManager, null);
            if (obj != null) {
                Log.e("hagan", "objStr:" + JsonUtils.toJson(obj));
                return JsonUtils.toJson(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public static boolean isLocalFingerprintInfoChange(Context context) {
        //最新的指纹库信息
        List<FingerprintBean> latestFingerprintInfo = getFingerprintInfo(context);
        //保存在本地的指纹库信息jsonString
        String localFingerprintInfoString = SPUtil.getInstance().getString(Constants.SP_LOCAL_FINGERPRINT_INFO, "");
        if (latestFingerprintInfo == null || TextUtils.isEmpty(localFingerprintInfoString)) {
            //最新指纹库为空或者本地保存的指纹信息为空时,须关闭指纹登录功能后重新录入开启
            return true;
        }
        //保存在本地的指纹库信息
        List<FingerprintBean> localFingerprintInfo = (List<FingerprintBean>) JsonUtils.fromJson(localFingerprintInfoString, new TypeToken<List<FingerprintBean>>() {
        }.getType());
        if (localFingerprintInfo == null) {
            //解析失败,须关闭指纹登录功能后重新录入开启
            return true;
        }
        if (localFingerprintInfo.size() != latestFingerprintInfo.size()) {
            //指纹数量发生变化,须关闭指纹登录功能后重新录入开启
            return true;
        }
        for (int i = 0; i < localFingerprintInfo.size(); i++) {
            if (!localFingerprintInfo.get(i).equals(latestFingerprintInfo.get(i))) {
                //对应指纹id发生变化,须关闭指纹登录功能后重新录入开启
                Log.e("hagan", i + "->localFingerprintInfo:" + localFingerprintInfo.get(i).toString());
                Log.e("hagan", i + "->latestFingerprintInfo:" + latestFingerprintInfo.get(i).toString());
                return true;
            }

        }
        return false;
    }
}
