package com.hagan.fingerprint.interfaze;

/**
 * @author HaganWu
 * @description 登录完成监听
 * @fileName LoginFinishListener.java
 * @date 2019/1/23-17:24
 */
public interface LoginPageOperationListener {
    /**
     * @description 关闭页面
     * @author HaganWu
     * @date 2019/1/23-17:25
     */
    void onFinish();

    /**
     * @description 密码登录/指纹登录 切换
     * @author HaganWu
     * @date 2019/1/24-10:43
     */
    void changePage(int flag);
}
