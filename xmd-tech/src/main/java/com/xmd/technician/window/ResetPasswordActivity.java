package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.xmd.technician.R;
import com.xmd.technician.common.DESede;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ResetPasswordResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class ResetPasswordActivity extends BaseActivity implements TextWatcher {
    public static final int ICODE_RESEND_TIME = 60000;//验证码重发时间
    @BindView(R.id.user_name)
    EditText mAccountNumberEdt;
    @BindView(R.id.icode)
    EditText mSecurityCodeEdt;
    @BindView(R.id.password)
    EditText mPassWordEdt;
    @BindView(R.id.send_icode)
    Button mSendMSMBtn;
    @BindView(R.id.confirm)
    Button mConfirmBtn;

    private Handler mHandler = new Handler();
    private boolean mPhoneNumReady = false;
    private long mCurrentTimeMillis = 0;

    private Subscription mResetPasswordSubscription;

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
        setContentView(R.layout.activity_reset_password);

        ButterKnife.bind(this);

        setTitle(R.string.find_password);
        setBackVisible(true);

        mAccountNumberEdt.addTextChangedListener(this);
        mSecurityCodeEdt.addTextChangedListener(this);
        mPassWordEdt.addTextChangedListener(this);
        mPassWordEdt.setFilters(new InputFilter[]{
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

        mResetPasswordSubscription = RxBus.getInstance().toObservable(ResetPasswordResult.class).subscribe(
                resetPasswordResult -> {
                    dismissProgressDialogIfShowing();
                    finish();
                }
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
        RxBus.getInstance().unsubscribe(mResetPasswordSubscription);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String account = mAccountNumberEdt.getText().toString();
        String password = mPassWordEdt.getText().toString();
        String securityCode = mSecurityCodeEdt.getText().toString();
        if (Util.matchPhoneNumFormat(account)) {
            mPhoneNumReady = true;
            refreshICodeButton(getICodeTimeLeft());
        } else {
            mPhoneNumReady = false;
            refreshICodeButton(getICodeTimeLeft());
        }

        if (Util.matchSecurityCodeFormat(securityCode) && Util.matchPassWordFormat(password) && Util.matchPhoneNumFormat(account)) {
            mConfirmBtn.setEnabled(true);
        } else {
            mConfirmBtn.setEnabled(false);
        }
    }

    @OnClick(R.id.confirm)
    public void resetPassword() {
        String username = mAccountNumberEdt.getText().toString();
        String password = mPassWordEdt.getText().toString();
        String icode = mSecurityCodeEdt.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USERNAME, username);
        params.put(RequestConstant.KEY_PASSWORD, password);
        params.put(RequestConstant.KEY_ICODE, icode);

        showProgressDialog(getString(R.string.find_password));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_RESET_PASSWORD, params);
    }

    @OnClick(R.id.send_icode)
    public void sendSecurityCodeMSM() {
        mHandler.removeCallbacks(mRefreshICodeTask);
        //do something
        mCurrentTimeMillis = SystemClock.elapsedRealtime();
        mHandler.post(mRefreshICodeTask);
        String mobile = mAccountNumberEdt.getText().toString();
        String sign = DESede.encrypt(mobile + RequestConstant.KEY_WHICH_VALUE);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_MOBILE, mobile);
        params.put(RequestConstant.KEY_WHICH, RequestConstant.KEY_WHICH_VALUE);
        params.put(RequestConstant.KEY_SIGN, sign);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ICODE, params);
    }

    @OnClick(R.id.login)
    public void gotoLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
