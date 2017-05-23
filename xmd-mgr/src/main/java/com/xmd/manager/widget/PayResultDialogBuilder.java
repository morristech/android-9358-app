package com.xmd.manager.widget;

import android.content.Context;
import android.view.View;

/**
 * Created by sdcm on 15-11-24.
 */
public class PayResultDialogBuilder {

    private PayResultDialog.Builder mBuilder;

    public PayResultDialogBuilder(Context context) {
        mBuilder = new PayResultDialog.Builder(context);
    }

    public PayResultDialogBuilder setTitle(String title) {
        mBuilder.setTitle(title);
        return this;
    }

    public PayResultDialogBuilder setCustomView(View customView) {
        mBuilder.setCustomView(customView);
        return this;
    }

    public PayResultDialogBuilder setMessage(String message) {
        mBuilder.setMessage(message);
        return this;
    }

    public PayResultDialogBuilder setPositiveButton(String positiveText, View.OnClickListener onClickListener) {
        mBuilder.setPositiveButton(positiveText, onClickListener);
        return this;
    }

    public PayResultDialogBuilder setCancelable(boolean cancelable) {
        mBuilder.setCancelable(cancelable);
        return this;
    }

    public void show() {
        mBuilder.show();
    }
}
