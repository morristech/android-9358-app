package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.xmd.chat.ChatMenu;

/**
 * Created by mo on 17-7-6.
 * 更多菜单
 */

public class SubMenuMoreViewModel {
    private ChatMenu chatMenu;
    private View.OnClickListener listener;

    public SubMenuMoreViewModel(ChatMenu chatMenu, View.OnClickListener listener) {
        this.chatMenu = chatMenu;
        this.listener = listener;
    }

    public ChatMenu getChatMenu() {
        return chatMenu;
    }

    public void setChatMenu(ChatMenu chatMenu) {
        this.chatMenu = chatMenu;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @BindingAdapter("src")
    public static void bindSrc(ImageView imageView, int srcResource) {
        imageView.setImageResource(srcResource);
    }
}
