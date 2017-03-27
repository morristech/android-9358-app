package com.xmd.technician.presenter;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextUtils;

import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.ThreadPoolManager;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.contract.RegisterContract;
import com.xmd.technician.databinding.ActivityRegisterBinding;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;

import java.util.concurrent.Future;

import rx.Subscription;

/**
 * Created by heyangya on 16-12-20.
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter {
    private ActivityRegisterBinding mBinding;
    private LoginTechnician mTech = LoginTechnician.getInstance();
    public ObservableBoolean mCanGetVerificationCode = new ObservableBoolean();//是否能获取验证码
    public ObservableBoolean mCanGotoSetInfoView = new ObservableBoolean(); //是否能跳转到设置资料页面
    public ObservableField<String> mSendVerificationText = new ObservableField<>();
    public String mTitle;
    public boolean mShowTechNoAndClubInviteCode;
    private String mPhoneNumber;
    private long mSendVerificationTime;
    private String mVerificationCode;
    private String mPassword;
    private Future mSendVerificationFuture;
    private static final int VERIFICATION_INTERVAL = 60000;//验证码间隔60秒


    private Subscription mRegisterSubscription;

    public RegisterPresenter(Context context, RegisterContract.View view, ActivityRegisterBinding binding) {
        super(context, view);
        mBinding = binding;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSendVerificationTime = SharedPreferenceHelper.getVerificationCodeTime();
        checkVerificationCode();
        if (System.currentTimeMillis() - mSendVerificationTime < VERIFICATION_INTERVAL) {
            startVerificationTime();
        }
        if (!TextUtils.isEmpty(mTech.getUserId())) {
            mTitle = "完善资料";
            mShowTechNoAndClubInviteCode = true;
        } else {
            mTitle = "注册";
            mShowTechNoAndClubInviteCode = false;
        }
        mBinding.setTech(mTech);
        mBinding.setPresenter(this);

        mRegisterSubscription = RxBus.getInstance().toObservable(RegisterResult.class).subscribe(
                result -> handleRegisterResult(result)
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSendVerificationFuture != null) {
            mSendVerificationFuture.cancel(true);
        }
        RxBus.getInstance().unsubscribe(mRegisterSubscription);
    }

    @Override
    public void onClickNextStep() {
        //进入下一步，这里进行注册
        mView.showLoading("正在注册...");
        if (!TextUtils.isEmpty(mTech.getUserId())) {
            mTech.register(mPhoneNumber, mPassword, mVerificationCode, mTech.getClubInviteCode(), mTech.getUserId(), mTech.getTechNo(), mTech.getRoles());
        } else {
            mTech.register(mPhoneNumber, mPassword, mVerificationCode, null, null, null, null);
        }
    }

    @Override
    public void onClickGetVerificationCode() {
        mSendVerificationTime = System.currentTimeMillis();
        SharedPreferenceHelper.setVerificationCodeTime(mSendVerificationTime);
        startVerificationTime();
        mTech.getVerificationCode(mPhoneNumber);
    }

    //开始验证码倒计时
    private void startVerificationTime() {
        if (mSendVerificationFuture != null) {
            mSendVerificationFuture.cancel(true);
        }
        mSendVerificationFuture = ThreadPoolManager.run(new Runnable() {
            @Override
            public void run() {
                mCanGetVerificationCode.set(false);
                long elapsedTime = System.currentTimeMillis() - mSendVerificationTime;
                while (elapsedTime < VERIFICATION_INTERVAL) {
                    mSendVerificationText.set("获取验证码(" + (VERIFICATION_INTERVAL - elapsedTime) / 1000 + ")");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    elapsedTime = System.currentTimeMillis() - mSendVerificationTime;
                }
                checkVerificationCode();
            }
        });
    }

    //检查并设置验证码按钮状态和文本
    private void checkVerificationCode() {
        mCanGetVerificationCode.set(Utils.matchPhoneNumFormat(mPhoneNumber)
                && System.currentTimeMillis() - mSendVerificationTime >= VERIFICATION_INTERVAL);
        if (System.currentTimeMillis() - mSendVerificationTime >= VERIFICATION_INTERVAL) {
            mSendVerificationText.set("获取验证码");
        }
    }

    @Override
    public void setPhoneNumber(Editable s) {
        mPhoneNumber = s.toString();
        checkVerificationCode();
        checkCanGotoSetInfoView();
    }

    @Override
    public void setVerificationCode(Editable s) {
        mVerificationCode = s.toString();
        checkCanGotoSetInfoView();
    }

    @Override
    public void setNewPassword(Editable s) {
        mPassword = s.toString();
        checkCanGotoSetInfoView();
    }

    @Override
    public void onClickBack() {
        mView.finishSelf();
    }

    private void checkCanGotoSetInfoView() {
        mCanGotoSetInfoView.set(Utils.matchPhoneNumFormat(mPhoneNumber)
                && Utils.matchVerificationCode(mVerificationCode)
                && Utils.matchLoginPassword(mPassword));
    }

    //处理注册结果
    private void handleRegisterResult(RegisterResult result) {
        mView.hideLoading();
        if (result.statusCode > 299 || (result.statusCode < 200 && result.statusCode != 0)) {
            mView.showAlertDialog(result.msg);
        } else {
            mTech.onRegisterResult(result);
            UINavigation.gotoCompleteRegisterInfo(mContext); //注册完成，跳到完善资料页面
        }
    }
}
