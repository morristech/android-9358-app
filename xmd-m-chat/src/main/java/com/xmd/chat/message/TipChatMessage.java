package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.R;

/**
 * Created by mo on 17-7-7.
 * 提示消息
 */

public class TipChatMessage extends ChatMessage {
    private String ATTR_TIP_TYPE = "tipType";

    public TipChatMessage(EMMessage emMessage) {
        super(emMessage);
        setMsgType(ChatMessage.MSG_TYPE_TIP);
    }

    public TipChatMessage(EMMessage emMessage, String tipType) {
        super(emMessage);
        setMsgType(ChatMessage.MSG_TYPE_TIP);
        setAttr(ATTR_TIP_TYPE, tipType);
    }

    public String getAttrType() {
        String attrType = getSafeStringAttribute(ATTR_TIP_TYPE);
        if (attrType == null) {
            attrType = "";
        }
        return attrType;
    }

    @Override
    public CharSequence getContentText() {
        switch (getAttrType()) {
            case ChatMessage.MSG_TYPE_COUPON_TIP:
                return String.format("%s领取了您的\"%s\"", getUserName(), super.getContentText());
            case ChatMessage.MSG_TYPE_PAID_COUPON_TIP: {
                String text = super.getContentText().toString();
                String[] msg = text.split("&");
                String couponTitle = "";
                if (msg.length > 0) {
                    couponTitle = msg[0];
                }
                return String.format("%s购买了您 ＂%s＂点钟券", getUserName(), couponTitle);
            }

            default:
                return super.getContentText();
        }
    }

    public int getIconResourcesId() {
        switch (getAttrType()) {
            case ChatMessage.MSG_TYPE_COUPON_TIP:
                return R.drawable.tip_icon_coupon;
            case ChatMessage.MSG_TYPE_PAID_COUPON_TIP:
                return R.drawable.tip_icon_paid_coupon;
            case ChatMessage.MSG_TYPE_REWARD:
                return R.drawable.tip_icon_reward;
            default:
                return -1;
        }
    }

    public static TipChatMessage create(String remoteChatId, String tip) {
        return create(remoteChatId, tip, "");
    }

    public static TipChatMessage create(String remoteChatId, String tip, String tipType) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(tip, remoteChatId);
        return new TipChatMessage(emMessage, tipType);
    }
}
