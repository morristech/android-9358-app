package com.xmd.chat.message;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.R;
import com.xmd.chat.event.EventReplayDiceGame;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mo on 17-7-7.
 * 提示消息
 */

public class TipChatMessage extends ChatMessage {
    public static String ATTR_TIP_TYPE = "tipType";

    public static final String TIP_TYPE_PLAY_DICE = "tip_type_play_dice";

    public TipChatMessage(EMMessage emMessage) {
        super(emMessage);
    }


    public String getAttrType() {
        String attrType = getSafeStringAttribute(ATTR_TIP_TYPE);
        if (attrType == null) {
            attrType = "";
        }
        return attrType;
    }

    public boolean needSetMovementMethod() {
        return getAttrType().equals(TIP_TYPE_PLAY_DICE);
    }

    public CharSequence getTip() {
        switch (getAttrType()) {
            case ChatMessage.MSG_TYPE_COUPON_TIP:
                return  super.getContentText();
            case ChatMessage.MSG_TYPE_PAID_COUPON_TIP: {
                return super.getContentText().toString();
            }
            case TIP_TYPE_PLAY_DICE: {
                String msg = super.getOriginContentText();
                SpannableString s = new SpannableString(msg);
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        EventBus.getDefault().post(new EventReplayDiceGame());
                    }
                };
                if (msg.contains("再玩一局")) {
                    s.setSpan(span, s.length() - 4, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                } else {
                    s.setSpan(span, 0, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                return s;
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
