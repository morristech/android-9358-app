package com.xmd.chat;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by heyangya on 17-6-5.
 * 聊天消息
 */

public class ChatMessage {
    private EMMessage emMessage;
    private static final String ATTRIBUTE_USER_ID = "userId";
    private static final String ATTRIBUTE_USER_NAME = "userName";
    private static final String ATTRIBUTE_USER_AVATAR = "userHeader";

    public ChatMessage(EMMessage emMessage) {
        this.emMessage = emMessage;
    }

    public EMMessage getEmMessage() {
        return emMessage;
    }

    public void setEmMessage(EMMessage emMessage) {
        this.emMessage = emMessage;
    }

    public String getUserId() {
        return getSafeStringAttribute(ATTRIBUTE_USER_ID);
    }

    public void setUserId(String userId) {
        emMessage.setAttribute(ATTRIBUTE_USER_ID, userId);
    }

    public String getUserName() {
        return getSafeStringAttribute(ATTRIBUTE_USER_NAME);
    }

    public void setUserName(String userName) {
        emMessage.setAttribute(ATTRIBUTE_USER_NAME, userName);
    }

    public String getUserAvatar() {
        return getSafeStringAttribute(ATTRIBUTE_USER_AVATAR);
    }

    public void setAttributeUserAvatar(String userAvatar) {
        emMessage.setAttribute(ATTRIBUTE_USER_AVATAR, userAvatar);
    }

    public String getSafeStringAttribute(String key) {
        try {
            return emMessage.getStringAttribute(key);
        } catch (HyphenateException e) {
            return null;
        }
    }
}
