package com.xmd.chat.message;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.tencent.imsdk.TIMMessage;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.chat.R;
import com.xmd.chat.beans.Marketing;
import com.xmd.chat.beans.OnceCard;
import com.xmd.chat.xmdchat.constant.XmdMessageType;
import com.xmd.chat.xmdchat.messagebean.MarketingMessageBean;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.ImChatMessageManagerPresent;

/**
 * Created by mo on 17-7-10.
 * 分享内容
 */

public class ShareChatMessage<T> extends ChatMessage {
    private static final String ATTR_ACT_ID = "actId";
    private static final String ATTR_ACT_NAME = "actName";

    private static final String ATTR_TEMPLATE_ID = "templateId";

    private static final String ATTR_CARD_TYPE = "cardType";

    public ShareChatMessage(T emMessage) {
        super(emMessage);
    }

    public static ShareChatMessage createJournalMessage(String remoteChatId, String actId, String templateId, String actName) {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMMessage emMessage = EMMessage.createTxtSendMessage(ResourceUtils.getString(R.string.club_journal_message), remoteChatId);
            ShareChatMessage message = new ShareChatMessage(emMessage);
            message.setMsgType(MSG_TYPE_JOURNAL);
            message.setAttr(ATTR_TEMPLATE_ID, templateId);
            message.setAttr(ATTR_ACT_NAME, actName == null ? "" : actName);
            message.setAttr(ATTR_ACT_ID, actId);
            return message;
        } else {
            MarketingMessageBean bean = new MarketingMessageBean();
            bean.setActId(actId);
            bean.setActName(actName);
            bean.setTemplateId(templateId);
            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(bean, XmdMessageType.JOURNAL_TYPE, null, null);
            ShareChatMessage shareChatMessage = new ShareChatMessage(message);
            return shareChatMessage;
        }

    }

    public static ShareChatMessage createOnceCardMessage(String remoteChatId, OnceCard card) {
        String content = "";
        switch (card.cardType) {
            case OnceCard.CARD_TYPE_SINGLE:
                content = ResourceUtils.getString(R.string.once_card_single);
                break;
            case OnceCard.CARD_TYPE_MIX:
                content = ResourceUtils.getString(R.string.once_card_mix);
                break;
            case OnceCard.CARD_TYPE_CREDIT:
                content = ResourceUtils.getString(R.string.once_card_credit);
                break;
        }
        if(XmdChatModel.getInstance().chatModelIsEm()){
            EMMessage emMessage = EMMessage.createTxtSendMessage(content, remoteChatId);
            ShareChatMessage message = new ShareChatMessage(emMessage);
            message.setMsgType(MSG_TYPE_ONCE_CARD);
            message.setAttr(ATTR_CARD_TYPE, card.cardType);
            message.setAttr(ATTR_ACT_NAME, card.name);
            message.setAttr(ATTR_ACT_ID, card.id);
            return message;
        }else {
            MarketingMessageBean marketingBean = new MarketingMessageBean();
            marketingBean.setActId(card.id);
            marketingBean.setActName(card.name);
            marketingBean.setCardType(card.cardType);
//            switch (card.cardType) {
//                case OnceCard.CARD_TYPE_SINGLE:
//                    marketingBean.setCardType(ResourceUtils.getString(R.string.once_card_single));
//                    break;
//                case OnceCard.CARD_TYPE_MIX:
//                    marketingBean.setCardType(ResourceUtils.getString(R.string.once_card_mix));
//                    break;
//                case OnceCard.CARD_TYPE_CREDIT:
//                    marketingBean.setCardType(ResourceUtils.getString(R.string.once_card_credit));
//                    break;
//            }

            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(marketingBean, XmdMessageType.ITEM_CARD_TYPE, null, null);
            ShareChatMessage shareMessage = new ShareChatMessage(message);
            return shareMessage;
        }

    }

    public static ShareChatMessage createMarketingTimeLimitMessage(String remoteChatId, Marketing marketing) {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMMessage emMessage = EMMessage.createTxtSendMessage(ResourceUtils.getString(R.string.time_limit_marketing), remoteChatId);
            ShareChatMessage message = new ShareChatMessage(emMessage);
            message.setMsgType(MSG_TYPE_TIME_LIMIT_TYPE);
            message.setAttr(ATTR_ACT_NAME, marketing.itemName);
            message.setAttr(ATTR_ACT_ID, TextUtils.isEmpty(marketing.actId) ? marketing.id : marketing.actId);
            return message;
        } else {
            MarketingMessageBean marketingBean = new MarketingMessageBean();
            marketingBean.setActId(TextUtils.isEmpty(marketing.actId) ? marketing.id : marketing.actId);
            marketingBean.setActName(marketing.itemName);
            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(marketingBean, XmdMessageType.TIME_LIMIT_TYPE, null, null);
            ShareChatMessage shareMessage = new ShareChatMessage(message);
            return shareMessage;
        }

    }

    public static ShareChatMessage createMarketingOneYuanMessage(String remoteChatId, Marketing marketing) {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMMessage emMessage = EMMessage.createTxtSendMessage(ResourceUtils.getString(R.string.one_yuan_marketing), remoteChatId);
            ShareChatMessage message = new ShareChatMessage(emMessage);
            message.setMsgType(MSG_TYPE_ONE_YUAN_TYPE);
            message.setAttr(ATTR_ACT_NAME, marketing.actName);
            message.setAttr(ATTR_ACT_ID, TextUtils.isEmpty(marketing.actId) ? marketing.id : marketing.actId);
            return message;
        } else {
            MarketingMessageBean bean = new MarketingMessageBean();
            bean.setActName(marketing.actName);
            bean.setActId(marketing.actId);
            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(bean, XmdMessageType.ONE_YUAN_TYPE, null, null);
            ShareChatMessage shareChatMessage = new ShareChatMessage(message);
            return shareChatMessage;
        }

    }

    public static ShareChatMessage createMarketingLuckWheelMessage(String remoteChatId, Marketing marketing) {

        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMMessage emMessage = EMMessage.createTxtSendMessage(ResourceUtils.getString(R.string.luck_wheel_marketing), remoteChatId);
            ShareChatMessage message = new ShareChatMessage(emMessage);
            message.setMsgType(MSG_TYPE_LUCKY_WHEEL_TYPE);
            message.setAttr(ATTR_ACT_NAME, marketing.actName);
            message.setAttr(ATTR_ACT_ID, TextUtils.isEmpty(marketing.actId) ? marketing.id : marketing.actId);
            return message;
        } else {
            MarketingMessageBean bean = new MarketingMessageBean();
            bean.setActName(marketing.actName);
            bean.setActId(marketing.actId);
            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(bean, XmdMessageType.LUCKY_WHEEL_TYPE, null, null);
            ShareChatMessage shareChatMessage = new ShareChatMessage(message);
            return shareChatMessage;
        }
    }

    public static ShareChatMessage createInvitationMessage(String remoteChatId) {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMMessage emMessage = EMMessage.createTxtSendMessage(ResourceUtils.getString(R.string.invitation_marketing), remoteChatId);
            ShareChatMessage message = new ShareChatMessage(emMessage);
            message.setMsgType(MSG_TYPE_INVITE_GIFT_TYPE);
            return message;
        } else {
            MarketingMessageBean bean = new MarketingMessageBean();
            bean.setActName(XmdMessageType.INVITE_GIFT_TYPE);
            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(bean, XmdMessageType.INVITE_GIFT_TYPE, null, null);
            ShareChatMessage shareMessage = new ShareChatMessage(message);
            return shareMessage;
        }

    }


    public String getActName() {
        return getSafeStringAttribute(ATTR_ACT_NAME);
    }

    public String getCardType() {
        return getSafeStringAttribute(ATTR_CARD_TYPE);
    }
}
