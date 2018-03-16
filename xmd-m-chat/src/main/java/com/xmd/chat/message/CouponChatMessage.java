package com.xmd.chat.message;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.tencent.imsdk.TIMMessage;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.chat.R;
import com.xmd.chat.xmdchat.ImMessageParseManager;
import com.xmd.chat.xmdchat.constant.XmdChatConstant;
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
//        String content = "";
//        if(XmdChatModel.getInstance().chatModelIsEm()){
//          content  = getOriginContentText().toString();
//        }else {
//          content = ImMessageParseManager.getContent((TIMMessage) message,null).toString();
//        }
//        XLogger.i(">>>","content>"+content);
//        String[] str = content.split("<b>|</b>|<span>|</span>|<i>|</i>");
//        typeText = str.length > 1 ? str[1] : "--";
//        couponDescription = str.length > 4 ? str[2] + str[3] + str[4] : "--";
//        timeLimit = str.length > 5 ? str[5] : "--";

    }

    public static CouponChatMessage create(String remoteChatId, boolean isPaid, String actId, String techCode, String typeName, String couponName, String discountValue, String validPeriod) {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMMessage emMessage = EMMessage.createTxtSendMessage(couponName, remoteChatId);
            CouponChatMessage chatMessage = new CouponChatMessage(emMessage);
            chatMessage.setAttr(ATTR_COUPON_ID, actId);
            chatMessage.setAttr(ATTR_INVITE_CODE, techCode);
            chatMessage.setMsgType(isPaid ? MSG_TYPE_PAID_COUPON : MSG_TYPE_COUPON);
            return chatMessage;
        } else {
            CouponMessageBean bean = new CouponMessageBean();
            bean.setActId(actId);
            bean.setTechCode(techCode);
            if(isPaid){
                bean.setDiscountValue(discountValue);
            }else {
                bean.setTypeName(typeName);
                bean.setCouponName(couponName);
            }
            bean.setValidPeriod(validPeriod);
            TIMMessage timMessage = ImChatMessageManagerPresent.wrapMessage(bean,isPaid ?XmdMessageType.PAID_COUPON_TYPE : XmdMessageType.COUPON_TYPE,null,null);
            CouponChatMessage chatMessage = new CouponChatMessage(timMessage);
            return chatMessage;
        }
    }

    public String getTypeText() {
        if(TextUtils.isEmpty(ImMessageParseManager.getContent((TIMMessage) message, XmdChatConstant.CHAT_KEY_TYPE_NAME).toString())){
            return ResourceUtils.getString(R.string.request_paid_coupon);
        }else {
            return ImMessageParseManager.getContent((TIMMessage) message,XmdChatConstant.CHAT_KEY_TYPE_NAME).toString();
        }
    }

    public String getCouponDescription() {
        if(TextUtils.isEmpty(ImMessageParseManager.getContent((TIMMessage) message,XmdChatConstant.CHAT_KEY_COUPON_NAME).toString())){
            return String.format("立减%s元",String.valueOf(ImMessageParseManager.getContent((TIMMessage) message,XmdChatConstant.CHAT_KEY_DICCOUNT_VALUE).toString()));
        }else
//        if(ImMessageParseManager.getContent((TIMMessage) message,XmdChatConstant.CHAT_KEY_COUPON_NAME).toString().equals("折扣券")){
//
//        }else
        {
            return ImMessageParseManager.getContent((TIMMessage) message,XmdChatConstant.CHAT_KEY_COUPON_NAME).toString();
        }
    }

    public String getTimeLimit() {
        return ImMessageParseManager.getContent((TIMMessage) message,XmdChatConstant.CHAT_KEY_VALID_PERIOD).toString();
    }
}
