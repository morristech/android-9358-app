package com.xmd.technician.widget;

import android.content.Context;
import android.view.View;

/**
 * Created by sdcm on 15-11-24.
 */
public class AlertDialogBuilder {

    private CustomAlertDialog.Builder mBuilder;

    public AlertDialogBuilder(Context context) {
        mBuilder = new CustomAlertDialog.Builder(context);
    }

    public AlertDialogBuilder setTitle(String title) {
        mBuilder.setTitle(title);
        return this;
    }

    public AlertDialogBuilder setCustomView(View customView) {
        mBuilder.setCustomView(customView);
        return this;
    }

    public AlertDialogBuilder setMessage(String message) {
        mBuilder.setMessage(message);
        return this;
    }

    public AlertDialogBuilder setNegativeButton(String negativeText, View.OnClickListener onClickListener) {
        mBuilder.setNegativeButton(negativeText, onClickListener);
        return this;
    }

    public AlertDialogBuilder setPositiveButton(String positiveText, View.OnClickListener onClickListener) {
        mBuilder.setPositiveButton(positiveText, onClickListener);
        return this;
    }

    public AlertDialogBuilder setCancelable(boolean cancelable) {
        mBuilder.setCancelable(cancelable);
        return this;
    }

    public void show() {
        mBuilder.show();
    }
}
