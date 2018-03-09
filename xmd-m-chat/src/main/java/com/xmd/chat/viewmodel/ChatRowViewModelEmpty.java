package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowEmptyBinding;
import com.xmd.chat.message.ChatMessage;


/**
 * Created by mo on 17-7-1.
 * 空消息
 */

public class ChatRowViewModelEmpty extends ChatRowViewModel {

    public ChatRowViewModelEmpty(ChatMessage chatMessage) {
        super(chatMessage);
    }

    public static View createView(ViewGroup parent) {
        ChatRowEmptyBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_empty, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        ChatRowEmptyBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    public void onUnbindView() {

    }

    @Override
    public Drawable getContentViewBackground(Context context) {
        return null;
    }




}
