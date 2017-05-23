package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.LocalPersistenceManager;
import com.xmd.cashier.dal.bean.User;
import com.xmd.cashier.dal.net.NetworkSubscriber;
import com.xmd.cashier.dal.net.SpaRetrofit;
import com.xmd.cashier.dal.net.response.ClubResult;
import com.xmd.cashier.dal.net.response.LoginResult;
import com.xmd.cashier.dal.net.response.LogoutResult;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by heyangya on 16-8-23.
 */

public class AccountManager {
    private static AccountManager mInstance = new AccountManager();
    private User mUser;

    private AccountManager() {
        mUser = LocalPersistenceManager.readUser();
        if (mUser == null) {
            mUser = new User();
        }
    }

    public static AccountManager getInstance() {
        return mInstance;
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(mUser.token);
    }

    private void setUserInfo(LoginResult loginResult) {
        mUser.loginName = loginResult.loginName;
        mUser.token = loginResult.token;
        mUser.userName = loginResult.name;
        mUser.clubIconUrl = loginResult.avatarUrl;
        mUser.userId = loginResult.userId;
        LocalPersistenceManager.writeUser(mUser);
        XLogger.i("login ok: " + mUser.toString());
    }

    private void setClubInfo(ClubResult clubResult) {
        if (clubResult.respData != null) {
            mUser.clubIconUrl = clubResult.respData.imageUrl;
            mUser.clubName = clubResult.respData.name;
            mUser.clubId = clubResult.respData.clubId;
            LocalPersistenceManager.writeUser(mUser);
        }
    }

    public void cleanUserInfo() {
        mUser.token = "";
        mUser.clubIconUrl = "";
        LocalPersistenceManager.writeUser(mUser);
    }

    public User getUser() {
        return mUser;
    }

    public String getToken() {
        return mUser.token;
    }

    public String getUserId() {
        return mUser.userId;
    }

    public String getClubName() {
        return mUser.clubName;
    }

    public String getClubId() {
        return mUser.clubId;
    }

    public String getClubIcon() {
        return mUser.clubIconUrl;
    }

    public Subscription login(String username, String password, final Callback<LoginResult> callback) {
        return SpaRetrofit.getService().login(username, password, Utils.getAppVersionName(), AppConstants.SESSION_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<LoginResult>() {
                    @Override
                    public void onCallbackSuccess(final LoginResult loginResult) {
                        setUserInfo(loginResult);

                        getClubInfo(getToken(), new Callback<ClubResult>() {
                            @Override
                            public void onSuccess(ClubResult o) {
                                setClubInfo(o);
                                callback.onSuccess(loginResult);
                            }

                            @Override
                            public void onError(String error) {
                                callback.onError(error);
                            }
                        });
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    private Subscription getClubInfo(String token, final Callback<ClubResult> callback) {
        return SpaRetrofit.getService().getClubInfo(token, AppConstants.SESSION_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<ClubResult>() {
                    @Override
                    public void onCallbackSuccess(ClubResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    public Subscription logout(final Callback<LogoutResult> callback) {
        Subscription subscription = SpaRetrofit.getService().logout(getToken(), AppConstants.SESSION_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<LogoutResult>() {
                    @Override
                    public void onCallbackSuccess(LogoutResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
        LocalPersistenceManager.clearClubQrcodeBytes(getClubId()); //清除二维码
        cleanUserInfo(); //清除用户信息
        return subscription;
    }
}
