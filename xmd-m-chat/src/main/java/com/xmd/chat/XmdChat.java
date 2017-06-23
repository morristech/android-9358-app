package com.xmd.chat;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.xmd.app.IFunctionInit;

/**
 * Created by mo on 17-6-21.
 * 聊天模块
 */

public class XmdChat implements IFunctionInit{
    private static final XmdChat ourInstance = new XmdChat();

    public static XmdChat getInstance() {
        return ourInstance;
    }

    private XmdChat() {
    }

    @Override
    public void init(Context context) {
        //获取所有的会话消息
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
    }
}
