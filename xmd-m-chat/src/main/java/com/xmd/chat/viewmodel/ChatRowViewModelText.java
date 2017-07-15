package com.xmd.chat.viewmodel;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowTextBinding;
import com.xmd.chat.message.ChatMessage;


/**
 * Created by mo on 17-7-1.
 * 文本消息
 */

public class ChatRowViewModelText extends ChatRowViewModel {
    public ChatRowViewModelText(ChatMessage chatMessage) {
        super(chatMessage);
    }

    public static View createView(ViewGroup parent) {
        ChatRowTextBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_text, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        ChatRowTextBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    public void onUnbindView() {

    }

    public CharSequence getMessage() {
        return chatMessage.getContentText();
    }
}
