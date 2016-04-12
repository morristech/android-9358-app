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
 * 自定义样式的输入对话框
 * Created by ruth on 15-6-1.
 */
public abstract class ConfirmDialog extends Dialog {

    private Button mInputCancel;
    private Button mInputConfirm;

    private Context context;
    private String mMessage;

    public ConfirmDialog(Context context) {
        this(context, R.style.default_dialog_style);
        this.context = context;
    }

    public ConfirmDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public ConfirmDialog(Context context,String msg) {
        this(context, R.style.default_dialog_style);
        this.context = context;
        this.mMessage = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);

        mInputCancel = (Button) findViewById(R.id.dialog_alert_cancel_btn);
        mInputCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mInputConfirm = (Button) findViewById(R.id.dialog_alert_ok_btn);
        mInputConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClick();
                dismiss();
            }
        });

        if(!TextUtils.isEmpty(mMessage)){
           ((TextView) findViewById(R.id.dialog_alert_message)).setText(mMessage);
        }
    }

    public abstract void onConfirmClick();
}
