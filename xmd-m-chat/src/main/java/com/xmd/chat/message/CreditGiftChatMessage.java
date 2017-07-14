package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;

/**
 * Created by mo on 17-7-14.
 * 积分礼物消息
 */

public class CreditGiftChatMessage extends ChatMessage {
    public String ATTR_GIFT_VALUE = "giftValue";
    public String ATTR_GIFT_NAME = "giftName";
    public String ATTR_GIFT_ID = "giftId";

    public CreditGiftChatMessage(EMMessage emMessage) {
        super(emMessage);
    }

    public String getGiftId() {
        return getSafeStringAttribute(ATTR_GIFT_ID);
    }

    public String getGiftName() {
        return getSafeStringAttribute(ATTR_GIFT_NAME);
    }

    public int getGiftCredit() {
        return getSafeIntegerAttribute(ATTR_GIFT_VALUE);
    }
}
