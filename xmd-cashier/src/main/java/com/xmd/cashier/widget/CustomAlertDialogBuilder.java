package com.xmd.cashier.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;

/**
 * Created by heyangya on 16-9-7.
 */

public class CustomAlertDialogBuilder {
    private TextView mMessageTextView;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private DialogInterface mDialogInterface;
    private View mDividerView2;

    private AlertDialog.Builder mBuilder;

    public CustomAlertDialogBuilder(Context context) {
        mBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
        mBuilder.setView(view);
        mBuilder.setCancelable(false);
        mMessageTextView = (TextView) view.findViewById(R.id.tv_message);
        mPositiveButton = (Button) view.findViewById(R.id.button1);
        mNegativeButton = (Button) view.findViewById(R.id.button2);
        mDividerView2 = view.findViewById(R.id.divider2);
    }

    public CustomAlertDialogBuilder setMessage(int messageId) {
        mMessageTextView.setText(messageId);
        return this;
    }

    public CustomAlertDialogBuilder setMessage(String message) {
        mMessageTextView.setText(message);
        return this;
    }

    public CustomAlertDialogBuilder setPositiveButton(int textId, final DialogInterface.OnClickListener listener) {
        mPositiveButton.setText(textId);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(mDialogInterface, DialogInterface.BUTTON_POSITIVE);
            }
        });
        return this;
    }

    public CustomAlertDialogBuilder setPositiveButton(String text, final DialogInterface.OnClickListener listener) {
        mPositiveButton.setVisibility(View.VISIBLE);
        mPositiveButton.setText(text);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(mDialogInterface, DialogInterface.BUTTON_POSITIVE);
            }
        });
        return this;
    }


    public CustomAlertDialogBuilder setNegativeButton(int textId, final DialogInterface.OnClickListener listener) {
        mDividerView2.setVisibility(View.VISIBLE);
        mNegativeButton.setVisibility(View.VISIBLE);
        mNegativeButton.setText(textId);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(mDialogInterface, DialogInterface.BUTTON_NEGATIVE);
            }
        });
        return this;
    }

    public CustomAlertDialogBuilder setNegativeButton(String text, final DialogInterface.OnClickListener listener) {
        mDividerView2.setVisibility(View.VISIBLE);
        mNegativeButton.setVisibility(View.VISIBLE);
        mNegativeButton.setText(text);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(mDialogInterface, DialogInterface.BUTTON_NEGATIVE);
            }
        });
        return this;
    }

    public AlertDialog create() {
        AlertDialog alertDialog = mBuilder.create();
        mDialogInterface = alertDialog;
        return alertDialog;
    }
}
