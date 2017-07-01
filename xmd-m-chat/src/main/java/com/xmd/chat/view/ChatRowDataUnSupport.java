package com.xmd.chat.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xmd.chat.message.ChatMessage;

/**
 * Created by mo on 17-7-1.
 * 文本消息
 */

public class ChatRowDataUnSupport extends BaseChatRowData {
    public ChatRowDataUnSupport(ChatMessage chatMessage) {
        super(chatMessage);
    }

    @Override
    public View createViewAndBindData(Context context) {
        TextView textView = new TextView(context);
        textView.setText("当前暂不支持查看此消息");
        return textView;
    }
}
