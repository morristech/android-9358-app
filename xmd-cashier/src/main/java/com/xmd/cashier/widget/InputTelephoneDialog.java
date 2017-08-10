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
 * 输入手机号码
 */

public class InputTelephoneDialog extends Dialog {
    private Context mContext;

    private TextView mTitle;
    private TextView mMemberNo;
    private ClearableEditText mMemberTele;
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

    public void setMemberNo(String memberNo) {
        if (!TextUtils.isEmpty(memberNo)) {
            mMemberNo.setText(memberNo);
        }
    }

    public InputTelephoneDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public InputTelephoneDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected InputTelephoneDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_telephone);

        mTitle = (TextView) findViewById(R.id.tv_dialog_title);
        mMemberNo = (TextView) findViewById(R.id.tv_member_no);
        mMemberTele = (ClearableEditText) findViewById(R.id.edt_member_phone);
        mNegative = (Button) findViewById(R.id.btn_negative);
        mPositive = (Button) findViewById(R.id.btn_positive);

        mNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(mMemberTele.getWindowToken(), 0);
                }
                mCallBack.onBtnNegative();
            }
        });

        mPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(mMemberTele.getWindowToken(), 0);
                }
                mCallBack.onBtnPositive(mMemberTele.getText().toString());
            }
        });
    }

    public interface BtnCallBack {
        void onBtnNegative();

        void onBtnPositive(String telephone);
    }
}
