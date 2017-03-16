package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xmd.technician.R;

/**
 * Created by sdcm on 17-3-16.
 */

public class ChatMessageManagerDialog extends Dialog {

    private TextView textDelete;
    private OnItemClickedListener mOnItemClicked;

    public interface OnItemClickedListener{
        void onItemClicked();
    }

    public ChatMessageManagerDialog(Context context) {
        this(context, R.style.default_dialog_style);
    }

    public void setItemClickedListener(OnItemClickedListener listener){
        this.mOnItemClicked = listener;
    }

    public ChatMessageManagerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ChatMessageManagerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_message_manager_dialog);
        textDelete = (TextView) findViewById(R.id.delete_message);
        textDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mOnItemClicked.onItemClicked();
            }
        });
    }
}
