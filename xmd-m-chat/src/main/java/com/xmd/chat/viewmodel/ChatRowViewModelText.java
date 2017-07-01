package com.xmd.chat.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.chat.R;
import com.xmd.chat.message.ChatMessage;


/**
 * Created by mo on 17-7-1.
 * 文本消息
 */

public class ChatRowViewModelText extends BaseChatRowViewModel {
    public ChatRowViewModelText(ChatMessage chatMessage) {
        super(chatMessage);
    }

    public static View createView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row_text, parent, false);
    }

    @Override
    public void bindView(View view) {
        ((TextView) view).setText(chatMessage.getContentText());
    }
}
