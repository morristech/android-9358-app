package com.xmd.manager.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xmd.manager.R;

/**
 * Created by Lhj on 2017/1/5.
 */

public class AddGroupDialog extends Dialog {

    private EditText editText;
    private Button positiveButton, negativeButton;
    private TextView title;

    public AddGroupDialog(Context context) {
        this(context, -1);
    }

    public AddGroupDialog(Context context, int themeResId) {
        super(context, R.style.custom_alert_dialog);
        setCustomDialog();
    }

    protected AddGroupDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.layout_add_editor_group, null);
        title = (TextView) mView.findViewById(R.id.dialog_title);
        editText = (EditText) mView.findViewById(R.id.dialog_edit_content);
        positiveButton = (Button) mView.findViewById(R.id.dialog_positive);
        negativeButton = (Button) mView.findViewById(R.id.dialog_negative);
        super.addContentView(mView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public View getEditText() {
        return editText;
    }

    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    /**
     * 取消键监听器
     *
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }

//    @Override
//    public void show() {
//        super.show();
//        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.gravity= Gravity.BOTTOM;
//        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.height= WindowManager.LayoutParams.WRAP_CONTENT;
//
//        getWindow().getDecorView().setPadding(0, 0, 0, 0);
//
//        getWindow().setAttributes(layoutParams);
//    }
}
