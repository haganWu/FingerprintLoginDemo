package com.hagan.fingerprint.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author HaganWu
 * @description MD5工具
 * @fileName MD5Util.java
 * @date 2019/1/29-13:51
 */
public class MD5Util {


    /**
     * @description 密码加密
     * @author HaganWu
     * @date 2019/1/29-13:52
     */
    public static String md5Password(String password) {
        StringBuffer sb = new StringBuffer();
        // 得到一个信息摘要器
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            // 把每一个byte做一个与运算 0xff
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
