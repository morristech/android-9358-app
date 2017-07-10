package com.xmd.chat.message;

import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

/**
 * Created by mo on 17-7-7.
 * 撤回消息
 */

public class RevokeChatMessage extends ChatMessage {
    public RevokeChatMessage(String revokeMsgId, EMMessage emMessage) {
        super(emMessage, MSG_TYPE_ORIGIN_CMD);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody("revoke");
        setAttr("messageId", revokeMsgId);
        setAttr("time", emMessage.getMsgTime());
        setAttr("mark", "revoke");
        emMessage.addBody(cmdBody);
    }

    public static RevokeChatMessage create(String remoteChatId, String revokeMsgId) {
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMessage.setTo(remoteChatId);
        return new RevokeChatMessage(revokeMsgId, cmdMessage);
    }
}
