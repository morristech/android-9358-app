package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.beans.OnceCard;

/**
 * Created by mo on 17-7-10.
 * 分享内容
 */

public class ShareChatMessage extends ChatMessage {
    private static final String ATTR_ACT_ID = "actId";
    private static final String ATTR_ACT_NAME = "actName";

    private static final String ATTR_TEMPLATE_ID = "templateId";

    private static final String ATTR_CARD_TYPE = "cardType";

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

    public static ShareChatMessage createOnceCardMessage(String remoteChatId, OnceCard card) {
        String content = "";
        switch (card.cardType) {
            case OnceCard.CARD_TYPE_SINGLE:
                content = "品质服务，畅快享受";
                break;
            case OnceCard.CARD_TYPE_MIX:
                content = "特惠套餐，超越期待";
                break;
            case OnceCard.CARD_TYPE_CREDIT:
                content = "会员积分超值兑换～";
                break;
        }
        EMMessage emMessage = EMMessage.createTxtSendMessage(content, remoteChatId);
        ShareChatMessage message = new ShareChatMessage(emMessage);
        message.setMsgType(MSG_TYPE_ONCE_CARD);
        message.setAttr(ATTR_CARD_TYPE, card.cardType);
        message.setAttr(ATTR_ACT_NAME, card.name);
        message.setAttr(ATTR_ACT_ID, card.id);
        return message;
    }

    public String getActName() {
        return getSafeStringAttribute(ATTR_ACT_NAME);
    }

    public String getCardType() {
        return getSafeStringAttribute(ATTR_CARD_TYPE);
    }
}
