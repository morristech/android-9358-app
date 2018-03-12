package com.xmd.chat;

import android.content.Context;

import com.xmd.app.event.EventLogin;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.xmdchat.contract.XmdChatAccountManagerInterface;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.EmChatAccountManagerPresent;
import com.xmd.chat.xmdchat.present.ImChatAccountManagerPresent;

/**
 * Created by mo on 17-6-28.
 * 账号管理（登录，登出）
 */

public class ChatAccountManager {
    private static final ChatAccountManager ourInstance = new ChatAccountManager();

    public static ChatAccountManager getInstance() {
        return ourInstance;
    }

    private XmdChatAccountManagerInterface mInterface;
    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();

    private ChatAccountManager() {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            mInterface = new EmChatAccountManagerPresent();
        } else {
            mInterface = ImChatAccountManagerPresent.getInstance();
        }
    }

    public void init(Context context) {
        mInterface.init(context);
    }

    public void login(EventLogin eventLogin) {
        mInterface.login(eventLogin);
    }

    public void logout() {
        mInterface.logout();
    }

    public User getUser() {
        return userInfoService.getCurrentUser();
    }

    public String getChatId() {
        User user = userInfoService.getCurrentUser();
        return user == null ? null : user.getChatId();
    }

    public String getUserType() {
        User user = userInfoService.getCurrentUser();
        return user == null ? null : user.getUserType();
    }
}
