package com.xmd.chat.xmdchat.present;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.EmojiManager;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.TipChatMessage;
import com.xmd.chat.xmdchat.contract.XmdChatMessageInterface;

import java.util.Calendar;


/**
 * Created by Lhj on 18-1-22.
 */

public class EmChatMessagePresent implements XmdChatMessageInterface<EMMessage> {

    public EMMessage emMessage;
    protected String formatTime; //缓存格式化后的时间
    protected String relativeTime;
    private Spannable contentText; //缓存emoji格式化后的数据
    private static final String ATTR_INNER_PROCESSED = "inner_processed";
    public EmChatMessagePresent(EMMessage message) {
        emMessage = (EMMessage) message;
    }

    public static String getSafeStringAttribute(EMMessage emMessage, String key) {
        if (emMessage == null || TextUtils.isEmpty(key)) {
            return "";
        }
        try {
            return emMessage.getStringAttribute(key);
        } catch (HyphenateException e) {
            e.getLocalizedMessage();
            return "";
        }
    }

    @Override
    public String getMsgType() {
        String msgType = getSafeStringAttribute(emMessage, ChatMessage.ATTRIBUTE_MESSAGE_TYPE);
        return TextUtils.isEmpty(msgType) ? ChatMessage.MSG_TYPE_ORIGIN_TXT : msgType;
    }

    @Override
    public long getMsgTime() {
        return emMessage.getMsgTime();
    }

    @Override
    public void setMsgType(String msgType) {
        setAttr(ChatMessage.ATTRIBUTE_MESSAGE_TYPE, msgType);
    }

    @Override
    public String getMsgType(EMMessage message) {
        String msgType = getSafeStringAttribute(emMessage, ChatMessage.ATTRIBUTE_MESSAGE_TYPE);
        return TextUtils.isEmpty(msgType) ? ChatMessage.MSG_TYPE_ORIGIN_TXT : msgType;
    }

    @Override
    public String getToChatId() {
        return emMessage.getTo();
    }

    @Override
    public String getFromChatId() {
        return emMessage.getFrom();
    }

    @Override
    public void setUserId(String userId) {
        setAttr(ChatMessage.ATTRIBUTE_USER_ID, userId);
    }

    @Override
    public String getUserRoles() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_USER_ROLES);
    }

    @Override
    public void setUserRoles(String userRoles) {
        setAttr(ChatMessage.ATTRIBUTE_USER_ROLES, userRoles);
    }

    @Override
    public String getUserName() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_USER_NAME);
    }

    @Override
    public void setUserName(String userName) {
        setAttr(ChatMessage.ATTRIBUTE_USER_NAME, userName);
    }

    @Override
    public String getUserAvatar() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_USER_AVATAR);
    }

    @Override
    public void setUserAvatar(String userAvatar) {
        setAttr(ChatMessage.ATTRIBUTE_USER_AVATAR, userAvatar);
    }

    @Override
    public String getUserAvatarId() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_USER_AVATAR_ID);
    }

    @Override
    public void setUserAvatarId(String userAvatarId) {
        setAttr(ChatMessage.ATTRIBUTE_USER_AVATAR_ID, userAvatarId);
    }

    @Override
    public String getTime() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_TIME);
    }

    @Override
    public void setTime(String time) {
        setAttr(ChatMessage.ATTRIBUTE_TIME, time);
    }

    @Override
    public String getTechId() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_TECH_ID);
    }

    @Override
    public void setTechId(String techId) {
        setAttr(ChatMessage.ATTRIBUTE_TECH_ID, techId);
    }

    @Override
    public String getTechNo() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_SERIAL_NO);
    }

    @Override
    public void setTechNo(String techNo) {
        setAttr(ChatMessage.ATTRIBUTE_SERIAL_NO, techNo);
    }

    @Override
    public String getClubId() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_CLUB_ID);
    }

    @Override
    public void setClubId(String clubId) {
        setAttr(ChatMessage.ATTRIBUTE_CLUB_ID, clubId);
    }

    @Override
    public String getClubName() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_CLUB_NAME);
    }

    @Override
    public void setClubName(String clubName) {
        setAttr(ChatMessage.ATTRIBUTE_CLUB_NAME, clubName);
    }

    @Override
    public void addTag(String tag) {
        setAttr(ChatMessage.ATTRIBUTE_TAG, TextUtils.isEmpty(getSafeStringAttribute(ChatMessage.ATTRIBUTE_TAG)) ?
                tag : getSafeStringAttribute(ChatMessage.ATTRIBUTE_TAG) + "," + tag);
    }

    @Override
    public void clearTag() {

    }

    @Override
    public String getUserId() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_USER_ID);
    }

    @Override
    public CharSequence getContentText() {
        if (contentText == null) {
            if (emMessage.getType().equals(EMMessage.Type.TXT)) {
                String message = ((EMTextMessageBody) emMessage.getBody()).getMessage();
                if (getAttrType().equals(ChatMessage.MSG_TYPE_COUPON_TIP)) {
                    String couponMessage = String.format("%s领取了您的\"%s\"", getUserName(), message);
                    contentText = EmojiManager.getInstance().format(couponMessage);
                } else if (getAttrType().equals(ChatMessage.MSG_TYPE_PAID_COUPON_TIP)) {
                    String[] msg = message.split("&");
                    String couponTitle = "";
                    if (msg.length > 0) {
                        couponTitle = msg[0];
                    }
                    String paidType = String.format("%s购买了您 ＂%s＂点钟券", getUserName(), couponTitle);
                    contentText = EmojiManager.getInstance().format(paidType);
                } else {
                    contentText = EmojiManager.getInstance().format(message);
                }

            } else {
                contentText = new SpannableString("[" + emMessage.getType().name() + "]");
            }
        }
        return contentText;
    }

    @Override
    public String getOriginContentText() {
        if (emMessage.getType().equals(EMMessage.Type.TXT)) {
            return ((EMTextMessageBody) emMessage.getBody()).getMessage();
        } else {
            return "[" + emMessage.getType().name() + "]";
        }
    }

    @Override
    public boolean isCustomerService() {
        return getSafeStringAttribute(ChatMessage.ATTRIBUTE_TAG) != null && getSafeStringAttribute(ChatMessage.ATTRIBUTE_TAG)
                .contains(ChatMessage.MSG_TAG_CUSTOMER_SERVICE);
    }

    @Override
    public String getRemoteChatId() {
        return emMessage.getTo();
    }

    @Override
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

    @Override
    public String getChatRelativeTime() {
        if (relativeTime == null) {
            long msgTime = emMessage.getMsgTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(msgTime);
            int relativeHour = calendar.get(Calendar.HOUR_OF_DAY);
            Calendar now = Calendar.getInstance();
            String timeDistinguish = "";
            if (0 <= relativeHour && relativeHour < 6) {
                timeDistinguish = "凌晨";
            } else if (6 <= relativeHour && relativeHour < 12) {
                timeDistinguish = "早上";
            } else if (12 <= relativeHour && relativeHour < 13) {
                timeDistinguish = "中午";
            } else if (12 <= relativeHour && relativeHour < 18) {
                timeDistinguish = "下午";
            } else if (18 <= relativeHour && relativeHour < 24) {
                timeDistinguish = "晚上";
            }
            if (now.get(Calendar.YEAR) > calendar.get(Calendar.YEAR)
                    || now.get(Calendar.DAY_OF_YEAR) - 2 >= calendar.get(Calendar.DAY_OF_YEAR)) {
                relativeTime = DateUtils.doLong2RelativeString(msgTime);
            } else if (now.get(Calendar.DAY_OF_YEAR) - 1 >= calendar.get(Calendar.DAY_OF_YEAR)) {
                relativeTime = DateUtils.doLong2String(msgTime, "昨天 " + timeDistinguish + "hh:mm");
            } else {
                relativeTime = DateUtils.doLong2String(msgTime, timeDistinguish + "hh:mm");
            }

        }
        return relativeTime;
    }

    @Override
    public String getSafeStringAttribute(String key) {
        return getSafeStringAttribute(emMessage, key);
    }

    @Override
    public Integer getSafeIntegerAttribute(String key) {
        try {
            return emMessage.getIntAttribute(key);
        } catch (HyphenateException e) {
            return 0;
        }
    }

    @Override
    public Long getSafeLongAttribute(String key) {
        try {
            return emMessage.getLongAttribute(key);
        } catch (HyphenateException e) {
            return null;
        }
    }

    @Override
    public void setInnerProcessed(String processedDesc) {
        setAttr(ATTR_INNER_PROCESSED, processedDesc);
        ChatMessageManager.getInstance().saveMessage(new ChatMessage(emMessage)); //设置状态标记后，需要保存消息到本地
    }

    @Override
    public String getAttrType() {
        String attrType = getSafeStringAttribute(TipChatMessage.ATTR_TIP_TYPE);
        if (attrType == null) {
            attrType = "";
        }
        return attrType;
    }

    @Override
    public void setAttr(String key, String value) {
        emMessage.setAttribute(key, value);
    }

    @Override
    public void setAttr(String key, Integer value) {
        emMessage.setAttribute(key, value);
    }

    @Override
    public void setAttr(String key, Boolean value) {
        emMessage.setAttribute(key, value);
    }

    @Override
    public void setAttr(String key, Long value) {
        emMessage.setAttribute(key, value);
    }

    @Override
    public long getMessageTime() {
       return emMessage.getMsgTime();
    }

    @Override
    public boolean isReceivedMessage() {
      return   emMessage.direct().equals(EMMessage.Direct.RECEIVE);
    }


}
