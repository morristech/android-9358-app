package com.xmd.chat.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xmd.chat.message.ChatMessage;


/**
 * Created by mo on 17-7-1.
 * 文本消息
 */

public class ChatRowDataText extends BaseChatRowData {
    public ChatRowDataText(ChatMessage chatMessage) {
        super(chatMessage);
    }

    @Override
    public View createViewAndBindData(Context context) {
        TextView textView = new TextView(context);
        textView.setText(chatMessage.getContentText());
        return textView;
    }
}
