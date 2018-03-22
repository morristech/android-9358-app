package com.xmd.chat.xmdchat.contract;

import android.content.Context;

import com.xmd.app.event.EventLogin;

/**
 * Created by Lhj on 18-1-24.
 */

public interface XmdChatAccountManagerInterface {

    void init(Context context, String appKey, boolean debug);

    void login(EventLogin eventLogin);

    void login();

    void logout();

    void loopLogin();

}
