package com.xmd.technician.window;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.ResetPasswordResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

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

    private String mUserName;
    private String mPassword;

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

        setTitle(R.string.login);
        setBackVisible(true);

        mUserName = getIntent().getExtras().getString(EXTRA_USERNAME);
        mPassword = getIntent().getExtras().getString(EXTRA_PASSWORD);

        mSecurityCodeEdt.addTextChangedListener(this);
        mClubInviteEdt.addTextChangedListener(this);

        mRegisterSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                loginResult -> handleRegisterResult(loginResult)
        );

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

        if (Util.matchSecurityCodeFormat(securityCode) && !TextUtils.isEmpty(mClubInviteEdt.getText())) {
            mRegisterBtn.setEnabled(true);
        } else {
            mRegisterBtn.setEnabled(false);
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
        params.put(RequestConstant.KEY_MOBILE, mUserName);
        params.put(RequestConstant.KEY_PASSWORD, mPassword);
        params.put(RequestConstant.KEY_ICODE, mSecurityCodeEdt.getText().toString());
        params.put(RequestConstant.KEY_CLUB_CODE, mClubInviteEdt.getText().toString());
        params.put(RequestConstant.KEY_CHANEL, "android"+AppConfig.getAppVersionCode());

        showProgressDialog(getString(R.string.register));

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_REGISTER, params);
    }

    @OnClick(R.id.send_icode)
    public void sendSecurityCodeMSM(){
        mHandler.removeCallbacks(mRefreshICodeTask);
        //do something
        mCurrentTimeMillis = SystemClock.elapsedRealtime();
        mHandler.post(mRefreshICodeTask);

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, mUserName.toString());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ICODE, params);
    }

    private void handleRegisterResult(LoginResult loginResult) {
        if (loginResult.status.equals("fail")) {
            dismissProgressDialogIfShowing();
            makeShortToast(loginResult.message);
        } else {
            SharedPreferenceHelper.setUserToken(loginResult.token);
            SharedPreferenceHelper.setUserName(loginResult.name);
            SharedPreferenceHelper.setUserId(loginResult.userId);
            EMClient.getInstance().login(loginResult.emchatId, loginResult.emchatPassword, new EMCallBack() {
                @Override
                public void onSuccess() {
                    dismissProgressDialogIfShowing();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                    if (!EMClient.getInstance().updateCurrentUserNick(loginResult.name)) {
                        Logger.e("LoginActivity", "update current user nick fail");
                    }
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {
                    dismissProgressDialogIfShowing();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                }
            });
        }
    }
}
