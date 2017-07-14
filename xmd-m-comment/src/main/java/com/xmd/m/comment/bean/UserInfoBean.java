package com.xmd.m.comment.bean;


import java.io.Serializable;

/**
 * Created by Lhj on 17-7-12.
 */

public class UserInfoBean implements Serializable {
    public String contactPhone;
    public String emChatId;
    public String emChatName;
    public String chatHeadUrl;
    public String contactType;
    public String userId;
    public String userNoteName;
    public String remarkMessage;
    public String remarkImpression;
    public String id;

    public UserInfoBean(String id, String contactPhone, String emChatId, String emChatName, String chatHeadUrl, String contactType, String userId, String userNoteName,
                        String remarkMessage, String remarkImpression) {
        this.id = id;
        this.contactPhone = contactPhone;
        this.emChatId = emChatId;
        this.emChatName = emChatName;
        this.chatHeadUrl = chatHeadUrl;
        this.contactType = contactType;
        this.userId = userId;
        this.userNoteName = userNoteName;
        this.remarkMessage = remarkMessage;
        this.remarkImpression = remarkImpression;
    }
}
