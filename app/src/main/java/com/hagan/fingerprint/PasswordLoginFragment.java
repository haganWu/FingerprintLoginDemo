package com.hagan.fingerprint;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hagan.fingerprint.constants.Constants;
import com.hagan.fingerprint.interfaze.LoginPageOperationListener;
import com.hagan.fingerprint.utils.MD5Util;
import com.hagan.fingerprint.utils.SPUtil;
import com.hagan.fingerprint.widget.PromptButton;
import com.hagan.fingerprint.widget.PromptDialog;
import com.hagan.fingerprint.widget.listener.PromptButtonListener;

public class PasswordLoginFragment extends BaseFragment implements View.OnClickListener {

    private EditText et_account, et_password;
    private LoginPageOperationListener loginPageOperationListener;
    private PromptDialog promptDialog;
    private final int LOGOUT_ID = 0x0001;
    private final int SMS_LOGIN_ID = 0x0002;
    private final int FINGERPRINT_ID = 0x0003;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_password_login;
    }

    @Override
    protected void initView(View view) {
        et_account = view.findViewById(R.id.et_account);
        et_password = view.findViewById(R.id.et_password);
        view.findViewById(R.id.tv_login).setOnClickListener(this);
        view.findViewById(R.id.tv_password_login_more).setOnClickListener(this);
        String account = SPUtil.getInstance().getString(Constants.SP_ACCOUNT);
        if (!TextUtils.isEmpty(account)) {
            if (account.length() == 11) {
                account = account.substring(0, 3) + "****" + account.substring(7, account.length());
            }
            et_account.setText(account);
            et_password.requestFocus();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                loginClick();
                break;
            case R.id.tv_password_login_more:
                moreClick();
                break;
        }
    }

    /**
     * @description 更多点击(切换到指纹登录)
     * @author HaganWu
     * @date 2019/1/24-9:33
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
                    case FINGERPRINT_ID:
                        if (loginPageOperationListener != null) {
                            loginPageOperationListener.changePage(MainActivity.FINGERPRINT_LOGIN_FLAG);
                        }
                        break;
                }
            }
        };
        if (SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN)) {
            promptDialog.showAlertSheet("", true, cancel,
                    new PromptButton(LOGOUT_ID, "切换/注册账号", promptButtonListener),
                    new PromptButton(SMS_LOGIN_ID, "短信安全登录", promptButtonListener),
                    new PromptButton(FINGERPRINT_ID, "指纹登录", promptButtonListener));
        } else {
            promptDialog.showAlertSheet("", true, cancel,
                    new PromptButton(LOGOUT_ID, "切换/注册账号", promptButtonListener), new PromptButton(SMS_LOGIN_ID, "短信安全登录", promptButtonListener));
        }
    }

    private void loginClick() {
        if (checkLegal()) {
            String account = et_account.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            StringBuffer stringBuffer = new StringBuffer();
            //本地存储账号用户指纹登录时显示账号信息
            SPUtil.getInstance().putString(Constants.SP_ACCOUNT, account);
            stringBuffer.append(account);
            stringBuffer.append(password);
            SPUtil.getInstance().putString(Constants.SP_A_P, MD5Util.md5Password(stringBuffer.toString()));
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            if (loginPageOperationListener != null) {
                loginPageOperationListener.onFinish();
            }
        }
    }

    private boolean checkLegal() {
        if (TextUtils.isEmpty(et_account.getText().toString().trim()) ||
                TextUtils.isEmpty(et_password.getText().toString().trim())) {
            Toast.makeText(getActivity(), "账号密码不能为空", Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }

    public void setLoginPageOperationListener(LoginPageOperationListener loginPageOperationListener) {
        this.loginPageOperationListener = loginPageOperationListener;
    }
}
