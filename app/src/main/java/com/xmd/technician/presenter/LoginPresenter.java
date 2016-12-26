package com.xmd.technician.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.contract.LoginContract;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.window.MainActivity;
import com.xmd.technician.window.ResetPasswordActivity;

import rx.Subscription;

/**
 * Created by heyangya on 16-12-19.
 */

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {
    private String mInviteCode;
    private String mTechNo;
    private String mPassword;
    private String mPhoneNumber;

    private LoginTechnician mLoginTech = LoginTechnician.getInstance();

    private Subscription mLoginSubscription;
    private Subscription mLoadTechInfoSubscription;

    public LoginPresenter(Context context, LoginContract.View view) {
        super(context, view);
    }

    @Override
    public void onCreate() {
        //初始化数据
        mInviteCode = mLoginTech.getClubInviteCode();
        mTechNo = mLoginTech.getTechNo();
        mPhoneNumber = mLoginTech.getPhoneNumber();
        mView.setInviteCode(mInviteCode);
        mView.setTechNo(mTechNo);
        mView.setPhoneNumber(mPhoneNumber);

        showLoginView();

        mLoginSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                result -> handleLoginResult(result));
        mLoadTechInfoSubscription = RxBus.getInstance().toObservable(TechInfoResult.class)
                .subscribe(this::handleLoadTechInfo);
    }

    @Override
    public void onDestroy() {
        RxBus.getInstance().unsubscribe(mLoadTechInfoSubscription, mLoginSubscription);
    }

    @Override
    public void onClickSwitchLoginMethod() {
        if (mLoginTech.getLoginType() == LoginTechnician.LOGIN_TYPE_PHONE) {
            mLoginTech.setLoginType(LoginTechnician.LOGIN_TYPE_TECH_NO);
        } else {
            mLoginTech.setLoginType(LoginTechnician.LOGIN_TYPE_PHONE);
        }
        showLoginView();
    }

    private void showLoginView() {
        if (mLoginTech.getLoginType() == LoginTechnician.LOGIN_TYPE_PHONE) {
            mView.showPhoneLogin();
        } else {
            mView.showTechNoLogin();
        }
    }


    @Override
    public void onClickLogin() {
        mView.showLoading("正在登录...");
        if (mLoginTech.getLoginType() == LoginTechnician.LOGIN_TYPE_PHONE) {
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
        if (mLoginTech.getLoginType() == LoginTechnician.LOGIN_TYPE_PHONE) {
            mView.enableLogin(Utils.matchPhoneNumFormat(mPhoneNumber)
                    && Utils.matchLoginPassword(mPassword));
        } else {
            mView.enableLogin(Utils.matchInviteCode(mInviteCode)
                    && Utils.matchTechNo(mTechNo)
                    && Utils.matchLoginPassword(mPassword));
        }
    }

    //处理手机登录结果
    private void handleLoginResult(LoginResult result) {
        if (result.statusCode > 299 || (result.statusCode < 200 && result.statusCode != 0)) {
            mView.hideLoading();
            mView.showAlertDialog(result.msg == null ? result.message : result.msg);
        } else {
            if (mLoginTech.getLoginType() == LoginTechnician.LOGIN_TYPE_TECH_NO) {
                //新的接口返回数据在respData中，所以要做一下转换
                if (result.respData != null) {
                    Gson gson = new Gson();
                    LoginResult newLoginResult = gson.fromJson(gson.toJson(result.respData), LoginResult.class);
                    newLoginResult.statusCode = result.statusCode;
                    newLoginResult.msg = result.msg;
                    result = newLoginResult;
                } else {
                    mView.hideLoading();
                    mView.showAlertDialog("服务器出错，请联系管理员");
                    return;
                }
            }
            if (mLoginTech.getLoginType() == LoginTechnician.LOGIN_TYPE_TECH_NO && result.statusCode == 206) {
                //进入注册页面
                mView.hideLoading();
                mLoginTech.setTechId(result.spareTechId);
                UINavigation.gotoRegister(mContext, true);
            } else {
                //登录成功，获取用户信息
                mLoginTech.onLoginResult(result);
                mLoginTech.loadTechInfo();
            }
        }
    }

    private void handleLoadTechInfo(TechInfoResult result) {
        mView.hideLoading();
        if (result.statusCode < 200 || result.statusCode > 299) {
            mView.showAlertDialog(result.msg);
        } else {
            mLoginTech.onLoadTechInfo(result);
            if (TextUtils.isEmpty(mLoginTech.getClubId())) {
                //需要提示加入会所
                UINavigation.gotoJoinClubFrom(mContext, UINavigation.OPEN_JOIN_CLUB_FROM_LOGIN);
            } else {
                mLoadTechInfoSubscription.unsubscribe();
                ActivityHelper.getInstance().removeAllActivities();
                UserProfileProvider.getInstance().updateCurrentUserInfo(mLoginTech.getNickName(), mLoginTech.getAvatarUrl());
                mLoginTech.loginEmChatAccount();
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                mView.finishSelf();
            }
        }
    }
}
