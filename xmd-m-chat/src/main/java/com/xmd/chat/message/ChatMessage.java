package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by heyangya on 17-6-5.
 * 基本聊天消息
 */

public class ChatMessage {

    //订单
    public static final String MSG_TYPE_ORDER = "order";

    public static final String MSG_TYPE_ORDER_START = "order_start";
    public static final String MSG_TYPE_ORDER_REFUSE = "order_refuse";
    public static final String MSG_TYPE_ORDER_CONFIRM = "order_confirm";
    public static final String MSG_TYPE_ORDER_CANCEL = "order_cancel";
    public static final String MSG_TYPE_ORDER_SUCCESS = "order_success";
    public static final String MSG_TYPE_ORDER_REQUEST = "order_request"; //求预约

    public static final String MSG_TAG_CUSTOMER_SERVICE = "customer_service";


    public static final String ATTRIBUTE_MESSAGE_TYPE = "msgType";
    private static final String ATTRIBUTE_TAG = "xmd_tag";

    private static final String ATTRIBUTE_USER_ID = "userId";
    private static final String ATTRIBUTE_USER_NAME = "name";
    private static final String ATTRIBUTE_USER_AVATAR = "header";
    private static final String ATTRIBUTE_TIME = "time";

    private static final String ATTRIBUTE_SERIAL_NO = "no";
    private static final String ATTRIBUTE_TECH_ID = "techId";
    private static final String ATTRIBUTE_CLUB_ID = "clubId";
    private static final String ATTRIBUTE_CLUB_NAME = "clubName";


    //预约消息
    private EMMessage emMessage;

    public ChatMessage(EMMessage emMessage, String msgType) {
        this.emMessage = emMessage;
        setAttr(ATTRIBUTE_MESSAGE_TYPE, msgType);
    }

    //没有找到则返回 "none"字符串，方便switch判断
    public String getMsgType() {
        String msgType = getSafeStringAttribute(ATTRIBUTE_MESSAGE_TYPE);
        return msgType == null ? "none" : msgType;
    }

    public String getTag() {
        return getSafeStringAttribute(ATTRIBUTE_TAG);
    }

    public void setTag(String tag) {
        setAttr(ATTRIBUTE_TAG, tag);
    }

    public String getToChatId() {
        return emMessage.getTo();
    }

    public String getFromChatId() {
        return emMessage.getFrom();
    }

    public String getSafeStringAttribute(String key) {
        try {
            return emMessage.getStringAttribute(key);
        } catch (HyphenateException e) {
            return null;
        }
    }

    public Integer getSafeIntergeAttribute(String key) {
        try {
            return emMessage.getIntAttribute(key);
        } catch (HyphenateException e) {
            return null;
        }
    }

    public Long getSafeLongAttribute(String key) {
        try {
            return emMessage.getLongAttribute(key);
        } catch (HyphenateException e) {
            return null;
        }
    }

    public Boolean getSafeBooleanAttribute(String key) {
        try {
            return emMessage.getBooleanAttribute(key);
        } catch (HyphenateException e) {
            return null;
        }
    }

    public String getUserId() {
        return getSafeStringAttribute(ATTRIBUTE_USER_ID);
    }

    public void setUserId(String userId) {
        setAttr(ATTRIBUTE_USER_ID, userId);
    }

    public String getUserName() {
        return getSafeStringAttribute(ATTRIBUTE_USER_NAME);
    }

    public void setUserName(String userName) {
        setAttr(ATTRIBUTE_USER_NAME, userName);
    }

    public String getUserAvatar() {
        return getSafeStringAttribute(ATTRIBUTE_USER_AVATAR);
    }

    public void setUserAvatar(String userAvatar) {
        setAttr(ATTRIBUTE_USER_AVATAR, userAvatar);
    }

    public String getTime() {
        return getSafeStringAttribute(ATTRIBUTE_TIME);
    }

    public void setTime(String time) {
        setAttr(ATTRIBUTE_TIME, time);
    }

    public String getTechId() {
        return getSafeStringAttribute(ATTRIBUTE_TECH_ID);
    }

    public void setTechId(String techId) {
        setAttr(ATTRIBUTE_TECH_ID, techId);
    }

    public String getTechNo() {
        return getSafeStringAttribute(ATTRIBUTE_SERIAL_NO);
    }

    public void setTechNo(String techNo) {
        setAttr(ATTRIBUTE_SERIAL_NO, techNo);
    }

    public String getClubId() {
        return getSafeStringAttribute(ATTRIBUTE_CLUB_ID);
    }

    public void setClubId(String clubId) {
        setAttr(ATTRIBUTE_CLUB_ID, clubId);
    }

    public String getClubName() {
        return getSafeStringAttribute(ATTRIBUTE_CLUB_NAME);
    }

    public void setClubName(String clubName) {
        setAttr(ATTRIBUTE_CLUB_NAME, clubName);
    }


    protected void setAttr(String key, String value) {
        emMessage.setAttribute(key, value);
    }

    protected void setAttr(String attrKey, Long attr) {
        emMessage.setAttribute(attrKey, attr);
    }

    protected void setAttr(String attrKey, Integer attr) {
        emMessage.setAttribute(attrKey, attr);
    }

    protected void setAttr(String attrKey, Boolean attr) {
        emMessage.setAttribute(attrKey, attr);
    }

    public EMMessage getEmMessage() {
        return emMessage;
    }

    public void setEmMessage(EMMessage emMessage) {
        this.emMessage = emMessage;
    }

    public boolean isCustomerService() {
        return getTag() != null && getTag().contains(MSG_TAG_CUSTOMER_SERVICE);
    }
}
