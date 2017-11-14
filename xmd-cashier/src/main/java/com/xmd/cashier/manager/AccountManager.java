package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.LocalPersistenceManager;
import com.xmd.cashier.dal.bean.User;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.ClubResult;
import com.xmd.cashier.dal.net.response.LoginResult;
import com.xmd.cashier.dal.net.response.LogoutResult;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;


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
        if (clubResult.getRespData() != null) {
            mUser.clubIconUrl = clubResult.getRespData().imageUrl;
            mUser.clubName = clubResult.getRespData().name;
            mUser.clubId = clubResult.getRespData().clubId;
            mUser.clubCreateTime = clubResult.getRespData().createTime;
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

    public String getClubCreateTime() {
        return mUser.clubCreateTime;
    }

    public Subscription login(String username, String password, final Callback<LoginResult> callback) {
        Observable<LoginResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .login(username, password, Utils.getAppVersionName(), AppConstants.SESSION_TYPE);
        return XmdNetwork.getInstance().request(observable, new Subscriber<LoginResult>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    if (httpException.code() == RequestConstant.RESP_TOKEN_EXPIRED) {
                        EventBus.getDefault().post(new EventTokenExpired("会话已过期"));
                    }
                    callback.onError("会话已过期，请重新登录");
                } else if (e instanceof SocketTimeoutException) {
                    callback.onError("服务器请求超时");
                } else if (e instanceof ConnectException) {
                    callback.onError("服务器请求错误");
                } else {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onNext(final LoginResult loginResult) {
                if (loginResult != null && !loginResult.status.equals("fail")) {
                    setUserInfo(loginResult);
                    getClubInfo(getToken(), new Callback<ClubResult>() {
                        @Override
                        public void onSuccess(ClubResult o) {
                            setClubInfo(o);
                            callback.onSuccess(loginResult);

                            // 绑定推送
                            EventBus.getDefault().removeStickyEvent(EventLogin.class);
                            com.xmd.app.user.User user = new com.xmd.app.user.User(getUserId());
                            EventBus.getDefault().postSticky(new EventLogin(getToken(), user));
                            NotifyManager.getInstance().startGetFastPayCountAsync();
                            NotifyManager.getInstance().startGetOrderCountAsync();
                            MemberManager.getInstance().startGetMemberSetting();
                            InnerManager.getInstance().startGetInnerSwitch();
                            InnerManager.getInstance().startGetInnerChannel();
                        }

                        @Override
                        public void onError(String error) {
                            callback.onError(error);
                        }
                    });
                } else {
                    callback.onError(loginResult.message);
                }
            }
        });
    }


    private Subscription getClubInfo(String token, final Callback<ClubResult> callback) {
        Observable<ClubResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getClubInfo(token, AppConstants.SESSION_TYPE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<ClubResult>() {
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
        // 解绑推送
        EventBus.getDefault().removeStickyEvent(EventLogin.class);
        EventBus.getDefault().postSticky(new EventLogout(AccountManager.getInstance().getToken(), AccountManager.getInstance().getUserId()));
        NotifyManager.getInstance().stopGetFastPayCountAsync();
        NotifyManager.getInstance().stopGetOrderCountAsync();
        MemberManager.getInstance().stopGetMemberSetting();
        InnerManager.getInstance().stopGetInnerSwitch();
        InnerManager.getInstance().stopGetInnerChannel();

        Observable<LogoutResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .logout(getToken(), AppConstants.SESSION_TYPE);
        Subscription subscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<LogoutResult>() {
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
