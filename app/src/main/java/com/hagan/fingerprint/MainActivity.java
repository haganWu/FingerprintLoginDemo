package com.hagan.fingerprint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.hagan.fingerprint.constants.Constants;
import com.hagan.fingerprint.interfaze.LoginPageOperationListener;
import com.hagan.fingerprint.utils.SPUtil;

public class MainActivity extends AppCompatActivity implements LoginPageOperationListener {
    //密码登录页面
    private PasswordLoginFragment passwordLoginFragment;
    //指纹登录页面
    private FingerprintLoginFragment fingerprintLoginFragment;
    private Fragment currentFragment;
    public static final int PASSWORD_LOGIN_FLAG = 0x0004;
    public static final int FINGERPRINT_LOGIN_FLAG = 0x0005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initChildFragment();

        if (SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN)) {
            switchFragment(fingerprintLoginFragment).commit();
        } else {
            switchFragment(passwordLoginFragment).commit();
        }

    }


    private void initChildFragment() {
        passwordLoginFragment = new PasswordLoginFragment();
        passwordLoginFragment.setLoginPageOperationListener(this);
        fingerprintLoginFragment = new FingerprintLoginFragment();
        fingerprintLoginFragment.setLoginPageOperationListener(this);
    }

    private FragmentTransaction switchFragment(Fragment targetFragment) {

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.home_container, targetFragment, targetFragment.getClass().getName());

        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }

    @Override
    public void onFinish() {
        finish();
    }

    @Override
    public void changePage(int flag) {
        if (flag == FINGERPRINT_LOGIN_FLAG) {
            switchFragment(fingerprintLoginFragment).commit();
        } else if (flag == PASSWORD_LOGIN_FLAG)
            switchFragment(passwordLoginFragment).commit();
    }
}
