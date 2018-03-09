package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;
import com.tencent.imsdk.TIMMessage;
import com.xmd.chat.xmdchat.ImMessageParseManager;
import com.xmd.chat.xmdchat.constant.XmdMessageType;
import com.xmd.chat.xmdchat.messagebean.CouponMessageBean;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.ImChatMessageManagerPresent;

/**
 * Created by mo on 17-7-10.
 * 优惠券消息
 */

public class CouponChatMessage<T> extends ChatMessage {
    private final static String ATTR_COUPON_ID = "actId";
    private final static String ATTR_INVITE_CODE = "techCode";

    private String typeText;
    private String couponDescription;
    private String timeLimit;

    public CouponChatMessage(T message) {
        super(message);
        setMsgType(MSG_TYPE_COUPON);
        String content = "";
        if(XmdChatModel.getInstance().chatModelIsEm()){
          content  = getOriginContentText().toString();
        }else {
          content = ImMessageParseManager.getContent((TIMMessage) message,null).toString();
        }
        String[] str = content.split("<b>|</b>|<span>|</span>|<i>|</i>");
        typeText = str.length > 1 ? str[1] : "--";
        couponDescription = str.length > 4 ? str[2] + str[3] + str[4] : "--";
        timeLimit = str.length > 5 ? str[5] : "--";
    }

    public static CouponChatMessage create(String remoteChatId, boolean paid, String couponId, String couponText, String inviteCode,String actId,
                                           String typeName,String timeLimit) {
        if(XmdChatModel.getInstance().chatModelIsEm()){
            EMMessage emMessage = EMMessage.createTxtSendMessage(couponText, remoteChatId);
            CouponChatMessage chatMessage = new CouponChatMessage(emMessage);
            chatMessage.setAttr(ATTR_COUPON_ID, couponId);
            chatMessage.setAttr(ATTR_INVITE_CODE, inviteCode);
            chatMessage.setMsgType(paid ? MSG_TYPE_PAID_COUPON : MSG_TYPE_COUPON);
            return chatMessage;
        }else{
            CouponMessageBean bean = new CouponMessageBean();
            bean.setActId(actId);
            bean.setTechCode(inviteCode);
            bean.setTypeName(typeName);
            bean.setDescription(couponText);
            bean.setTimeLimit(timeLimit);
            TIMMessage timMessage = ImChatMessageManagerPresent.wrapMessage(bean, XmdMessageType.COUPON_TYPE,null,null);
            CouponChatMessage chatMessage = new CouponChatMessage(timMessage);
            return chatMessage;
        }

    }

    public String getTypeText() {
        return typeText;
    }

    public String getCouponDescription() {
        return couponDescription;
    }

    public String getTimeLimit() {
        return timeLimit;
    }
}
