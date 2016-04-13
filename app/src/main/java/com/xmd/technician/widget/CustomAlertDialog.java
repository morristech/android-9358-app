package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.common.Utils;

import butterknife.Bind;
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
        @Bind(R.id.dialog_content) TextView mContent;
        @Bind(R.id.dialog_negative) Button mBtnNegative;
        @Bind(R.id.dialog_positive) Button mBtnPositive;

        private String mMessage;
        private String mNegativeText;
        private String mPositiveText;

        private View.OnClickListener mPositiveClickListener;
        private View.OnClickListener mNegativeClickListener;

        private boolean mCancelable = true;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder setNegativeButton(String negativeText, View.OnClickListener negativeClickListener) {
            mNegativeText = negativeText;
            mNegativeClickListener = negativeClickListener;;
            return this;
        }

        public Builder setPositiveButton(String positiveText, View.OnClickListener positiveClickListener) {
            mPositiveText = positiveText;
            mPositiveClickListener = positiveClickListener;;
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

            if(Utils.isNotEmpty(mMessage)) {
                mContent.setText(mMessage);
            }

            if(Utils.isNotEmpty(mNegativeText)) {
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

            if(Utils.isNotEmpty(mPositiveText)) {
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
