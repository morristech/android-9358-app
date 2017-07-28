package com.xmd.app.user;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by heyangya on 17-6-5.
 * 用户信息
 */

public class User implements Serializable {
    private static final long serialVersionUID = 88750009432718L;

    public final static String ROLE_USER = "user"; //客户
    public final static String ROLE_TECH = "tech";//技师
    public final static String ROLE_FLOOR = "floor_staff";//楼面
    public final static String ROLE_MANAGER = "club_manager";//管理者

    public final static String USER_TYPE_USER = "user";
    public final static String USER_TYPE_TECH = "tech";
    public final static String USER_TYPE_MANAGER = "manager";

    private String userId; //用户ID
    private String chatId; //用户聊天ID
    private String avatar; //用户头像
    private String noteName; //用户备注名
    private String name; //用户名称
    private String userType;
    private String userRoles;
    private String telephone;
    private ContactPermission contactPermission;

    private String clubId;
    private String clubName;
    private String techNo;
    private String chatPassword; //聊天密码，当前登录用户设置

    //返回显示的名字，优先显示备注名
    public String getShowName() {
        if (!TextUtils.isEmpty(noteName)) {
            return noteName;
        }
        return name;
    }

    public User(String id) {
        this.userId = id;
    }

    public String getId() {
        return userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(String id) {
        this.userId = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(String roles) {
        this.userRoles = roles;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getChatPassword() {
        return chatPassword;
    }

    public void setChatPassword(String chatPassword) {
        this.chatPassword = chatPassword;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getTechNo() {
        return techNo;
    }

    public void setTechNo(String techNo) {
        this.techNo = techNo;
    }


    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        if (userType != null) {
            return userType;
        }
        if (!TextUtils.isEmpty(getUserRoles())) {
            if (getUserRoles().contains(User.ROLE_MANAGER)) {
                return USER_TYPE_MANAGER;
            }
            if (getUserRoles().contains(User.ROLE_FLOOR) || getUserRoles().contains(User.ROLE_TECH)) {
                return USER_TYPE_TECH;
            }
        }
        return USER_TYPE_USER;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public ContactPermission getContactPermission() {
        return contactPermission;
    }

    public void setContactPermission(ContactPermission contactPermission) {
        this.contactPermission = contactPermission;
    }

    public User update(User n) {
        chatId = n.chatId != null ? n.chatId : chatId;
        name = n.name != null ? n.name : name;
        avatar = n.avatar != null ? n.avatar : avatar;
        noteName = n.noteName != null ? n.noteName : noteName;
        userRoles = n.userRoles != null ? n.userRoles : userRoles;
        userType = n.userType != null ? n.userType : userType;
        telephone = n.telephone != null ? n.telephone : telephone;
        contactPermission = n.contactPermission != null ? n.contactPermission : contactPermission;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User o = (User) obj;
        return TextUtils.equals(userId, o.userId)
                && TextUtils.equals(chatId, o.chatId)
                && TextUtils.equals(avatar, o.avatar)
                && TextUtils.equals(name, o.name)
                && TextUtils.equals(userType, o.userType)
                && TextUtils.equals(telephone, o.telephone)
                && TextUtils.equals(userRoles, o.userRoles)
                && TextUtils.equals(noteName, o.noteName)
                && (o.contactPermission == null || o.contactPermission.equals(contactPermission));
    }

    @Override
    public int hashCode() {
        int h = 0x48;
        h += userId == null ? 0 : userId.hashCode();
        h += name == null ? 0 : name.hashCode();
        h += avatar == null ? 0 : avatar.hashCode();
        h += chatId == null ? 0 : chatId.hashCode();
        h += noteName == null ? 0 : noteName.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", chatId='" + chatId + '\'' +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", userType='" + userType + '\'' +
                ", userRoles='" + userRoles + '\'' +
                ", telephone='" + telephone + '\'' +
                ", contactPermission=" + contactPermission +
                ", noteName='" + noteName + '\'' +
                ", clubId='" + clubId + '\'' +
                ", clubName='" + clubName + '\'' +
                ", techNo='" + techNo + '\'' +
                ", chatPassword='" + chatPassword + '\'' +
                '}';
    }
}
