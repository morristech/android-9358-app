package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.technician.R;


/**
 * 登录失败后的提示对话框
 * Created by SD_ZR on 15-6-2.
 */
public class LoginFailDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private CharSequence mTitle;
    private CharSequence mSubTitle;
    private CharSequence mConfirmButtonTxt;
    private View.OnClickListener mOnClickListener;

    /**
     * modify by ruth on 2015-6-2
     *
     * @param context
     */
    public LoginFailDialog(Context context) {
        this(context, R.style.default_dialog_style);
    }

    public LoginFailDialog(Context context, CharSequence title, CharSequence subTitle) {
        this(context, R.style.default_dialog_style);
        mTitle = title;
        mSubTitle = subTitle;
    }

    public LoginFailDialog(Context context, int restitle, int resSubTitle) {
        this(context, context.getResources().getString(restitle), context.getResources().getString(resSubTitle));
    }

    public LoginFailDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnClickListener(CharSequence text, View.OnClickListener listener) {
        mOnClickListener = listener;
        mConfirmButtonTxt = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_fail);

        TextView txt = (TextView) findViewById(R.id.dialog_login_fail_title);
        if (!TextUtils.isEmpty(mTitle))
            txt.setText(mTitle);

        txt = (TextView) findViewById(R.id.dialog_login_fail_subtitle);
        if (!TextUtils.isEmpty(mSubTitle))
            txt.setText(mSubTitle);

        Button btn = (Button) findViewById(R.id.login_fail_button_confirm);
        if (!TextUtils.isEmpty(mConfirmButtonTxt))
            btn.setText(mConfirmButtonTxt);
        btn.setOnClickListener(mOnClickListener == null ? this : mOnClickListener);
    }

    /**
     * add by ruth on 2015-6-2
     */
    //TODO 添加确认按钮的点击事件处理
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_fail_button_confirm) {
            dismiss();
        }
    }

}
