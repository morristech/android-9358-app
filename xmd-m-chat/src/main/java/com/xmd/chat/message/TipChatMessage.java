package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;
import com.xmd.app.user.User;

/**
 * Created by mo on 17-7-7.
 * 提示消息
 */

public class TipChatMessage extends ChatMessage {
    public TipChatMessage(EMMessage emMessage) {
        super(emMessage, ChatMessage.MSG_TYPE_TIP);
    }

    public static TipChatMessage create(User remoteUser, String tip) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(tip, remoteUser.getChatId());
        return new TipChatMessage(emMessage);
    }
}
