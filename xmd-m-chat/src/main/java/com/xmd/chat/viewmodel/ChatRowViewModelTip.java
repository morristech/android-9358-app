package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowTipBinding;
import com.xmd.chat.message.ChatMessage;


/**
 * Created by mo on 17-7-1.
 * 位置消息
 */

public class ChatRowViewModelTip extends ChatRowViewModel {

    public ChatRowViewModelTip(ChatMessage chatMessage) {
        super(chatMessage);
    }

    public static View createView(ViewGroup parent) {
        ChatRowTipBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_tip, parent, false);
        return binding.getRoot();
    }

    @Override
    public void onBindView(View view) {
        ChatRowTipBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
    }

    @Override
    public void onUnbindView() {

    }

    @Override
    public Drawable getContentViewBackground(Context context) {
        return null;
    }

    public CharSequence getTip() {
        return chatMessage.getContentText();
    }
}
