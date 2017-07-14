package com.xmd.chat.viewmodel;

import android.databinding.ObservableBoolean;
import android.view.View;

/**
 * Created by mo on 17-7-10.
 * 通用分享数据
 */

public class ShareViewModel<T> {
    private T data;
    public View.OnClickListener listener;
    public ObservableBoolean select = new ObservableBoolean();

    public ShareViewModel(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
