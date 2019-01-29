package com.hagan.fingerprint;

import android.os.Bundle;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hagan.fingerprint.constants.Constants;
import com.hagan.fingerprint.fingerprint.FingerprintHelper;
import com.hagan.fingerprint.utils.FingerprintUtil;
import com.hagan.fingerprint.utils.SPUtil;
import com.hagan.fingerprint.widget.CommonTipDialog;
import com.hagan.fingerprint.widget.FingerprintVerifyDialog;

public class HomeActivity extends AppCompatActivity implements FingerprintHelper.SimpleAuthenticationCallback, View.OnClickListener {

    private ImageView iv_fingerprint_login_switch;
    private TextView tv_nonsupport;
    private CommonTipDialog openFingerprintLoginTipDialog;
    private FingerprintHelper helper;
    private FingerprintVerifyDialog fingerprintVerifyDialog;
    private CommonTipDialog fingerprintVerifyErrorTipDialog;
    private CommonTipDialog closeFingerprintTipDialog;
    private boolean isOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        iv_fingerprint_login_switch = findViewById(R.id.iv_fingerprint_login_switch);
        tv_nonsupport = findViewById(R.id.tv_nonsupport);
        iv_fingerprint_login_switch.setOnClickListener(this);
        helper = FingerprintHelper.getInstance();
        helper.init(getApplicationContext());
        helper.setCallback(this);
        if (helper.checkFingerprintAvailable(this) != -1) {
            //设备支持指纹登录
            tv_nonsupport.setVisibility(View.INVISIBLE);
            iv_fingerprint_login_switch.setEnabled(true);
        } else {
            //设备不支持指纹登录
            tv_nonsupport.setVisibility(View.VISIBLE);
            iv_fingerprint_login_switch.setEnabled(false);
        }
        isOpen = SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN);
        setSwitchStatus();

        tv_nonsupport.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.checkFingerprintAvailable(HomeActivity.this) != -1 &&
                        !isOpen) {
                    showOpenFingerprintLoginDialog();
                }
            }
        }, 500);
    }


    private void showOpenFingerprintLoginDialog() {
        if (openFingerprintLoginTipDialog == null) {
            openFingerprintLoginTipDialog = new CommonTipDialog(this);
        }
        openFingerprintLoginTipDialog.setSingleButton(false);
        openFingerprintLoginTipDialog.setContentText("您的设备支持指纹登录,是否现在开启?");
        openFingerprintLoginTipDialog.setOnDialogButtonsClickListener(new CommonTipDialog.OnDialogButtonsClickListener() {
            @Override
            public void onCancelClick(View v) {

            }

            @Override
            public void onConfirmClick(View v) {
                openFingerprintLogin();
            }
        });
        openFingerprintLoginTipDialog.show();
    }

    /**
     * @description 开启指纹登录功能
     * @author HaganWu
     * @date 2019/1/29-10:20
     */
    private void openFingerprintLogin() {
        Log.e("hagan", "openFingerprintLogin");

        helper.generateKey();
        if (fingerprintVerifyDialog == null) {
            fingerprintVerifyDialog = new FingerprintVerifyDialog(this);
        }
        fingerprintVerifyDialog.setContentText("请验证指纹");
        fingerprintVerifyDialog.setOnCancelButtonClickListener(new FingerprintVerifyDialog.OnDialogCancelButtonClickListener() {
            @Override
            public void onCancelClick(View v) {
                helper.stopAuthenticate();
            }
        });
        fingerprintVerifyDialog.show();
        helper.setPurpose(KeyProperties.PURPOSE_ENCRYPT);
        helper.authenticate();
    }

    @Override
    public void onAuthenticationSucceeded(String value) {
        Log.e("hagan", "HomeActivity->onAuthenticationSucceeded-> value:" + value);
        SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, true);
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.dismiss();
            Toast.makeText(this, "指纹登录已开启", Toast.LENGTH_SHORT).show();
            isOpen = true;
            setSwitchStatus();
            saveLocalFingerprintInfo();
        }
    }

    private void saveLocalFingerprintInfo() {
        SPUtil.getInstance().putString(Constants.SP_LOCAL_FINGERPRINT_INFO, FingerprintUtil.getFingerprintInfoString(getApplicationContext()));
    }

    @Override
    public void onAuthenticationFail() {
        Log.e("hagan", "HomeActivity->onAuthenticationFail");
        showFingerprintVerifyErrorInfo("指纹不匹配");
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Log.e("hagan", "HomeActivity->onAuthenticationError-> errorCode:" + errorCode + ",errString:" + errString);
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.dismiss();
        }
        showTipDialog(errorCode, errString.toString());

    }

    private void showTipDialog(int errorCode, CharSequence errString) {
        if (fingerprintVerifyErrorTipDialog == null) {
            fingerprintVerifyErrorTipDialog = new CommonTipDialog(this);
        }
        fingerprintVerifyErrorTipDialog.setContentText("errorCode:" + errorCode + "," + errString);
        fingerprintVerifyErrorTipDialog.setSingleButton(true);
        fingerprintVerifyErrorTipDialog.setOnSingleConfirmButtonClickListener(new CommonTipDialog.OnDialogSingleConfirmButtonClickListener() {
            @Override
            public void onConfirmClick(View v) {
                helper.stopAuthenticate();
            }
        });
        fingerprintVerifyErrorTipDialog.show();
    }


    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Log.e("hagan", "HomeActivity->onAuthenticationHelp-> helpCode:" + helpCode + ",helpString:" + helpString);
        showFingerprintVerifyErrorInfo(helpString.toString());
    }

    private void showFingerprintVerifyErrorInfo(String info) {
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.setContentText(info);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_fingerprint_login_switch:
                isOpen = !isOpen;
                dealOnOff(isOpen);
                break;
        }
    }

    private void dealOnOff(boolean isOpen) {
        if (isOpen) {
            openFingerprintLogin();
        } else {
            showCloseFingerprintTipDialog();
        }
    }

    private void showCloseFingerprintTipDialog() {
        if (closeFingerprintTipDialog == null) {
            closeFingerprintTipDialog = new CommonTipDialog(this);
        }
        closeFingerprintTipDialog.setContentText("确定关闭指纹登录?");
        closeFingerprintTipDialog.setSingleButton(false);
        closeFingerprintTipDialog.setOnDialogButtonsClickListener(new CommonTipDialog.OnDialogButtonsClickListener() {
            @Override
            public void onCancelClick(View v) {

            }

            @Override
            public void onConfirmClick(View v) {
                closeFingerprintLogin();
            }
        });
        closeFingerprintTipDialog.show();
    }

    /**
     * @description 关闭指纹登录功能
     * @author HaganWu
     * @date 2019/1/24-14:41
     */
    private void closeFingerprintLogin() {
        SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, false);
        setSwitchStatus();
        helper.closeAuthenticate();
    }

    private void setSwitchStatus() {
        iv_fingerprint_login_switch.setImageResource(isOpen ? R.mipmap.switch_open_icon : R.mipmap.switch_close_icon);
    }
}
