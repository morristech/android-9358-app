package com.xmd.chat.message;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.R;
import com.xmd.chat.xmdchat.model.XmdChatModel;

/**
 * Created by mo on 17-7-7.
 * 提示消息
 */

public class TipChatMessage<T> extends ChatMessage {
    public static String ATTR_TIP_TYPE = "tipType";

    public static final String TIP_TYPE_PLAY_DICE = "tip_type_play_dice";

    public TipChatMessage(T message) {
        super(message);
    }


    public String getAttrType() {
        String attrType = "";
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            attrType = getSafeStringAttribute(ATTR_TIP_TYPE);
        } else {
            attrType = getMsgType();
        }
        return TextUtils.isEmpty(attrType) ? "" : attrType;
    }

    public boolean needSetMovementMethod() {
        // return getAttrType().equals(TIP_TYPE_PLAY_DICE);
        return false;
    }

    public CharSequence getTip() {
        XLogger.i(">>>","getTip>"+getAttrType());
        switch (getAttrType()) {
            case ChatMessage.MSG_TYPE_COUPON_TIP:
                return XmdChatModel.getInstance().chatModelIsEm() ? super.getContentText() : String.format("领取了您的%s",getSafeStringAttribute("name"));
            case ChatMessage.MSG_TYPE_PAID_COUPON_TIP: {
                return XmdChatModel.getInstance().chatModelIsEm() ? super.getContentText().toString() : String.format("购买了您的%s",getSafeStringAttribute("name"));
            }
            case ChatMessage.REVERT_MSG:
                return "撤回一条消息";
//            case TIP_TYPE_PLAY_DICE: {
//                String msg = super.getOriginContentText();
//                SpannableString s = new SpannableString(msg);
//                ClickableSpan span = new ClickableSpan() {
//                    @Override
//                    public void onClick(View widget) {
//                        EventBus.getDefault().post(new EventReplayDiceGame());
//                    }
//                };
//                if (msg.contains("再玩一局")) {
//                    s.setSpan(span, s.length() - 4, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                } else {
//                    s.setSpan(span, 0, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                }
//                return s;
//            }
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

    public static TipChatMessage create(EMMessage emMessage, String tipType) {
        TipChatMessage message = new TipChatMessage(emMessage);
        message.setMsgType(ChatMessage.MSG_TYPE_TIP);
        message.setAttr(ATTR_TIP_TYPE, tipType);
        return message;
    }

    public static TipChatMessage create(String remoteChatId, String tip, String tipType) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(tip, remoteChatId);
        return create(emMessage, tipType);
    }
}
