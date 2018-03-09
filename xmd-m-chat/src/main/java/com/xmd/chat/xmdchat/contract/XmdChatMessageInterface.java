package com.xmd.chat.xmdchat.contract;

/**
 * Created by Lhj on 18-1-22.
 */

public interface XmdChatMessageInterface<T> {

    String getMsgType();

    long getMsgTime();

    void setMsgType(String msgType);

    String getMsgType(T message);

    String getToChatId();

    String getFromChatId();

    void setUserId(String userId);

    String getUserRoles();

    void setUserRoles(String userRoles);

    String getUserName();

    void setUserName(String userName);

    String getUserAvatar();

    void setUserAvatar(String userAvatar);

    String getUserAvatarId();

    void setUserAvatarId(String userAvatarId);

    String getTime();

    void setTime(String time);

    String getTechId();

    void setTechId(String techId);

    String getTechNo();

    void setTechNo(String techNo);

    String getClubId();

    void setClubId(String clubId);

    String getClubName();

    void setClubName(String clubName);

    void addTag(String tag);

    void clearTag();

    String getUserId();

    CharSequence getContentText();

    String getOriginContentText();

    boolean isCustomerService();

    String getRemoteChatId();

    String getFormatTime();

    String getChatRelativeTime();

    String getSafeStringAttribute(String key);

    Integer getSafeIntegerAttribute(String key);

    Long getSafeLongAttribute(String key);

    void setInnerProcessed(String processedDesc);

    String getAttrType();

    void setAttr(String key, String value);

    void setAttr(String key, Integer value);

    void setAttr(String key, Boolean value);

    void setAttr(String key, Long value);

    long getMessageTime();

    boolean isReceivedMessage();



}
