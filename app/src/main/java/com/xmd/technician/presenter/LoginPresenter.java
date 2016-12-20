package com.xmd.technician.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xmd.technician.AppConfig;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.common.Utils;
import com.xmd.technician.contract.LoginContract;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.window.MainActivity;
import com.xmd.technician.window.RegisterActivity;
import com.xmd.technician.window.RegisterActivity2;
import com.xmd.technician.window.ResetPasswordActivity;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by heyangya on 16-12-19.
 */

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {
    private boolean mIsPhoneLogin;

    private String mInviteCode;
    private String mTechNo;
    private String mPassword;
    private String mPhoneNumber;

    private Subscription mPhoneLoginSubscription;
    private Subscription mTechNoLoginSubscription;

    public LoginPresenter(Context context, LoginContract.View view) {
        super(context, view);
    }

    @Override
    public void onCreate() {
        mIsPhoneLogin = true;
        switchLoginMethodTo(1);
        LoginTechnician.getInstance().inviteCode = "111817";
        LoginTechnician.getInstance().techNo = "00003";
        LoginTechnician.getInstance().techId = "";
        mContext.startActivity(new Intent(mContext, RegisterActivity.class));//FIXME
    }

    @Override
    public void onDestroy() {
        unregisterPhoneLoginListener();
        unregisterTechNoLoginListener();
    }

    @Override
    public void onClickSwitchLoginMethod() {
        mIsPhoneLogin = !mIsPhoneLogin;
        switchLoginMethodTo(mIsPhoneLogin ? 0 : 1);
    }

    private void switchLoginMethodTo(int loginMethod) {
        if (loginMethod == 0) {
            mView.showPhoneLogin();
            mView.setPhoneNumber(SharedPreferenceHelper.getUserAccount());
            unregisterTechNoLoginListener();
            registerPhoneLoginListener();
        } else {
            //FIXME
            mInviteCode = "111817";
            mPassword = "123456";
            mTechNo = "00003";
            mView.showTechNoLogin();
            unregisterPhoneLoginListener();
            registerTechNoLoginListener();
        }
    }

    private void registerPhoneLoginListener() {
        if (mPhoneLoginSubscription == null) {
            mPhoneLoginSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                    loginResult -> handleLoginResult(loginResult));
        }
    }

    private void unregisterPhoneLoginListener() {
        if (mPhoneLoginSubscription != null) {
            RxBus.getInstance().unsubscribe(mPhoneLoginSubscription);
            mPhoneLoginSubscription = null;
        }
    }

    private void registerTechNoLoginListener() {
        if (mTechNoLoginSubscription == null) {
            mTechNoLoginSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                    loginResult -> handleLoginResult(loginResult));
        }
    }

    private void unregisterTechNoLoginListener() {
        if (mTechNoLoginSubscription != null) {
            RxBus.getInstance().unsubscribe(mTechNoLoginSubscription);
            mTechNoLoginSubscription = null;
        }
    }

    @Override
    public void onClickLogin() {
        mView.showLoading("正在登录...");
        if (mIsPhoneLogin) {
            //使用手机号码登录
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_USERNAME, mPhoneNumber);
            params.put(RequestConstant.KEY_PASSWORD, mPassword);
            params.put(RequestConstant.KEY_APP_VERSION, "android." + AppConfig.getAppVersionNameAndCode());
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN, params);
        } else {
            //使用技师编号登录
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_CLUB_CODE, mInviteCode);
            params.put(RequestConstant.KEY_TECH_No, mTechNo);
            params.put(RequestConstant.KEY_PASSWORD, mPassword);
            params.put(RequestConstant.KEY_APP_VERSION, "android." + AppConfig.getAppVersionNameAndCode());
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_BY_TECH_NO, params);
        }
    }

    @Override
    public void onClickRegister() {
        mContext.startActivity(new Intent(mContext, RegisterActivity2.class));
    }

    @Override
    public void onClickFindPassword() {
        mContext.startActivity(new Intent(mContext, ResetPasswordActivity.class));
    }

    @Override
    public void setInviteCode(String value) {
        mInviteCode = value;
        checkLoginReady();
    }

    @Override
    public void setTechNumber(String value) {
        mTechNo = value;
        checkLoginReady();
    }

    @Override
    public void setPassword(String value) {
        mPassword = value;
        checkLoginReady();
    }

    @Override
    public void setPhoneNumber(String value) {
        mPhoneNumber = value;
        checkLoginReady();
    }

    private void checkLoginReady() {
        if (mIsPhoneLogin) {
            mView.enableLogin(!TextUtils.isEmpty(mPhoneNumber)
                    && Utils.matchPhoneNumFormat(mPhoneNumber)
                    && !TextUtils.isEmpty(mPassword)
                    && mPassword.length() >= 6 && mPassword.length() <= 20);
        } else {
            mView.enableLogin(!TextUtils.isEmpty(mInviteCode) && mInviteCode.length() >= 6
                    && !TextUtils.isEmpty(mTechNo) && mTechNo.length() >= 5
                    && !TextUtils.isEmpty(mPassword));
        }
    }

    //处理手机登录结果
    private void handleLoginResult(LoginResult loginResult) {
        mView.hideLoading();
        if (loginResult.statusCode > 299 || (loginResult.statusCode < 200 && loginResult.statusCode != 0)) {
            mView.showAlertDialog(loginResult.msg);
        } else {
            if (!mIsPhoneLogin && loginResult.statusCode == 206) {
                //进入完善资料界面
                mContext.startActivity(new Intent(mContext, RegisterActivity.class));
            } else {
                //进入主界面
                SharedPreferenceHelper.setUserAccount(mPhoneNumber);
                SharedPreferenceHelper.setUserToken(loginResult.token);
                SharedPreferenceHelper.setUserId(loginResult.userId);
                SharedPreferenceHelper.setEmchatId(loginResult.emchatId);
                SharedPreferenceHelper.setEMchatPassword(loginResult.emchatPassword);
                UserProfileProvider.getInstance().updateCurrentUserInfo(loginResult.name, loginResult.avatarUrl);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_EMCHAT, null);
                ActivityHelper.getInstance().removeAllActivities();
                mContext.startActivity(new Intent(mContext, MainActivity.class));
            }
            mView.finishSelf();
        }
    }
}
