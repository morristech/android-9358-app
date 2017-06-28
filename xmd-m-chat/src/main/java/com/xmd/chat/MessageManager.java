package com.xmd.chat;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.event.EventTotalUnreadMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by mo on 17-6-28.
 * 消息管理
 */

class MessageManager {
    private static final MessageManager ourInstance = new MessageManager();

    static MessageManager getInstance() {
        return ourInstance;
    }

    private MessageManager() {
    }

    public void init() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                EventBus.getDefault().post(new EventNewMessages(list));
                EventBus.getDefault().post(new EventTotalUnreadMessage(EMClient.getInstance().chatManager().getUnreadMessageCount()));
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        });
    }
}
