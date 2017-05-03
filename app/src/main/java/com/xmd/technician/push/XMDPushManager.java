package com.xmd.technician.push;

import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.technician.AppConfig;
import com.xmd.technician.common.DESede;
import com.xmd.technician.event.EventLogin;
import com.xmd.technician.event.EventLogout;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xmd.technician.http.RetrofitServiceFactory.getSpaService;

/**
 * Created by heyangya on 17-5-3.
 * 管理个推的绑定与解绑
 */

class XMDPushManager {
    private static final String TAG = "XMDPushManager";
    private static final XMDPushManager ourInstance = new XMDPushManager();

    static XMDPushManager getInstance() {
        return ourInstance;
    }

    private XMDPushManager() {
        RxBus.getInstance().toObservable(EventLogin.class).subscribe(this::handleLogin);
        RxBus.getInstance().toObservable(EventLogout.class).subscribe(this::handleLogout);
    }

    private String mClientId;
    private boolean mIsBind; //是否绑定
    private boolean mRunBind; //是否正在执行绑定
    private Call<BaseResult> mBindCall;
    private Call<BaseResult> mUnBindCall;
    private LoginTechnician technician = LoginTechnician.getInstance();

    void onGetClientId(String clientId) {
        this.mClientId = clientId;
        loopBind(technician.getToken());
    }

    //登录事件
    private void handleLogin(EventLogin eventLogin) {
        loopBind(eventLogin.getToken());
    }

    //登出事件
    private void handleLogout(EventLogout eventLogout) {
        unbind(eventLogout.getToken());
    }

    private void loopBind(final String token) {
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(mClientId)) {
            XLogger.d(TAG, " token or client id is null , token=" + token + ",client id=" + mClientId);
            return;
        }
        if (mIsBind) {
            XLogger.i(TAG, " already bind!");
            return;
        }
        if (mRunBind) {
            XLogger.i(TAG, " already running bind!");
            return;
        }
        XLogger.d(TAG, "start bind ... ");
        if (mUnBindCall != null) {
            mUnBindCall.cancel();
        }
        mRunBind = true;
        RetryPool.getInstance().postWork(new RetryPool.RetryRunnable(1000, 1, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return !mRunBind || bind(token);
            }
        }));
    }

    private boolean bind(String token) {
        String userId = technician.getUserId();
        String secretBefore = AppConfig.sGetuiAppId +
                AppConfig.sGetuiAppSecret +
                userId +
                AppConfig.sGetuiAppKey +
                AppConfig.sGetuiMasterSecret +
                mClientId;
        String secret = DESede.encrypt(secretBefore);
        mBindCall = getSpaService().bindGetuiClientId(token, userId, RequestConstant.USER_TYPE_TECH, RequestConstant.APP_TYPE_ANDROID, mClientId, secret);
        try {
            Response<BaseResult> response = mBindCall.execute();
            BaseResult result = response.body();
            if (!response.isSuccess() || result == null || result.statusCode != 200) {
                XLogger.e(TAG, "bind failed:" + response.code() + "," + response.message());
                return false;
            }
            XLogger.i(TAG, "bind userId:" + userId + " with " + mClientId + " success!");
            mIsBind = true;
            mRunBind = false;
            return true;
        } catch (IOException e) {
            XLogger.e(TAG, "bind failed:" + e.getLocalizedMessage());
        }
        return false;
    }

    private void unbind(String token) {
        XLogger.i(TAG, "unbind ");
        mRunBind = false;
        if (mBindCall != null) {
            mBindCall.cancel();
        }
        if (mIsBind) {
            mIsBind = false;
            mUnBindCall = getSpaService().unbindGetuiClientId(RequestConstant.USER_TYPE_TECH,
                    token, RequestConstant.SESSION_TYPE, mClientId);
            mUnBindCall.enqueue(new Callback<BaseResult>() {
                @Override
                public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {

                }

                @Override
                public void onFailure(Call<BaseResult> call, Throwable t) {

                }
            });
        }
    }
}
