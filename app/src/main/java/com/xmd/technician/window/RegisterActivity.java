package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.Util;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ClearableEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class RegisterActivity extends BaseActivity implements TextWatcher{

    public static final int ICODE_RESEND_TIME = 60000;//验证码重发时间

    public final static String EXTRA_USERNAME = "username";
    public final static String EXTRA_PASSWORD = "password";

    @Bind(R.id.icode) EditText mSecurityCodeEdt;
    @Bind(R.id.invite_club) EditText mClubInviteEdt;
    @Bind(R.id.send_icode) Button mSendMSMBtn;
    @Bind(R.id.register) Button mRegisterBtn;
    @Bind(R.id.user_name) ClearableEditText mEtUsername;
    @Bind(R.id.password) ClearableEditText mEtPassword;

    private long mCurrentTimeMillis = 0;
    private boolean mPhoneNumReady = true;
    private Handler mHandler = new Handler();

    private Subscription mRegisterSubscription;

    //定时刷新验证码剩余时间
    private final Runnable mRefreshICodeTask = new Runnable() {
        @Override
        public void run() {
            long timeLeft = getICodeTimeLeft();
            refreshICodeButton(timeLeft);
            if (timeLeft > 0) {
                mHandler.postDelayed(mRefreshICodeTask, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        setTitle(R.string.register);
        setBackVisible(true);

        mSecurityCodeEdt.addTextChangedListener(this);
        mClubInviteEdt.addTextChangedListener(this);
        mEtPassword.addTextChangedListener(this);
        mEtUsername.addTextChangedListener(this);

        mEtPassword.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if (!Character.isLetterOrDigit(source.charAt(i))) {
                                return "";
                            }
                        }
                        return null;
                    }
                },
                new InputFilter.LengthFilter(20)
        });

        mRegisterSubscription = RxBus.getInstance().toObservable(RegisterResult.class).subscribe(
                result -> handleRegisterResult(result)
        );

        mEtUsername.setText(SharedPreferenceHelper.getUserAccount());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRefreshICodeTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mRefreshICodeTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mRegisterSubscription);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        String securityCode = mSecurityCodeEdt.getText().toString();
        String password = mEtPassword.getText().toString();
        String account = mEtUsername.getText().toString();
        if (Util.matchPassWordFormat(password) && Util.matchPhoneNumFormat(account)&&Util.matchSecurityCodeFormat(securityCode) && !TextUtils.isEmpty(mClubInviteEdt.getText())) {
            mRegisterBtn.setEnabled(true);
        } else {
            mRegisterBtn.setEnabled(false);
        }

        if(Util.matchPhoneNumFormat(account)){
            mSendMSMBtn.setEnabled(true);
        }else {
            mSendMSMBtn.setEnabled(false);
        }
    }

    private void refreshICodeButton(long timeLeft) {
        if (timeLeft > 0) {
            mSendMSMBtn.setEnabled(false);
            mSendMSMBtn.setText(String.format(getString(R.string.register_resend_security_code), (timeLeft + 999) / 1000));
        } else {
            mSendMSMBtn.setEnabled(mPhoneNumReady);
            mSendMSMBtn.setText(R.string.register_send_security_code);
        }
    }

    /**
     * 获取验证发送后的剩余时间，总时长是60s
     *
     * @return 剩余时间，ms为单位
     */
    private long getICodeTimeLeft() {
        long interval = SystemClock.elapsedRealtime() - mCurrentTimeMillis;
        if (interval < ICODE_RESEND_TIME) {
            return ICODE_RESEND_TIME - interval;
        } else {
            return 0;
        }
    }

    @OnClick(R.id.register)
    public void register(){
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, mEtUsername.getText().toString());
        params.put(RequestConstant.KEY_PASSWORD, mEtPassword.getText().toString());
        params.put(RequestConstant.KEY_ICODE, mSecurityCodeEdt.getText().toString());
        params.put(RequestConstant.KEY_CLUB_CODE, mClubInviteEdt.getText().toString());
        params.put(RequestConstant.KEY_LOGIN_CHANEL, "android"+AppConfig.getAppVersionCode());

        //Save the usernames
        SharedPreferenceHelper.setUserAccount(mEtUsername.getText().toString());

        showProgressDialog(getString(R.string.register));

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_REGISTER, params);

        Utils.reportRegisterEvent(this, "注册验证");
    }

    @OnClick(R.id.send_icode)
    public void sendSecurityCodeMSM(){
        mHandler.removeCallbacks(mRefreshICodeTask);
        //do something
        mCurrentTimeMillis = SystemClock.elapsedRealtime();
        mHandler.post(mRefreshICodeTask);

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, mEtUsername.getText().toString());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ICODE, params);
    }

    private void handleRegisterResult(RegisterResult result) {
        if (result.status.equals("fail")) {
            dismissProgressDialogIfShowing();
            makeShortToast(result.message);
        } else {
            SharedPreferenceHelper.setUserToken(result.token);
            SharedPreferenceHelper.setUserName(result.name);
            SharedPreferenceHelper.setUserId(result.userId);
            SharedPreferenceHelper.setEmchatId(result.emchatId);
            SharedPreferenceHelper.setEMchatPassword(result.emchatPassword);

            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_EMCHAT, null);

            dismissProgressDialogIfShowing();
            Intent intent = new Intent(RegisterActivity.this, InfoInputActivity.class);
            intent.putExtra(InfoInputActivity.EXTRA_PHONE_NUM, result.phoneNum);
            startActivity(intent);
            finish();
        }
    }
}
