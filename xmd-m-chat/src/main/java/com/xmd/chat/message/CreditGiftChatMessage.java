package com.xmd.chat.message;

/**
 * Created by mo on 17-7-14.
 * 积分礼物消息
 */

public class CreditGiftChatMessage<T> extends ChatMessage {
    public String ATTR_GIFT_VALUE = "giftValue";
    public String ATTR_GIFT_NAME = "giftName";
    public String ATTR_GIFT_ID = "giftId";

    public CreditGiftChatMessage(T message) {
        super(message);
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
