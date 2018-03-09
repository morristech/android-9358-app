package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;
import com.tencent.imsdk.TIMMessage;
import com.xmd.chat.xmdchat.constant.XmdMessageType;
import com.xmd.chat.xmdchat.messagebean.TextMessageBean;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.ImChatMessageManagerPresent;

/**
 * Created by mo on 17-7-12.
 * 打赏消息
 */

public class RewardChatMessage<T> extends ChatMessage {
    public RewardChatMessage(T message) {
        super(message);
    }
    public static RewardChatMessage createRequestRewardMessage(String remoteChatId) {
        if(XmdChatModel.getInstance().chatModelIsEm()){
            EMMessage emMessage = EMMessage.createTxtSendMessage("<i></i>万水千山总是情<br/>打赏两个行不行~", remoteChatId);
            RewardChatMessage message = new RewardChatMessage(emMessage);
            message.setMsgType(MSG_TYPE_REQUEST_REWARD);
            return message;
        }else {
            TextMessageBean bean = new TextMessageBean();
            bean.setContent("<i></i>万水千山总是情<br/>打赏两个行不行~");
            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(bean,XmdMessageType.REQUEST_REWARD_TYPE,null,null);
            RewardChatMessage rewardChatMessage = new RewardChatMessage(message);
            return rewardChatMessage;
        }
    }
}
