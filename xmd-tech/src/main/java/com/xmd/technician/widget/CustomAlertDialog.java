package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.common.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-12-2.
 */
public class CustomAlertDialog extends Dialog {

    public CustomAlertDialog(Context context, int themeId) {
        super(context, themeId);
    }

    public static class Builder {

        private Context mContext;
        @BindView(R.id.dialog_title)
        TextView mTvTitle;
        @BindView(R.id.flContentContainer)
        FrameLayout mFlContentContainer;
        @BindView(R.id.dialog_content)
        TextView mTvContent;
        @BindView(R.id.dialog_negative)
        Button mBtnNegative;
        @BindView(R.id.dialog_positive)
        Button mBtnPositive;

        private String mTitle;
        private String mMessage;
        private String mNegativeText;
        private String mPositiveText;
        private View mCustomerView;

        private View.OnClickListener mPositiveClickListener;
        private View.OnClickListener mNegativeClickListener;

        private boolean mCancelable = true;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder setCustomView(View customerView) {
            mCustomerView = customerView;
            return this;
        }

        public Builder setNegativeButton(String negativeText, View.OnClickListener negativeClickListener) {
            mNegativeText = negativeText;
            mNegativeClickListener = negativeClickListener;
            ;
            return this;
        }

        public Builder setPositiveButton(String positiveText, View.OnClickListener positiveClickListener) {
            mPositiveText = positiveText;
            mPositiveClickListener = positiveClickListener;
            ;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public CustomAlertDialog build() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.custom_alert_dialog, null);
            final CustomAlertDialog dialog = new CustomAlertDialog(mContext, R.style.custom_alert_dialog);

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
                mFlContentContainer.addView(mCustomerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                mTvContent.setVisibility(View.GONE);
            } else if (Utils.isNotEmpty(mMessage)) {
                mTvContent.setText(mMessage);
            }

            if (Utils.isNotEmpty(mNegativeText)) {
                mBtnNegative.setText(mNegativeText);
                mBtnNegative.setOnClickListener(v -> {
                    if (mNegativeClickListener != null) {
                        mNegativeClickListener.onClick(v);
                    }
                    dialog.dismiss();
                });
            } else {
                mBtnNegative.setVisibility(View.GONE);
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

        public CustomAlertDialog show() {
            CustomAlertDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
