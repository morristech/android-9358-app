package com.xmd.technician.presenter;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.contract.LoginContract;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.window.MainActivity;
import com.xmd.technician.window.ResetPasswordActivity;

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

    private LoginTechnician mLoginTech = LoginTechnician.getInstance();

    private Subscription mLoginSubscription;

    public LoginPresenter(Context context, LoginContract.View view) {
        super(context, view);
    }

    @Override
    public void onCreate() {
        //初始化数据
        mInviteCode = mLoginTech.getInviteCode();
        mTechNo = mLoginTech.getTechNo();
        mPhoneNumber = mLoginTech.getPhoneNumber();
        mView.setInviteCode(mInviteCode);
        mView.setTechNo(mTechNo);
        mView.setPhoneNumber(mPhoneNumber);

        mIsPhoneLogin = false;
        switchLoginMethodTo(mIsPhoneLogin ? 0 : 1);
        registerLoginListener();
    }

    @Override
    public void onDestroy() {
        unregisterLoginListener();
    }

    @Override
    public void onClickSwitchLoginMethod() {
        mIsPhoneLogin = !mIsPhoneLogin;
        switchLoginMethodTo(mIsPhoneLogin ? 0 : 1);
    }

    private void switchLoginMethodTo(int loginMethod) {
        if (loginMethod == 0) {
            mView.showPhoneLogin();
        } else {
            mView.showTechNoLogin();
        }
    }

    private void registerLoginListener() {
        if (mLoginSubscription == null) {
            mLoginSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                    loginResult -> handleLoginResult(loginResult));
        }
    }

    private void unregisterLoginListener() {
        if (mLoginSubscription != null) {
            RxBus.getInstance().unsubscribe(mLoginSubscription);
            mLoginSubscription = null;
        }
    }

    @Override
    public void onClickLogin() {
        mView.showLoading("正在登录...");
        if (mIsPhoneLogin) {
            //使用手机号码登录
            LoginTechnician.getInstance().loginByPhoneNumber(mPhoneNumber, mPassword);
        } else {
            //使用技师编号登录
            LoginTechnician.getInstance().loginByTechNo(mInviteCode, mTechNo, mPassword);
        }
    }

    @Override
    public void onClickRegister() {
        UINavigation.gotoRegister(mContext, false);
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
            mView.enableLogin(Utils.matchPhoneNumFormat(mPhoneNumber)
                    && Utils.matchLoginPassword(mPassword));
        } else {
            mView.enableLogin(Utils.matchInviteCode(mInviteCode)
                    && Utils.matchTechNo(mTechNo)
                    && Utils.matchLoginPassword(mPassword));
        }
    }

    //处理手机登录结果
    private void handleLoginResult(LoginResult loginResult) {
        mView.hideLoading();
        if (loginResult.statusCode > 299 || (loginResult.statusCode < 200 && loginResult.statusCode != 0)) {
            mView.showAlertDialog(loginResult.msg);
        } else {
            if (!mIsPhoneLogin) {
                //进入完善资料界面
                if (loginResult.respData != null) {
                    Gson gson = new Gson();
                    loginResult = gson.fromJson(gson.toJson(loginResult.respData), LoginResult.class);
                } else {
                    mView.showAlertDialog("服务器出错，请联系管理员");
                }
            }
            if (!mIsPhoneLogin && loginResult.statusCode == 206) {
                //进入注册页面
                mLoginTech.setTechId(loginResult.spareTechId);
                UINavigation.gotoRegister(mContext, true);
            } else {
                //进入主界面
                mLoginTech.saveLoginResult(loginResult);
                UserProfileProvider.getInstance().updateCurrentUserInfo(loginResult.name, loginResult.avatarUrl);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_EMCHAT, null);
                ActivityHelper.getInstance().removeAllActivities();
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                mView.finishSelf();
            }
        }
    }
}
