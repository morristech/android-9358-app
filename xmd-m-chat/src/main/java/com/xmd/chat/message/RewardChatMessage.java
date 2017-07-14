package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;

/**
 * Created by mo on 17-7-12.
 * 打赏消息
 */

public class RewardChatMessage extends ChatMessage {
    public RewardChatMessage(EMMessage emMessage) {
        super(emMessage);
    }
    public static RewardChatMessage createRequestRewardMessage(String remoteChatId) {
        EMMessage emMessage = EMMessage.createTxtSendMessage("<i></i>万水千山总是情<br/>打赏两个行不行~", remoteChatId);
        RewardChatMessage message = new RewardChatMessage(emMessage);
        message.setMsgType(MSG_TYPE_REQUEST_REWARD);
        return message;
    }
}