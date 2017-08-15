package com.xmd.app.user;

import com.shidou.commonlibrary.Callback;
import com.xmd.app.IFunctionInit;

import java.util.List;

/**
 * Created by heyangya on 17-6-5.
 * 用户信息模块服务接口
 */

public interface UserInfoService extends IFunctionInit {
    //加载用户信息
    void loadUserInfoByUserIdFromServer(String userId, Callback<User> callback);

    //从网络加载用户信息
    void loadUserInfoByChatIdFromServer(String chatId, Callback<User> callback);

    //从网络或者缓存加载用户信息
    void loadUserInfoByChatId(String chatId, Callback<User> callback);

    //聊天ID获取用户信息
    User getUserByChatId(String chatId);

    //用户ID获取用户信息
    User getUserByUserId(String userId);

    void saveUser(User user);

    //获取当前登录的用户
    User getCurrentUser();

    //保存用户信息到缓存
    void saveCurrentUser(User user);

    //获取token值
    String getCurrentToken();

    List<User> getAllUsers();

}
