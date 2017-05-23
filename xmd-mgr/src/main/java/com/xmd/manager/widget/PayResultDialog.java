package com.xmd.manager.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-12-2.
 */
public class PayResultDialog extends Dialog {

    public PayResultDialog(Context context, int themeId) {
        super(context, themeId);
    }

    public static class Builder {

        private Context mContext;
        @Bind(R.id.dialog_title)
        TextView mTvTitle;
        @Bind(R.id.dialog_content)
        TextView mTvContent;
        @Bind(R.id.dialog_positive)
        Button mBtnPositive;

        private String mTitle;
        private String mMessage;
        private String mPositiveText;
        private View mCustomerView;

        private View.OnClickListener mPositiveClickListener;

        private boolean mCancelable = true;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setCustomView(View customerView) {
            mCustomerView = customerView;
            return this;
        }

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder setPositiveButton(String positiveText, View.OnClickListener positiveClickListener) {
            mPositiveText = positiveText;
            mPositiveClickListener = positiveClickListener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public PayResultDialog build() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.pay_result_dialog, null);
            final PayResultDialog dialog = new PayResultDialog(mContext, R.style.custom_alert_dialog);
            dialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            ButterKnife.bind(this, view);

            if (Utils.isEmpty(mTitle)) {
                mTvTitle.setVisibility(View.GONE);
            } else {
                mTvTitle.setText(mTitle);
            }

            if (mCustomerView != null) {
                mTvContent.setVisibility(View.GONE);
            } else if (Utils.isNotEmpty(mMessage)) {
                mTvContent.setText(mMessage);
            }
            if (Utils.isNotEmpty(mPositiveText)) {
                mBtnPositive.setText(mPositiveText);
                mBtnPositive.setOnClickListener(v -> {
                    if (mPositiveClickListener != null) {
                        mPositiveClickListener.onClick(v);
                    }
                    dialog.dismiss();
                });
            } else {
                mBtnPositive.setVisibility(View.GONE);
            }

            dialog.setCancelable(mCancelable);

            return dialog;
        }

        public PayResultDialog show() {
            PayResultDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
