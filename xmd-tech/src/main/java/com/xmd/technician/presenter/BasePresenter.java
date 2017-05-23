package com.xmd.technician.presenter;

import android.content.Context;

/**
 * Created by heyangya on 16-12-19.
 */

public class BasePresenter<T> {
    protected T mView;
    protected Context mContext;

    public BasePresenter(Context context, T view) {
        mContext = context;
        mView = view;
    }

    public void onCreate() {

    }

    public void onDestroy() {

    }
}
