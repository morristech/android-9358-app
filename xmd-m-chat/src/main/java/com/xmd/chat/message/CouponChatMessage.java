package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;

/**
 * Created by mo on 17-7-10.
 * 优惠券消息
 */

public class CouponChatMessage extends ChatMessage {
    private final static String ATTR_COUPON_ID = "actId";
    private final static String ATTR_INVITE_CODE = "techCode";

    public CouponChatMessage(EMMessage emMessage) {
        super(emMessage);
        setMsgType(MSG_TYPE_COUPON);
    }

    public static CouponChatMessage create(String remoteChatId, String couponId, String couponText, String inviteCode) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(couponText, remoteChatId);
        CouponChatMessage chatMessage = new CouponChatMessage(emMessage);
        chatMessage.setAttr(ATTR_COUPON_ID, couponId);
        chatMessage.setAttr(ATTR_INVITE_CODE, inviteCode);
        return chatMessage;
    }
}
