package com.hagan.fingerprint;

import android.content.Intent;
import android.graphics.Color;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hagan.fingerprint.constants.Constants;
import com.hagan.fingerprint.fingerprint.FingerprintHelper;
import com.hagan.fingerprint.interfaze.LoginPageOperationListener;
import com.hagan.fingerprint.utils.FingerprintUtil;
import com.hagan.fingerprint.utils.SPUtil;
import com.hagan.fingerprint.widget.CommonTipDialog;
import com.hagan.fingerprint.widget.FingerprintVerifyDialog;
import com.hagan.fingerprint.widget.PromptButton;
import com.hagan.fingerprint.widget.PromptDialog;
import com.hagan.fingerprint.widget.listener.PromptButtonListener;

/**
 * @author HaganWu
 * @description 指纹登录页面
 * @fileName FingerprintLoginFragment.java
 * @date 2019/1/23-17:49
 */
public class FingerprintLoginFragment extends BaseFragment implements View.OnClickListener, FingerprintHelper.SimpleAuthenticationCallback {

    private LoginPageOperationListener loginPageOperationListener;
    private TextView tv_account_info;
    private PromptDialog promptDialog;
    private final int LOGOUT_ID = 0x0001;
    private final int SMS_LOGIN_ID = 0x0002;
    private final int PASSWORD_ID = 0x0003;
    private FingerprintHelper helper;
    private FingerprintVerifyDialog fingerprintVerifyDialog;
    private CommonTipDialog errorTipDialog;
    private CommonTipDialog fingerprintChangeTipDialog;
    private ImageView iv_fingerprint_login;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_fingerprint_login;
    }

    @Override
    protected void initView(View view) {
        tv_account_info = view.findViewById(R.id.tv_account_info);
        iv_fingerprint_login = view.findViewById(R.id.iv_fingerprint_login);
        iv_fingerprint_login.setEnabled(true);
        String account = SPUtil.getInstance().getString(Constants.SP_ACCOUNT);
        if (!TextUtils.isEmpty(account)) {
            if (account.length() == 11) {
                account = account.substring(0, 3) + "****" + account.substring(7, account.length());
            }
            tv_account_info.setText(String.format(getString(R.string.fingerprint_account), account));
        }
        view.findViewById(R.id.tv_fingerprint_login_more).setOnClickListener(this);
        iv_fingerprint_login.setOnClickListener(this);
        helper = FingerprintHelper.getInstance();
        helper.init(getContext());
        helper.setCallback(this);
        tv_account_info.postDelayed(new Runnable() {
            @Override
            public void run() {
                openFingerprintLogin();
            }
        }, 500);

    }

    private void openFingerprintLogin() {
        Log.e("hagan", "FingerprintLoginFragment->openFingerprintLogin");

        //验证指纹库信息是否发生变化
        if (FingerprintUtil.isLocalFingerprintInfoChange(getContext())) {
            //指纹库信息发生变化
            showFingerprintChangeTipDialog();
            return;
        }

        if (fingerprintVerifyDialog == null) {
            fingerprintVerifyDialog = new FingerprintVerifyDialog(getActivity());
        }
        fingerprintVerifyDialog.setContentText("请验证指纹");
        fingerprintVerifyDialog.setOnCancelButtonClickListener(new FingerprintVerifyDialog.OnDialogCancelButtonClickListener() {
            @Override
            public void onCancelClick(View v) {
                helper.stopAuthenticate();
            }
        });
        fingerprintVerifyDialog.show();
        helper.setPurpose(KeyProperties.PURPOSE_DECRYPT);
        helper.authenticate();
    }

    private void showFingerprintChangeTipDialog() {
        if (fingerprintChangeTipDialog == null) {
            fingerprintChangeTipDialog = new CommonTipDialog(getActivity());
        }
        fingerprintChangeTipDialog.setContentText(getResources().getString(R.string.fingerprintChangeTip));
        fingerprintChangeTipDialog.setSingleButton(true);
        fingerprintChangeTipDialog.setOnSingleConfirmButtonClickListener(new CommonTipDialog.OnDialogSingleConfirmButtonClickListener() {
            @Override
            public void onConfirmClick(View v) {
                iv_fingerprint_login.setEnabled(false);
                SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, false);
                helper.closeAuthenticate();
            }
        });
        fingerprintChangeTipDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_fingerprint_login_more:
                moreClick();
                break;
            case R.id.iv_fingerprint_login:
                openFingerprintLogin();
                break;
        }
    }

    /**
     * @description 更多点击(切换到密码登录)
     * @author HaganWu
     * @date 2019/1/24-11:00
     */
    private void moreClick() {
        showMoreDialog();
    }

    private void showMoreDialog() {
        if (promptDialog == null) {
            promptDialog = new PromptDialog(getActivity());
        }
        PromptButton cancel = new PromptButton("取消", null);
        cancel.setTextColor(Color.parseColor("#0076ff"));
        PromptButtonListener promptButtonListener = new PromptButtonListener() {
            @Override
            public void onClick(PromptButton button) {
                switch (button.getId()) {
                    case LOGOUT_ID:
                        Toast.makeText(getActivity(), "切换/注册账号", Toast.LENGTH_SHORT).show();
                        break;
                    case SMS_LOGIN_ID:
                        Toast.makeText(getActivity(), "短信安全登录", Toast.LENGTH_SHORT).show();
                        break;
                    case PASSWORD_ID:
                        if (loginPageOperationListener != null) {
                            loginPageOperationListener.changePage(MainActivity.PASSWORD_LOGIN_FLAG);
                        }
                        break;
                }
            }
        };
        promptDialog.showAlertSheet("", true, cancel,
                new PromptButton(LOGOUT_ID, "切换/注册账号", promptButtonListener),
                new PromptButton(SMS_LOGIN_ID, "短信安全登录", promptButtonListener),
                new PromptButton(PASSWORD_ID, "密码登录", promptButtonListener));
    }

    public void setLoginPageOperationListener(LoginPageOperationListener loginPageOperationListener) {
        this.loginPageOperationListener = loginPageOperationListener;
    }

    @Override
    public void onAuthenticationSucceeded(String value) {
        Log.e("hagan", "FingerprintLoginFragment->onAuthenticationSucceeded-> value:" + value);
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.dismiss();
        }
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        if (loginPageOperationListener != null) {
            loginPageOperationListener.onFinish();
        }
    }


    @Override
    public void onAuthenticationFail() {
        Log.e("hagan", "FingerprintLoginFragment->onAuthenticationFail");
        showFingerprintVerifyErrorInfo("指纹不匹配");
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Log.e("hagan", "FingerprintLoginFragment->onAuthenticationSucceeded-> errString:" + errString.toString());
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.dismiss();
        }
        showTipDialog(errorCode, errString.toString());
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Log.e("hagan", "FingerprintLoginFragment->onAuthenticationHelp-> helpString:" + helpString);
        showFingerprintVerifyErrorInfo(helpString.toString());
    }

    private void showTipDialog(int errorCode, CharSequence errString) {
        if (errorTipDialog == null) {
            errorTipDialog = new CommonTipDialog(getActivity());
        }
        errorTipDialog.setContentText("errorCode:" + errorCode + "," + errString);
        errorTipDialog.setSingleButton(true);
        errorTipDialog.setOnSingleConfirmButtonClickListener(new CommonTipDialog.OnDialogSingleConfirmButtonClickListener() {
            @Override
            public void onConfirmClick(View v) {
                helper.stopAuthenticate();
            }
        });
        errorTipDialog.show();
    }

    private void showFingerprintVerifyErrorInfo(String info) {
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.setContentText(info);
        }
    }
}
