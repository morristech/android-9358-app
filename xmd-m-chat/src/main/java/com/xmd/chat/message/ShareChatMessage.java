package com.xmd.chat.message;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.beans.Marketing;
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

    public static ShareChatMessage createMarketingTimeLimitMessage(String remoteChatId, Marketing marketing) {
        EMMessage emMessage = EMMessage.createTxtSendMessage("春宵一刻值千金", remoteChatId);
        ShareChatMessage message = new ShareChatMessage(emMessage);
        message.setMsgType(MSG_TYPE_TIME_LIMIT_TYPE);
        message.setAttr(ATTR_ACT_NAME, marketing.itemName);
        message.setAttr(ATTR_ACT_ID, TextUtils.isEmpty(marketing.actId) ? marketing.id : marketing.actId);
        return message;
    }

    public static ShareChatMessage createMarketingOneYuanMessage(String remoteChatId, Marketing marketing) {
        EMMessage emMessage = EMMessage.createTxtSendMessage("我和你只有一块钱的距离", remoteChatId);
        ShareChatMessage message = new ShareChatMessage(emMessage);
        message.setMsgType(MSG_TYPE_ONE_YUAN_TYPE);
        message.setAttr(ATTR_ACT_NAME, marketing.actName);
        message.setAttr(ATTR_ACT_ID, TextUtils.isEmpty(marketing.actId) ? marketing.id : marketing.actId);
        return message;
    }

    public static ShareChatMessage createMarketingLuckWheelMessage(String remoteChatId, Marketing marketing) {
        EMMessage emMessage = EMMessage.createTxtSendMessage("那一世转山转水，只为相见", remoteChatId);
        ShareChatMessage message = new ShareChatMessage(emMessage);
        message.setMsgType(MSG_TYPE_LUCKY_WHEEL_TYPE);
        message.setAttr(ATTR_ACT_NAME, marketing.actName);
        message.setAttr(ATTR_ACT_ID, TextUtils.isEmpty(marketing.actId) ? marketing.id : marketing.actId);
        return message;
    }

    public static ShareChatMessage createInvitationMessage(String remoteChatId) {
        EMMessage emMessage = EMMessage.createTxtSendMessage("邀请好友赢豪礼～", remoteChatId);
        ShareChatMessage message = new ShareChatMessage(emMessage);
        message.setMsgType(MSG_TYPE_INVITE_GIFT_TYPE);
        return message;
    }


    public String getActName() {
        return getSafeStringAttribute(ATTR_ACT_NAME);
    }

    public String getCardType() {
        return getSafeStringAttribute(ATTR_CARD_TYPE);
    }
}
