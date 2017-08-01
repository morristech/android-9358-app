package com.xmd.manager;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.user.User;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.manager.beans.ClubInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mo on 17-8-1.
 */

public class ManagerAccountManager {
    private static final ManagerAccountManager ourInstance = new ManagerAccountManager();

    public static ManagerAccountManager getInstance() {
        return ourInstance;
    }

    private ManagerAccountManager() {
    }

    private Context context;
    private boolean login;

    public void init(Context context) {
        this.context = context;
        EventBusSafeRegister.register(this);

        if (!TextUtils.isEmpty(SharedPreferenceHelper.getUserToken())) {
            onLogin();
        }
    }

    //登录后调用
    public void onLogin() {
        login = true;
        EventBus.getDefault().removeStickyEvent(EventLogout.class);
        User user = new User(SharedPreferenceHelper.getUserId());
        user.setUserRoles(User.ROLE_MANAGER);
        user.setChatId(SharedPreferenceHelper.getEmchatId());
        user.setChatPassword(SharedPreferenceHelper.getEmchatPassword());
        user.setName(SharedPreferenceHelper.getUserName());
        user.setAvatar(SharedPreferenceHelper.getUserAvatar());
        ClubInfo clubInfo = ClubData.getInstance().getClubInfo();
        if (clubInfo != null) {
            user.setClubId(clubInfo.clubId);
            user.setClubName(clubInfo.clubName);
        }
        EventLogin eventLogin = new EventLogin(SharedPreferenceHelper.getUserToken(), user);
        EventBus.getDefault().postSticky(eventLogin);
    }

    /**
     * 登出账号
     */
    public void logout() {
        if (!login) {
            return;
        }
        EventBus.getDefault().removeStickyEvent(EventLogin.class);
        EventBus.getDefault().postSticky(new EventLogout(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserId()));

        login = false;
        SharedPreferenceHelper.setUserToken(null);
        UINavigation.gotoLogin(context);
    }

    @Subscribe
    public void onTokenExpire(EventTokenExpired expired) {
        if (!login) {
            return;
        }
        XToast.show(expired.getReason());
        login = false;
        SharedPreferenceHelper.setUserToken(null);
        UINavigation.gotoLogin(context);
    }
}
