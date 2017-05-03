package com.xmd.technician.chat;

import android.content.Context;

import com.hyphenate.chat.EMConversation;

import java.util.List;

/**
 * Created by heyangya on 17-4-21.
 * 聊天模块对外接口
 */

public interface IEmchat {

    //初始化接口，在Applicaton.onCreate中调用
    void init(Context context, String appKey, boolean debug);

    /**
     * 登录环信接口
     *
     * @param name     用户名
     * @param password 密码
     *                 登录成功后会发送@EventEmChatLoginSuccess , 会一直重试，直到登录成功
     */
    void login(final String name, final String password);

    /**
     * 更新昵称信息
     * 此方法主要为了在苹果推送时能够推送nick而不是userid 一般可以在登陆成功后从自己服务器获取到个人信息，
     * 然后拿到nick更新到环信服务器 并且，在个人信息中如果更改个人的昵称，也要把环信服务器更新下nickname 防止显示差异
     *
     * @param nickName 昵称
     */
    void updateNickName(String nickName);

    /**
     * 登出
     */
    void logout();

    /**
     * 是否登录成功
     */
    boolean isConnected();


    /**
     * 获取会话列表
     */
    List<EMConversation> getAllConversationList();

    /**
     * 获取未读消息数量
     */
    int getUnreadMessageCount();

    /**
     * 清除和某人的未读消息
     *
     * @param userName
     */
    void clearUnreadMessage(String userName);

    void clearUnreadMessage(EMConversation conversation);
}
