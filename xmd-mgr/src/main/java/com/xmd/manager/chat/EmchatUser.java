package com.xmd.manager.chat;

import android.text.TextUtils;

import com.hyphenate.chat.EMContact;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */
public class EmchatUser extends EMContact {

    /**
     * 昵称首字母
     */
    protected String initialLetter;
    /**
     * 用户头像
     */
    private String avatar;

    public EmchatUser(String username) {
        this.username = username;
    }

    public EmchatUser(String username, String nickname, String avatar) {
        this.username = username;
        this.nick = nickname;
        this.avatar = avatar;
    }

    @Override
    public String getNick() {
        return TextUtils.isEmpty(this.nick) ? "匿名用户" : this.nick;
    }

    public String getInitialLetter() {
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EmchatUser)) {
            return false;
        }
        return getUsername().equals(((EmchatUser) o).getUsername()) && getAvatar().equals(((EmchatUser) o).getAvatar()) && getNick().equals(((EmchatUser) o).getNick());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }
}