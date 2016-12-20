package com.xmd.technician.presenter;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.Editable;

import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.ThreadPoolManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.contract.RegisterContract;
import com.xmd.technician.databinding.ActivityRegisterBinding;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by heyangya on 16-12-20.
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter {
    private ActivityRegisterBinding mBinding;
    private LoginTechnician mTech = LoginTechnician.getInstance();
    public ObservableBoolean mCanGetVerificationCode = new ObservableBoolean();//是否能获取验证码
    public ObservableBoolean mCanGotoSetInfoView = new ObservableBoolean(); //是否能跳转到设置资料页面
    public ObservableField<String> mSendVerificationText = new ObservableField<>();
    private long mSendVerificationTime;
    private String mVerificationCode;
    private String mPassword;
    private Future mSendVerificationFuture;
    private static final int VERIFICATION_INTERVAL = 60000;//验证码间隔60秒

    public RegisterPresenter(Context context, RegisterContract.View view, ActivityRegisterBinding binding) {
        super(context, view);
        mBinding = binding;
    }

    @Override
    public void onClickNextStep() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSendVerificationTime = SharedPreferenceHelper.getVerificationCodeTime();
        checkVerificationCode();
        if (System.currentTimeMillis() - mSendVerificationTime < VERIFICATION_INTERVAL) {
            startVerificationTime();
        }
        mBinding.setTech(mTech);
        mBinding.setPresenter(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSendVerificationFuture != null) {
            mSendVerificationFuture.cancel(true);
        }
    }

    @Override
    public void onClickGetVerificationCode() {
        mSendVerificationTime = System.currentTimeMillis();
        SharedPreferenceHelper.setVerificationCodeTime(mSendVerificationTime);
        startVerificationTime();
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, mTech.phoneNumber);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ICODE, params);
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
        mCanGetVerificationCode.set(Utils.matchPhoneNumFormat(mTech.phoneNumber)
                && System.currentTimeMillis() - mSendVerificationTime >= VERIFICATION_INTERVAL);
        if (System.currentTimeMillis() - mSendVerificationTime >= VERIFICATION_INTERVAL) {
            mSendVerificationText.set("获取验证码");
        }
    }

    @Override
    public void setPhoneNumber(Editable s) {
        mTech.phoneNumber = s.toString();
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

    private void checkCanGotoSetInfoView() {
        mCanGotoSetInfoView.set(mCanGetVerificationCode.get()
                && Utils.matchVerificationCode(mVerificationCode)
                && Utils.matchLoginPassword(mPassword));
    }

}
