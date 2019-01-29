package com.hagan.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseFragment extends Fragment {
    protected View rootView;

    /**
     * 是否加载完成
     * 当执行完onViewCreated方法后即为true
     */
    protected boolean mIsImmersion = false;

    private boolean mIsVisible = false;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            mIsVisible = true;

        } else {
            mIsVisible = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 可防止空白页问题
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutResource(), container, false);
        }
        initView(rootView);
        return rootView;
    }

    public boolean onBackPressed() {
        return false;
    }

    //获取布局文件
    protected abstract int getLayoutResource();


    //初始化view
    protected abstract void initView(View view);

    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
