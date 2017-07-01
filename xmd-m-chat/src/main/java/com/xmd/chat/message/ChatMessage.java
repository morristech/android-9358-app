package com.xmd.chat.message;

import android.text.SpannableString;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.EmojiManager;

import java.util.Calendar;

/**
 * Created by heyangya on 17-6-5.
 * 基本聊天消息
 */

public class ChatMessage {
    private static final String TAG = "ChatMessage";
    //订单
    public static final String MSG_TYPE_ORDER = "order";

    /**
     * 原始数据类型:
     * TXT,IMAGE,VIDEO,LOCATION,VOICE,FILE,CMD
     * 自定义类型:
     */
    public static final String MSG_TYPE_ORIGIN_TXT = "TXT";
    public static final String MSG_TYPE_ORIGIN_IMAGE = "IMAGE";
    public static final String MSG_TYPE_ORIGIN_VIDEO = "VIDEO";
    public static final String MSG_TYPE_ORIGIN_LOCATION = "LOCATION";
    public static final String MSG_TYPE_ORIGIN_VOICE = "VOICE";
    public static final String MSG_TYPE_ORIGIN_FILE = "FILE";
    public static final String MSG_TYPE_ORIGIN_CMD = "CMD";
    public static final String MSG_TYPE_ORDER_START = "order_start";
    public static final String MSG_TYPE_ORDER_REFUSE = "order_refuse";
    public static final String MSG_TYPE_ORDER_CONFIRM = "order_confirm";
    public static final String MSG_TYPE_ORDER_CANCEL = "order_cancel";
    public static final String MSG_TYPE_ORDER_SUCCESS = "order_success";
    public static final String MSG_TYPE_ORDER_REQUEST = "order_request"; //求预约

    public static final String MSG_TAG_CUSTOMER_SERVICE = "customer_service";//客服消息
    public static final String MSG_TAG_HELLO = "hello"; //打招呼消息

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

    private SpannableString contentText; //缓存emoji格式化后的数据
    protected String formatTime; //缓存格式化后的时间

    public ChatMessage(EMMessage emMessage, String msgType) {
        this.emMessage = emMessage;
        setAttr(ATTRIBUTE_MESSAGE_TYPE, msgType);
    }

    //没有找到则返回 "none"字符串，方便switch判断
    public String getMsgType() {
        String msgType = getSafeStringAttribute(ATTRIBUTE_MESSAGE_TYPE);
        return TextUtils.isEmpty(msgType) ? emMessage.getType().name() : msgType;
    }

    public String getTag() {
        return getSafeStringAttribute(ATTRIBUTE_TAG);
    }

    public void addTag(String tag) {
        setAttr(ATTRIBUTE_TAG, TextUtils.isEmpty(getTag()) ? tag : getTag() + "," + tag);
    }

    public void clearTag() {
        setAttr(ATTRIBUTE_TAG, "");
    }

    public String getToChatId() {
        return emMessage.getTo();
    }

    public String getFromChatId() {
        return emMessage.getFrom();
    }

    public String getSafeStringAttribute(String key) {
        try {
            String value = emMessage.getStringAttribute(key);
//            XLogger.d(TAG, "get key=" + key + ",value=" + value);
            return value;
        } catch (HyphenateException e) {
//            XLogger.d(TAG, "get key=" + key + ",e=" + e.getMessage());
            return null;
        }
    }

    public Integer getSafeIntergeAttribute(String key) {
        try {
            Integer value = emMessage.getIntAttribute(key);
//            XLogger.d(TAG, "get key=" + key + ",value=" + value);
            return value;
        } catch (HyphenateException e) {
//            XLogger.d(TAG, "get key=" + key + ",e=" + e.getMessage());
            return null;
        }
    }

    public Long getSafeLongAttribute(String key) {
        try {
            Long value = emMessage.getLongAttribute(key);
//            XLogger.d(TAG, "get key=" + key + ",value=" + value);
            return value;
        } catch (HyphenateException e) {
//            XLogger.d(TAG, "get key=" + key + ",e=" + e.getMessage());
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
        EMClient.getInstance().chatManager().updateMessage(emMessage);
    }

    protected void setAttr(String attrKey, Long attr) {
        emMessage.setAttribute(attrKey, attr);
        EMClient.getInstance().chatManager().updateMessage(emMessage);
    }

    protected void setAttr(String attrKey, Integer attr) {
        emMessage.setAttribute(attrKey, attr);
        EMClient.getInstance().chatManager().updateMessage(emMessage);
    }

    protected void setAttr(String attrKey, Boolean attr) {
        emMessage.setAttribute(attrKey, attr);
        EMClient.getInstance().chatManager().updateMessage(emMessage);
    }

    public EMMessage getEmMessage() {
        return emMessage;
    }

    public void setEmMessage(EMMessage emMessage) {
        this.emMessage = emMessage;
    }

    public CharSequence getContentText() {
        if (contentText == null) {
            if (emMessage.getType().equals(EMMessage.Type.TXT)) {
                String message = ((EMTextMessageBody) emMessage.getBody()).getMessage();
                contentText = EmojiManager.getInstance().format(message);
            } else {
                contentText = new SpannableString("[" + emMessage.getType().name() + "]");
            }
        }
        return contentText;
    }

    public CharSequence getOriginContentText() {
        if (emMessage.getType().equals(EMMessage.Type.TXT)) {
            return ((EMTextMessageBody) emMessage.getBody()).getMessage();
        } else {
            return "[" + emMessage.getType().name() + "]";
        }
    }

    public boolean isCustomerService() {
        return getTag() != null && getTag().contains(MSG_TAG_CUSTOMER_SERVICE);
    }

    public String getRemoteChatId() {
        if (getEmMessage().direct() == EMMessage.Direct.RECEIVE) {
            return getFromChatId();
        } else {
            return getToChatId();
        }
    }

    public String getFormatTime() {
        if (formatTime == null) {
            long msgTime = emMessage.getMsgTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(msgTime);
            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.YEAR) > calendar.get(Calendar.YEAR)
                    || now.get(Calendar.DAY_OF_YEAR) - 2 >= calendar.get(Calendar.DAY_OF_YEAR)) {
                formatTime = DateUtils.doLong2String(msgTime);
            } else if (now.get(Calendar.DAY_OF_YEAR) - 1 >= calendar.get(Calendar.DAY_OF_YEAR)) {
                formatTime = DateUtils.doLong2String(msgTime, "昨天 HH:mm");
            } else {
                formatTime = DateUtils.doLong2String(msgTime, "HH:mm");
            }
        }
        return formatTime;
    }
}
