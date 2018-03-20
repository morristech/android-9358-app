package com.xmd.chat.xmdchat;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.ext.message.TIMMessageExt;
import com.xmd.app.EmojiManager;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.chat.R;
import com.xmd.chat.XmdChat;
import com.xmd.chat.beans.OnceCard;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.xmdchat.constant.XmdChatConstant;
import com.xmd.chat.xmdchat.constant.XmdMessageType;
import com.xmd.chat.xmdchat.messagebean.CouponMessageBean;
import com.xmd.chat.xmdchat.messagebean.OrderAppointmentMessageBean;
import com.xmd.chat.xmdchat.messagebean.RewardMessageBean;
import com.xmd.chat.xmdchat.messagebean.TextMessageBean;
import com.xmd.chat.xmdchat.messagebean.XmdChatMessageBaseBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Lhj on 18-1-29.
 * 该类用于解析IM消息内容
 */

public class ImMessageParseManager {

    private static ImMessageParseManager instance;

    private ImMessageParseManager() {

    }

    public static ImMessageParseManager getInstance() {
        if (instance == null) {
            synchronized (ImMessageParseManager.class) {
                instance = new ImMessageParseManager();
            }
        }
        return instance;
    }

    /**
     * 获取消息类型
     */
    public String getMessageType(TIMMessage message) {
        return getIndexByMessage(message, XmdChatConstant.BASE_INDEX_TYPE);
    }

    /**
     * 获取消息Tag
     */

    public String getMessageTag(TIMMessage message){
        return getIndexByMessage(message,XmdChatConstant.BASE_INDEX_TAG);
    }

    /**
     * @param message
     * @param index
     * @return 根据key值获取内容
     */
    public static String getIndexByMessage(TIMMessage message, String index) {
        if (message == null) {
            return null;
        }
        String indexInfo = "";
        TIMCustomElem elem;
        if (message.getElement(0) instanceof TIMCustomElem) {
            elem = (TIMCustomElem) message.getElement(0);
        } else if(message.getElementCount() > 1 && message.getElement(1) instanceof TIMCustomElem){
            elem = (TIMCustomElem) message.getElement(1);
        }else{
            return indexInfo;
        }
        try {
            String str = new String(elem.getData(), "UTF-8");
            JSONObject jsonObject = new JSONObject(str);
            indexInfo = jsonObject.getString(index);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
            indexInfo = "";
        }
        return indexInfo;
    }

    public static CharSequence getContent(TIMMessage message, String key) {
        if (message == null) {
            XToast.show("消息为空，获取不到相关内容");
            return null;
        } else if (!TextUtils.isEmpty(key) && key.equals(ChatMessage.ATTR_INNER_PROCESSED)) {
            TIMMessageExt ext = new TIMMessageExt(message);
            return ext.getCustomStr();
        }
        TIMCustomElem elem;
        if (message.getElement(0) instanceof TIMCustomElem ) {
            elem = (TIMCustomElem) message.getElement(0);
        } else if(message.getElementCount() > 1 && message.getElement(1) instanceof TIMCustomElem){
            elem = (TIMCustomElem) message.getElement(1);
        }else{
            return TextUtils.isEmpty(key)?"":key;
        }

        if (TextUtils.isEmpty(key)) {
            return getContentParse(elem.getData());
        } else if (key.equals(XmdChatConstant.BASE_INDEX_TAG)) {
            return getIndexByMessage(message, XmdChatConstant.BASE_INDEX_TAG);
        } else {
            return getContentByKey(elem.getData(), key);
        }

    }


    public static String parseToAppointmentInfo(TIMMessage message, String key) {
        String content = null;
        if (message == null) {
            XToast.show("消息未空，获取不到相关内容");
            return null;
        }
        try {
            TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
            String str = new String(elem.getData(), "UTF-8");
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(str);
            JsonObject root = element.getAsJsonObject();
            JsonObject data = root.getAsJsonObject("data");
            Gson gson = new Gson();
            OrderAppointmentMessageBean orderBean = gson.fromJson(data, OrderAppointmentMessageBean.class);
            switch (key) {
                case XmdMessageType.ORDER_TYPE_ORDER_ID:
                    content = orderBean.getOrderId();
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_PAY_MONEY:
                    content = orderBean.getOrderPayMoney() != null ? String.valueOf(orderBean.getOrderPayMoney()) : null;
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_TECH_ID:
                    content = orderBean.getOrderTechId();
                    break;
                case XmdMessageType.ORDER_TYPE_TECH_NAME:
                    content = orderBean.getOrderTechName();
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_TECH_AVATAR:
                    content = orderBean.getOrderTechAvatar();
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_SERVICE_ID:
                    content = orderBean.getOrderServiceId();
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_SERVICE_NAME:
                    content = orderBean.getOrderServiceName();
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_SERVICE_TIME:
                    content = String.valueOf(orderBean.getOrderServiceTime());
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_SERVICE_DURATION:
                    content = String.valueOf(orderBean.getOrderServiceDuration());
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_CUSTOMER_ID:
                    content = orderBean.getOrderCustomerId();
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_CUSTOMER_NAME:
                    content = orderBean.getOrderCustomerName();
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_CUSTOMER_PHONE:
                    content = orderBean.getOrderCustomerPhone();
                    break;
                case XmdMessageType.ORDER_TYPE_ORDER_SERVICE_PRICE:
                    content = orderBean.getOrderServicePrice();
                    break;
                default:
                    content = "";
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return content;
    }

    public static CharSequence getContentByKey(byte[] data, String key) {
        try {
            String str = new String(data, "UTF-8");
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(str);
            JsonObject root = element.getAsJsonObject();
            JsonObject object = root.getAsJsonObject(XmdChatConstant.BASE_INDEX_DATA);
            JSONObject jsonObject = new JSONObject(object.toString());
            String dataKey = jsonObject.getString(key);
            return dataKey;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static CharSequence getContentParse(byte[] data) {
        String content = "";
        String messageType = "";
        try {
            String str = new String(data, "UTF-8");
            XLogger.i(XmdChat.TAG, "contentParse>" + str);
            Gson gson = new Gson();
            XmdChatMessageBaseBean messageBean = gson.fromJson(str, XmdChatMessageBaseBean.class);
            messageType = messageBean.getType();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(str);
            JsonObject root = element.getAsJsonObject();
            JsonObject obj = root.getAsJsonObject(XmdChatConstant.BASE_INDEX_DATA);
            if (messageType.equals(XmdMessageType.TEXT_TYPE)) {
                TextMessageBean textBean = new Gson().fromJson(obj, TextMessageBean.class);
                content = textBean.getContent();
                return EmojiManager.getInstance().format(content);
            } else {
                switch (messageType) {
                    case XmdMessageType.COUPON_TYPE:
                        CouponMessageBean couponBean = new Gson().fromJson(obj, CouponMessageBean.class);
                        content = couponBean.getTypeName();
                        break;
                    case XmdMessageType.PAID_COUPON_TYPE:
                        content = ResourceUtils.getString(R.string.request_paid_coupon);
                        break;
                    case XmdMessageType.REQUEST_REWARD_TYPE:
                        content = ResourceUtils.getString(R.string.request_reward_message);
                        break;
                    case XmdMessageType.ORDER_REQUEST_TYPE:
                        content = ResourceUtils.getString(R.string.order_request_message);
                        break;
                    case XmdMessageType.INVITE_GIFT_TYPE:
                        content = ResourceUtils.getString(R.string.invite_gift_message);
                        break;
                    case XmdMessageType.CLUB_LOCATION_TYPE:
                        content = ResourceUtils.getString(R.string.location_message);
                        break;
                    case XmdMessageType.TIME_LIMIT_TYPE:
                        content = ResourceUtils.getString(R.string.time_limit_marketing);
                        break;
                    case XmdMessageType.LUCKY_WHEEL_TYPE:
                        content = ResourceUtils.getString(R.string.luck_wheel_marketing);
                        break;
                    case XmdMessageType.JOURNAL_TYPE:
                        content = ResourceUtils.getString(R.string.club_journal_message);
                        break;
                    case XmdMessageType.ITEM_CARD_TYPE:
                        String cardType = getContentByKey(data, "cardType").toString();
                        switch (cardType) {
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
                        break;
                    case XmdMessageType.IMAGE_TYPE:
                        content = ResourceUtils.getString(R.string.image_message);
                        break;
                    case XmdMessageType.COUPON_TIP_TYPE:
                        content = ResourceUtils.getString(R.string.coupon_tip_message);
                        break;
                    case XmdMessageType.PAID_COUPON_TIP_TYPE:
                        content = ResourceUtils.getString(R.string.paid_coupon_tip_message);
                        break;
                    case XmdMessageType.ORDER_START_TYPE:
                    case XmdMessageType.ORDER_REFUSE_TYPE:
                    case XmdMessageType.ORDER_CONFIRM_TYPE:
                    case XmdMessageType.ORDER_CANCEL_TYPE:
                    case XmdMessageType.ORDER_SUCCESS_TYPE:
                        content = ResourceUtils.getString(R.string.order_message);
                        break;
                    case XmdMessageType.REWARD_TYPE:
                        RewardMessageBean rewardMessageBean = new Gson().fromJson(obj, RewardMessageBean.class);
                        content = rewardMessageBean.getAmount();
                        break;
                    case XmdMessageType.REVERT_MSG_TYPE:
                        content = ResourceUtils.getString(R.string.has_revoke_message);
                        break;
                    default:
                        content = messageType;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return TextUtils.isEmpty(content) ? "" : content;
    }

    public static CharSequence getLastMessageContentParse(byte[] data) {
        String content = "";
        String messageType = "";
        try {
            String str = new String(data, "UTF-8");
            Gson gson = new Gson();
            XmdChatMessageBaseBean messageBean = gson.fromJson(str, XmdChatMessageBaseBean.class);
            messageType = messageBean.getType();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(str);
            JsonObject root = element.getAsJsonObject();
            JsonObject obj = root.getAsJsonObject(XmdChatConstant.BASE_INDEX_DATA);
            if (messageType.equals(XmdMessageType.TEXT_TYPE)) {
                TextMessageBean textBean = new Gson().fromJson(obj, TextMessageBean.class);
                content = textBean.getContent();
                return EmojiManager.getInstance().format(content);
            } else {
                switch (messageType) {
                    case XmdMessageType.COUPON_TYPE:
                        CouponMessageBean couponBean = new Gson().fromJson(obj, CouponMessageBean.class);
                        content = String.format("[%s]%s", couponBean.getTypeName(), couponBean.getCouponName());
                        break;
                    case XmdMessageType.PAID_COUPON_TYPE:
                        content = "[求点钟]";
                        break;
                    case XmdMessageType.REQUEST_REWARD_TYPE:
                        content = "[求打赏]";
                        break;
                    case XmdMessageType.ORDER_REQUEST_TYPE:
                        content = "[求预约]";
                        break;
                    case XmdMessageType.INVITE_GIFT_TYPE:
                        content = "[邀请有礼]";
                        break;
                    case XmdMessageType.CLUB_LOCATION_TYPE:
                        content = ResourceUtils.getString(R.string.location_message);
                        break;
                    case XmdMessageType.TIME_LIMIT_TYPE:
                        content = "[限时购]";
                        break;
                    case XmdMessageType.LUCKY_WHEEL_TYPE:
                        content = "[大转盘]";
                        break;
                    case XmdMessageType.JOURNAL_TYPE:
                        content = "[电子期刊]";
                        break;
                    case XmdMessageType.ITEM_CARD_TYPE:
                        String cardType = getContentByKey(data, "cardType").toString();
                        switch (cardType) {
                            case OnceCard.CARD_TYPE_SINGLE:
                                content = "[单项次卡]";
                                break;
                            case OnceCard.CARD_TYPE_MIX:
                                content = "[混合套餐]";
                                break;
                            case OnceCard.CARD_TYPE_CREDIT:
                                content = "[积分礼品]";
                                break;
                        }
                        break;
                    case XmdMessageType.IMAGE_TYPE:
                        content = ResourceUtils.getString(R.string.image_message);
                        break;
                    case XmdMessageType.COUPON_TIP_TYPE:
                        content = ResourceUtils.getString(R.string.coupon_tip_message);
                        break;
                    case XmdMessageType.PAID_COUPON_TIP_TYPE:
                        content = ResourceUtils.getString(R.string.paid_coupon_tip_message);
                        break;
                    case XmdMessageType.ORDER_START_TYPE:
                    case XmdMessageType.ORDER_REFUSE_TYPE:
                    case XmdMessageType.ORDER_CONFIRM_TYPE:
                    case XmdMessageType.ORDER_CANCEL_TYPE:
                    case XmdMessageType.ORDER_SUCCESS_TYPE:
                        content = ResourceUtils.getString(R.string.order_message);
                        break;
                    case XmdMessageType.REWARD_TYPE:
                        RewardMessageBean rewardMessageBean = new Gson().fromJson(obj, RewardMessageBean.class);
                        content = String.format("[打赏]金额%s元", rewardMessageBean.getAmount());
                        break;
                    case XmdMessageType.REVERT_MSG_TYPE:
                        content = ResourceUtils.getString(R.string.has_revoke_message);
                        break;
                    case XmdMessageType.VOICE_TYPE:
                        content = ResourceUtils.getString(R.string.voice_message);
                        break;
                    case XmdMessageType.CREDIT_GIFT_TYPE:
                        content = ResourceUtils.getString(R.string.credit_gift_message);
                        break;
                    default:
                        content = messageType;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return TextUtils.isEmpty(content) ? "" : content;
    }


}
