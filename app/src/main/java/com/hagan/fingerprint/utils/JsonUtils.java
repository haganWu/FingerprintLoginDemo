package com.hagan.fingerprint.utils;


import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * @author HaganWu
 * @description json解析
 * @fileName JsonUtils.java
 * @date 2019/1/29-13:54
 */
public class JsonUtils {

    private static Gson gson = new Gson();

    private JsonUtils() {
    }

    /**
     * @description 转化后的JSON串
     * @author HaganWu
     * @date 2019/1/29-13:54
     */
    public static String toJson(Object src) {
        if (null == src) {
            return gson.toJson(JsonNull.INSTANCE);
        }
        try {
            return gson.toJson(src);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @description 用来将JSON串转为对象，此方法可用来转带泛型的集合
     * @author HaganWu
     * @date 2019/1/29-13:55
     */
    public static Object fromJson(String json, Type typeOfT) {
        try {
            return gson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


}
