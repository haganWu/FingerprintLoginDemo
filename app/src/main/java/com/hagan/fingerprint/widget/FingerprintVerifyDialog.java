package com.hagan.fingerprint.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hagan.fingerprint.R;


/**
 * @author HaganWu
 * @description
 * @fileName FingerprintVerifyDialog.java
 * @date 2019/1/23-15:11
 */
public class FingerprintVerifyDialog extends Dialog {

    private Context mContext;
    private TextView tv_fingerprint_tip_content;
    private TextView tv_fingerprint_button_cancel;


    public FingerprintVerifyDialog(Context context) {
        super(context, R.style.TransparentDialogStyle);
        setContentView(R.layout.fingerprint_verify_dialog);
        setCanceledOnTouchOutside(false);
        this.mContext = context;
        tv_fingerprint_tip_content = (TextView) findViewById(R.id.tv_fingerprint_tip_content);
        tv_fingerprint_tip_content.setMovementMethod(new ScrollingMovementMethod());
        tv_fingerprint_button_cancel = (TextView) findViewById(R.id.tv_fingerprint_button_cancel);
        windowAnim();
    }


    /**
     * @description 提示文本内容设置
     * @author HaganWu
     * @date 2017/3/20-11:40
     */
    public void setContentText(String content) {
        tv_fingerprint_tip_content.setText(content);
    }

    public void setContentHtmlText(String content) {
        tv_fingerprint_tip_content.setText(Html.fromHtml(content));
    }

    public void setContentTextSize(float size) {
        tv_fingerprint_tip_content.setTextSize(size);
    }

    public void setContentTextColor(int color) {
        tv_fingerprint_tip_content.setTextColor(color);
    }

    public void setCancelTextColor(int color) {
        tv_fingerprint_button_cancel.setTextColor(color);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        this.dismiss();
    }


    public void setOnCancelButtonClickListener(final OnDialogCancelButtonClickListener onDialogSingleConfirmButtonClickListener) {
        tv_fingerprint_button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogSingleConfirmButtonClickListener.onCancelClick(v);
                if (FingerprintVerifyDialog.this.isShowing()) {
                    FingerprintVerifyDialog.this.dismiss();
                }
            }
        });

    }


   /**
    * @description 取消按钮点击监听
    * @author HaganWu
    * @date 2019/1/23-15:35
    */
    public interface OnDialogCancelButtonClickListener {

        /**
         * @description 取消点击回调
         * @author HaganWu
         * @date 2019/1/23-15:33
         */
        void onCancelClick(View v);
    }

    /**
     * @description 设置窗口显示
     * @author HaganWu
     * @date 2019/1/23-15:34
     */
    public void windowAnim() {
        Window window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

}

