package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by mo on 17-7-6.
 * 表情
 */

public class SubMenuEmojiViewModel {
    private int icon;
    private String text;
    private View.OnClickListener listener;

    public SubMenuEmojiViewModel(String text, int icon, View.OnClickListener listener) {
        this.icon = icon;
        this.text = text;
        this.listener = listener;
    }

    @BindingAdapter("image")
    public static void bindImage(ImageView imageView, SubMenuEmojiViewModel data) {
        imageView.setImageResource(data.icon);
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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
