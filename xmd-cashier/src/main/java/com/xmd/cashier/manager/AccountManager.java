package com.xmd.cashier.manager;

import android.os.SystemClock;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.LocalPersistenceManager;
import com.xmd.cashier.dal.bean.User;
import com.xmd.cashier.dal.net.AuthPayOkHttpUtil;
import com.xmd.cashier.dal.net.GeneOrderOkHttpUtil;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.ClubResult;
import com.xmd.cashier.dal.net.response.LoginResult;
import com.xmd.cashier.dal.net.response.LogoutResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.m.notify.push.XmdPushManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;


/**
 * Created by heyangya on 16-8-23.
 */

public class AccountManager {
    private static final String TAG = "AccountManager";
    private static AccountManager mInstance = new AccountManager();
    private User mUser;

    private AccountManager() {
        EventBusSafeRegister.register(this);
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
        mUser.clubId = loginResult.clubId;
        mUser.clubName = loginResult.clubName;
        mUser.clubIconUrl = loginResult.avatarUrl;
        mUser.userId = loginResult.userId;
        LocalPersistenceManager.writeUser(mUser);
        XLogger.i(TAG, AppConstants.LOG_BIZ_ACCOUNT_MANAGER + "Login OK：" + mUser.toString());
    }

    private void setClubInfo(ClubResult clubResult) {
        if (clubResult.getRespData() != null) {
            mUser.clubIconUrl = clubResult.getRespData().imageUrl;
            mUser.clubName = clubResult.getRespData().name;
            mUser.clubId = clubResult.getRespData().clubId;
            mUser.clubCreateTime = clubResult.getRespData().createTime;
            LocalPersistenceManager.writeUser(mUser);
            XLogger.i(TAG, AppConstants.LOG_BIZ_ACCOUNT_MANAGER + "Set Club OK：" + mUser.toString());
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
        if (TextUtils.isEmpty(mUser.clubCreateTime)) {
            // 返回小摩豆注册时间
            return AppConstants.TIME_XMD_REGISTER;
        } else {
            return mUser.clubCreateTime;
        }
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
                XLogger.e("Network error:" + e.getMessage());
                e.printStackTrace();
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    int exceptionCode = httpException.code();
                    if (exceptionCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                        callback.onError("会话已过期，请重新登录");
                        EventBus.getDefault().post(new EventTokenExpired("会话已过期"));
                    } else if (exceptionCode == RequestConstant.RESP_HTTP_BAD_GATEWAY) {
                        callback.onError("网络访问异常，请稍后重试（502）");
                    } else {
                        callback.onError("会话已过期，请重新登录");
                    }
                } else if (e instanceof SocketTimeoutException) {   //超时
                    callback.onError("服务器请求超时，请检查网络后重新登录");
                } else if (e instanceof ConnectException) { //服务器拒绝等
                    callback.onError("服务器连接异常，请重新登录");
                } else if (e instanceof UnknownHostException) { //无法解析主机地址等
                    callback.onError("无法连接服务器，请检查网络后重新登录");
                } else if (e instanceof EOFException) { //网络异常等
                    callback.onError("网络请求异常，请检查网络后重新登录");
                } else {    //其他异常情况
                    callback.onError("服务器请求异常，请重新登录");
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

                            SPManager.getInstance().initPushTagCount();
                            XmdNetwork.getInstance().setHeader("Club-Id", getClubId());
                            AuthPayOkHttpUtil.getInstance().setCommonHeader("Club-Id", getClubId());
                            GeneOrderOkHttpUtil.getInstance().setCommonHeader("Club-Id", getClubId());
                            XmdPushManager.getInstance().addListener(CustomPushMessageListener.getInstance());
                            EventBus.getDefault().removeStickyEvent(EventLogin.class);
                            com.xmd.app.user.User user = new com.xmd.app.user.User(getUserId());
                            EventBus.getDefault().postSticky(new EventLogin(getToken(), user));
                            MemberManager.getInstance().startGetMemberSetting();
                            InnerManager.getInstance().startGetInnerSwitch();
                            InnerManager.getInstance().getClubWorkTime();
                            NotifyManager.getInstance().startRepeatOnlinePay(SystemClock.elapsedRealtime() + AppConstants.DEFAULT_INTERVAL);
                            NotifyManager.getInstance().startRepeatOrderRecord(SystemClock.elapsedRealtime() + AppConstants.DEFAULT_INTERVAL);

                            ChannelManager.getInstance().getPayChannelList(null);
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
        XLogger.i(TAG, AppConstants.LOG_BIZ_ACCOUNT_MANAGER + "获取会所信息：" + RequestConstant.URL_CLUB_INFO);
        Observable<ClubResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getClubInfo(token, AppConstants.SESSION_TYPE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<ClubResult>() {
            @Override
            public void onCallbackSuccess(ClubResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_ACCOUNT_MANAGER + "获取会所信息---成功");
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_ACCOUNT_MANAGER + "获取会所信息---失败：" + e.getLocalizedMessage());
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    public void logout() {
        // 退出登录
        LocalPersistenceManager.clearClubQrcodeBytes(getClubId()); //清除二维码

        XmdPushManager.getInstance().removeListener(CustomPushMessageListener.getInstance());

        VerifyManager.getInstance().clearVerifyList();

        TradeManager.getInstance().newTrade();

        MemberManager.getInstance().newRechargeProcess();
        MemberManager.getInstance().newCardProcess();
        MemberManager.getInstance().stopGetMemberSetting();

        NotifyManager.getInstance().stopRepeatOnlinePay();
        NotifyManager.getInstance().stopRepeatOrderRecord();

        InnerManager.getInstance().stopGetInnerSwitch();
        InnerManager.getInstance().resetClubWorkTime();

        EventBus.getDefault().removeStickyEvent(EventLogin.class);
        EventBus.getDefault().postSticky(new EventLogout(AccountManager.getInstance().getToken(), AccountManager.getInstance().getUserId()));

        cleanUserInfo(); //清除用户信息
        XLogger.i(TAG, AppConstants.LOG_BIZ_ACCOUNT_MANAGER + "Already Logout ~~ ");
    }

    @Subscribe(priority = -1)
    public void onEvent(EventLogout eventLogout) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_ACCOUNT_MANAGER + "收银员登出操作：" + RequestConstant.URL_LOGOUT);
        final Call<LogoutResult> logoutCall = XmdNetwork.getInstance().getService(SpaService.class)
                .logout(getToken(), AppConstants.SESSION_TYPE);
        ThreadPoolManager.run(new Runnable() {
            @Override
            public void run() {
                try {
                    logoutCall.execute();
                } catch (IOException e) {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_ACCOUNT_MANAGER + "收银员登出操作异常：" + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @Subscribe
    public void onEvent(EventTokenExpired eventTokenExpired) {
        // token expire
        XmdPushManager.getInstance().removeListener(CustomPushMessageListener.getInstance());
        VerifyManager.getInstance().clearVerifyList();

        TradeManager.getInstance().newTrade();

        MemberManager.getInstance().newRechargeProcess();
        MemberManager.getInstance().newCardProcess();
        MemberManager.getInstance().stopGetMemberSetting();

        NotifyManager.getInstance().stopRepeatOrderRecord();
        NotifyManager.getInstance().stopRepeatOnlinePay();

        InnerManager.getInstance().stopGetInnerSwitch();
        InnerManager.getInstance().resetClubWorkTime();

        cleanUserInfo();
        XToast.show(ResourceUtils.getString(R.string.token_expired));
    }
}
