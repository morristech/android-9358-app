package com.xmd.chat.viewmodel;

import android.view.View;

/**
 * Created by mo on 17-7-6.
 * 表情
 */

public class SubMenuFastReplyViewModel {
    private String text;
    private View.OnClickListener listener;

    public SubMenuFastReplyViewModel(String text, View.OnClickListener listener) {
        this.text = text;
        this.listener = listener;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
