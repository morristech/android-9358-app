package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;

/**
 * Created by mo on 17-7-10.
 * 分享内容
 */

public class ShareChatMessage extends ChatMessage {

    private static final String ATTR_TEMPLATE_ID = "templateId";
    private static final String ATTR_ACT_NAME = "actName";
    private static final String ATTR_ACT_ID = "actId";

    public ShareChatMessage(EMMessage emMessage) {
        super(emMessage);
    }

    public static ShareChatMessage createJournalMessage(String remoteChatId, String actId, String templateId, String actName) {
        EMMessage emMessage = EMMessage.createTxtSendMessage("有美女、有福利，你就是我的VIP", remoteChatId);
        ShareChatMessage message = new ShareChatMessage(emMessage);
        message.setMsgType(MSG_TYPE_JOURNAL);
        message.setAttr(ATTR_TEMPLATE_ID, templateId);
        message.setAttr(ATTR_ACT_NAME, actName == null ? "" : actName);
        message.setAttr(ATTR_ACT_ID, actId);
        return message;
    }
}
