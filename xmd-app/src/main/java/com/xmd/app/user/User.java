package com.xmd.app.user;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by heyangya on 17-6-5.
 * 用户信息
 */

public class User implements Serializable {
    private static final long serialVersionUID = 88750009432710L;
    public final static int USER_TYPE_UNKNOWN = 0;
    public final static int USER_TYPE_CUSTOMER = 1;
    public final static int USER_TYPE_TECH = 2;
    public final static int USER_TYPE_MANAGER = 3;

    public final static String ROLE_USER = "user"; //客户

    public final static String ROLE_TECH = "tech";//技师
    public final static String ROLE_FLOOR = "floor_staff";//楼面
    public final static String ROLE_MANAGER = "管理者";

    private String id; //用户ID
    private String avatar; //用户头像
    private String name; //用户名称
    private String chatId; //用户聊天ID
    private String chatPassword; //聊天密码，当前登录用户设置

    private String clubId;
    private String clubName;

    private String techNo;

    private int type; //用户类型

    private String markName;

    //返回显示的名字，优先显示备注名
    public String getShowName() {
        if (!TextUtils.isEmpty(markName)) {
            return markName;
        }
        return name;
    }

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
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

    public User update(User n) {
        if (n.chatId != null) {
            chatId = n.chatId;
        }
        if (n.name != null) {
            name = n.name;
        }
        if (n.avatar != null) {
            avatar = n.avatar;
        }
        if (n.markName != null) {
            markName = n.markName;
        }
        if (n.type != USER_TYPE_UNKNOWN) {
            type = n.type;
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User o = (User) obj;
        return TextUtils.equals(id, o.id)
                && TextUtils.equals(avatar, o.avatar)
                && TextUtils.equals(name, o.name)
                && TextUtils.equals(chatId, o.chatId)
                && type == o.type
                && TextUtils.equals(markName, o.markName);
    }

    @Override
    public int hashCode() {
        int h = 0x48;
        h += id == null ? 0 : id.hashCode();
        h += name == null ? 0 : name.hashCode();
        h += avatar == null ? 0 : avatar.hashCode();
        h += chatId == null ? 0 : chatId.hashCode();
        h += type;
        h += markName == null ? 0 : markName.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", chatId='" + chatId + '\'' +
                ", chatPassword='" + chatPassword + '\'' +
                ", clubId='" + clubId + '\'' +
                ", clubName='" + clubName + '\'' +
                ", techNo='" + techNo + '\'' +
                ", type=" + type +
                ", markName='" + markName + '\'' +
                '}';
    }
}
