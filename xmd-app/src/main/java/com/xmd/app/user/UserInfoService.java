package com.xmd.app.user;

import com.xmd.app.IFunctionInit;

/**
 * Created by heyangya on 17-6-5.
 * 用户信息模块服务接口
 */

public interface UserInfoService extends IFunctionInit {
    //聊天ID获取用户信息
    User getUserByChatId(String chatId);

    //用户ID获取用户信息
    User getUserByUserId(String userId);

    void saveUser(User user);
}
