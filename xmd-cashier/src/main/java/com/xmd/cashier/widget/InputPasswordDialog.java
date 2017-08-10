package com.xmd.cashier.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;

/**
 * Created by zr on 17-8-10.
 * 输入收银员密码
 */

public class InputPasswordDialog extends Dialog {
    private Context mContext;

    private TextView mTitle;
    private ClearableEditText mPassword;
    private Button mNegative;
    private Button mPositive;

    private BtnCallBack mCallBack;

    public void setCallBack(BtnCallBack callback) {
        this.mCallBack = callback;
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    public InputPasswordDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public InputPasswordDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected InputPasswordDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_password);
        mTitle = (TextView) findViewById(R.id.tv_dialog_title);
        mPassword = (ClearableEditText) findViewById(R.id.edt_password_input);
        mNegative = (Button) findViewById(R.id.btn_negative);
        mPositive = (Button) findViewById(R.id.btn_positive);

        mNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
                }
                mCallBack.onBtnNegative();
            }
        });

        mPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
                }
                mCallBack.onBtnPositive(mPassword.getText().toString());
            }
        });
    }

    public interface BtnCallBack {
        void onBtnNegative();

        void onBtnPositive(String password);
    }
}
