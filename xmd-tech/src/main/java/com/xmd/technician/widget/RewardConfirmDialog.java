package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 16-4-13.
 */
public class RewardConfirmDialog extends Dialog{

    @Bind(R.id.dialog_alert_title) TextView mTitleTxt;
    @Bind(R.id.dialog_alert_message) TextView mTipsTxt;
    @Bind(R.id.dialog_alert_ok_btn) Button mBtnOk;
    @Bind(R.id.dialog_alert_cancel_btn) Button mBtnCancal;

    private String mTitle;
    private String mTipInfo;
    private String mBtnText;
    private boolean isShow;

    public RewardConfirmDialog(Context context) {
        this(context, R.style.default_dialog_style);
    }

    public RewardConfirmDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public RewardConfirmDialog(Context context,String title, String msg,String btnText){
        this(context, R.style.default_dialog_style);
        this.mTitle = title;
        this.mTipInfo = msg;
        this.mBtnText = btnText;
    }

    public RewardConfirmDialog(Context context,String title, String msg,String btnText,boolean isShowCancle){
        this(context, R.style.default_dialog_style);
        this.mTitle = title;
        this.mTipInfo = msg;
        this.mBtnText = btnText;
        this.isShow = isShowCancle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_confirm_dialog);
        ButterKnife.bind(this);

        if(TextUtils.isEmpty(mTitle)){
            mTitleTxt.setVisibility(View.GONE);
        }else {
            mTitleTxt.setText(mTitle);
        }
        if(TextUtils.isEmpty(mTipInfo)){
            mTipsTxt.setVisibility(View.GONE);
        }else {
            mTipsTxt.setText(mTipInfo);
        }
        if(TextUtils.isEmpty(mBtnText)){
            mBtnOk.setText(ResourceUtils.getString(R.string.confirm));
        }else{
            mBtnOk.setText(mBtnText);
        }
        if(!isShow){
            mBtnCancal.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.dialog_alert_cancel_btn)
    public void cancel(){
        dismiss();
    }

    @OnClick(R.id.dialog_alert_ok_btn)
    public void onConfirmClick(){
        dismiss();
    }
}
