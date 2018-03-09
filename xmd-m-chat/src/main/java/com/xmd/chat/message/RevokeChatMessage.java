package com.xmd.chat.message;

import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

/**
 * Created by mo on 17-7-7.
 * 撤回消息
 */

public class RevokeChatMessage<T> extends ChatMessage {
    public RevokeChatMessage(T message) {
        super(message);
    }

    public static RevokeChatMessage create(String remoteChatId, String revokeMsgId) {
        EMMessage emMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        RevokeChatMessage message = new RevokeChatMessage(emMessage);
        message.setMsgType(MSG_TYPE_ORIGIN_CMD);

        emMessage.setTo(remoteChatId);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody("revoke");
        message.setAttr("messageId", revokeMsgId);
        message.setAttr("time", emMessage.getMsgTime());
        message.setAttr("mark", "revoke");
        emMessage.addBody(cmdBody);

        return message;
    }
}
