package com.xmd.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.xmd.app.R;


/**
 * Created by Lhj on 17-7-12.
 */

public class PromptConfirmDialog extends Dialog  {

    TextView mTitleTxt;
    TextView mTipsTxt;
    Button mBtnOk;
    Button mBtnCancel;
    Button dialogAlertOkBtn;
    private String mTitle;
    private String mTipInfo;
    private String mBtnText;
    private boolean isShow;
    private ConfirmClickedListener mConfirmClicked;

    public PromptConfirmDialog(Context context) {
        this(context, R.style.default_dialog_style);

    }

    public interface ConfirmClickedListener{
        void onConfirmClick();
    }

    public PromptConfirmDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public PromptConfirmDialog(Context context, String title, String msg, String btnText,ConfirmClickedListener listener) {
        this(context, R.style.default_dialog_style);
        this.mTitle = title;
        this.mTipInfo = msg;
        this.mBtnText = btnText;
        this.mConfirmClicked = listener;
    }

    public PromptConfirmDialog(Context context, String title, String msg, String btnText, boolean isShowCancel) {
        this(context, R.style.default_dialog_style);
        this.mTitle = title;
        this.mTipInfo = msg;
        this.mBtnText = btnText;
        this.isShow = isShowCancel;
    }

    protected PromptConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_confirm_dialog);
        mTitleTxt = (TextView) findViewById(R.id.dialog_alert_title);
        mTipsTxt = (TextView) findViewById(R.id.dialog_alert_message);
        mBtnOk = (Button) findViewById(R.id.dialog_alert_ok_btn);
        mBtnCancel = (Button) findViewById(R.id.dialog_alert_cancel_btn);
        if (TextUtils.isEmpty(mTitle)) {
            mTitleTxt.setVisibility(View.GONE);
        } else {
            mTitleTxt.setText(mTitle);
        }
        if (TextUtils.isEmpty(mTipInfo)) {
            mTipsTxt.setVisibility(View.GONE);
        } else {
            mTipsTxt.setText(mTipInfo);
        }
        if (TextUtils.isEmpty(mBtnText)) {
            mBtnOk.setText("确定");
        } else {
            mBtnOk.setText(mBtnText);
        }
        if (!isShow) {
            mBtnCancel.setVisibility(View.GONE);
        }
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mConfirmClicked.onConfirmClick();
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



}
