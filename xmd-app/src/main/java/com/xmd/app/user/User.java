package com.xmd.app.user;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by heyangya on 17-6-5.
 * 用户信息
 */

public class User implements Serializable {
    private String id; //用户ID
    private String avatar; //用户头像
    private String name; //用户名称
    private String chatId; //用户聊天ID

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


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User o = (User) obj;
        return TextUtils.equals(id, o.id)
                && TextUtils.equals(avatar, o.avatar)
                && TextUtils.equals(name, o.name)
                && TextUtils.equals(chatId, o.chatId);
    }

    @Override
    public int hashCode() {
        int h = 0x48;
        h += id == null ? 0 : id.hashCode();
        h += name == null ? 0 : name.hashCode();
        h += avatar == null ? 0 : avatar.hashCode();
        h += chatId == null ? 0 : chatId.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }
}
