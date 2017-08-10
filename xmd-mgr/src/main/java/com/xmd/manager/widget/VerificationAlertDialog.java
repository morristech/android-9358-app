package com.xmd.manager.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.R;
import com.xmd.manager.beans.VerificationCouponDetailBean;
import com.xmd.manager.common.Utils;

import java.util.List;

/**
 * Created by Lhj on 17-7-20.
 */

public class VerificationAlertDialog extends Dialog implements View.OnClickListener, TextWatcher {


    EditText dialogEditContent;
    TextView tvDiscountTotal;
    LinearLayout llDiscountInfo;
    Button btnNegative;
    Button btnPositive;
    LinearLayout llDiscountTotal;

    private Context mContext;
    private List<VerificationCouponDetailBean> discounts;
    public VerificationSuccessListener mVerificationListener;
    private boolean canUse;

    public interface VerificationSuccessListener {
        void verificationSuccess(boolean success, float money);
    }

    public void setVerificationListener(VerificationSuccessListener verificationListener) {
        this.mVerificationListener = verificationListener;
    }

    public VerificationAlertDialog(Context context) {
        this(context, R.style.default_dialog_style);
        mContext = context;
    }

    public VerificationAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected VerificationAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public VerificationAlertDialog(Context context, List<VerificationCouponDetailBean> discount) {
        super(context);
        this.mContext = context;
        this.discounts = discount;
    }


    private void initDiscountView(float inputMoney) {
        if (discounts == null || discounts.size() == 0) {
            return;
        }
        llDiscountInfo.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, Utils.dip2px(mContext, 12));
        int actAmountTotal = 0;
        for (VerificationCouponDetailBean bean : discounts) {
            TextView tv = new TextView(mContext);
            actAmountTotal += (100000 - bean.actAmount);
            if (inputMoney > 0) {
                tv.setText(String.format("优惠金额: %1.1f元(%1.1f折)", (inputMoney * (100000 - bean.actAmount)) / 100000f, bean.actAmount / 10000f));
            } else {
                tv.setText(String.format("优惠金额: 元(%1.1f折)", bean.actAmount / 10000f));
            }
            tv.setTextSize(14);
            tv.setTextColor(Color.parseColor("#212121"));
            tv.setPadding(14, 0, 14, 0);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(lp);
            llDiscountInfo.addView(tv);
        }
        if (discounts.size() == 1) {
            llDiscountTotal.setVisibility(View.GONE);
        } else {
            llDiscountTotal.setVisibility(View.VISIBLE);
            if (actAmountTotal >= 100000) {
                tvDiscountTotal.setText(String.format("%1.1f元", inputMoney * 1.0f));
            } else {
                tvDiscountTotal.setText(String.format("%1.1f元", (inputMoney * actAmountTotal / 100000.0f)));
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_verification_alert_layout);
        dialogEditContent = (EditText) findViewById(R.id.dialog_edit_content);
        dialogEditContent.addTextChangedListener(this);

        llDiscountInfo = (LinearLayout) findViewById(R.id.ll_discount_info);
        tvDiscountTotal = (TextView) findViewById(R.id.tv_discount_money);
        llDiscountTotal = (LinearLayout) findViewById(R.id.ll_discount_total);
        btnNegative = (Button) findViewById(R.id.dialog_negative);
        btnNegative.setOnClickListener(this);
        btnPositive = (Button) findViewById(R.id.dialog_positive);
        btnPositive.setOnClickListener(this);
        // dialogEditContent.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        initDiscountView(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_negative:
                this.dismiss();
                break;
            case R.id.dialog_positive:
                if (mVerificationListener != null) {
                    if(TextUtils.isEmpty(dialogEditContent.getText().toString())){
                        XToast.show("输入金额不能为空");
                       return;
                    }
                    for (int i = 0; i < discounts.size(); i++) {
                        if (discounts.get(i).consumeAmount <= Float.parseFloat(dialogEditContent.getText().toString()) * 100) {
                            canUse = true;
                        } else {
                            canUse = false;
                            break;
                        }
                    }
                    if (canUse) {
                        mVerificationListener.verificationSuccess(true, Float.parseFloat(dialogEditContent.getText().toString()));
                    } else {
                        mVerificationListener.verificationSuccess(false, Float.parseFloat(dialogEditContent.getText().toString()));
                    }

                }
                this.dismiss();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = dialogEditContent.getText().toString();
        if (Utils.isNotEmpty(text)) {
            initDiscountView(Float.parseFloat(text));
        } else {
            initDiscountView(0.0f);
        }

    }
}
