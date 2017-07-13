package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.ModifyPasswordResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.LoginFailDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class ModifyPasswordActivity extends BaseActivity implements TextWatcher,InputFilter{

    public final static int PASSWORD_MAX_LEN = 20;

    @BindView(R.id.old_password) EditText mOldPassWordEdt;
    @BindView(R.id.new_password) EditText mNewPassWordEdt;
    @BindView(R.id.confirm_password) EditText mConfirmPassWordEdt;
    @BindView(R.id.confirm) Button mChangePassWordBtn;

    private Subscription mModifyPasswordSubscription;
    private Subscription mLoginSubscription;

    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        ButterKnife.bind(this);

        setTitle(R.string.settings_activity_modify_password);
        setBackVisible(true);

        mModifyPasswordSubscription = RxBus.getInstance().toObservable(ModifyPasswordResult.class).subscribe(
                result -> finish());

        mOldPassWordEdt.setFilters(new InputFilter[]{this, new InputFilter.LengthFilter(PASSWORD_MAX_LEN)});
        mOldPassWordEdt.addTextChangedListener(this);
        mNewPassWordEdt.setFilters(new InputFilter[]{this, new InputFilter.LengthFilter(PASSWORD_MAX_LEN)});
        mNewPassWordEdt.addTextChangedListener(this);
        mConfirmPassWordEdt.setFilters(new InputFilter[]{this, new InputFilter.LengthFilter(PASSWORD_MAX_LEN)});
        mConfirmPassWordEdt.addTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mModifyPasswordSubscription);
    }

    @OnClick(R.id.confirm)
    public void modifyPassword(){
        String nPassword = null,oPassword = null;

        nPassword = mNewPassWordEdt.getText().toString();
        oPassword = mOldPassWordEdt.getText().toString();
        mPassword = mConfirmPassWordEdt.getText().toString();
        if(!nPassword.equals(mPassword)){
            new LoginFailDialog(this,R.string.modify_password_failed, R.string.modify_password_change_confirm_err).show();
            return;
        }

        showProgressDialog(getString(R.string.settings_activity_modify_password));
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_OLD_PASSWORD, oPassword);
        params.put(RequestConstant.KEY_NEW_PASSWORD, nPassword);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MODIFY_PASSWORD, params);
    }

    private void login(){
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USERNAME, SharedPreferenceHelper.getUserAccount());
        params.put(RequestConstant.KEY_PASSWORD, mPassword);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN, params);
    }

    private void handleLoginResult(LoginResult loginResult) {
        dismissProgressDialogIfShowing();
        if (loginResult.status.equals("fail")) {
            makeShortToast(loginResult.message);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            SharedPreferenceHelper.setUserToken(loginResult.token);
            SharedPreferenceHelper.setUserName(loginResult.name);
            SharedPreferenceHelper.setUserId(loginResult.userId);
            SharedPreferenceHelper.setEmchatId(loginResult.emchatId);
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String oldPW = mOldPassWordEdt.getText().toString();
        String newPW = mNewPassWordEdt.getText().toString();
        String confirmPW = mConfirmPassWordEdt.getText().toString();

        if (Util.matchPassWordFormat(confirmPW) && Util.matchPassWordFormat(newPW) && Util.matchPassWordFormat(oldPW)) {
            mChangePassWordBtn.setEnabled(true);
        } else {
            mChangePassWordBtn.setEnabled(false);
        }
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            if (!Character.isLetterOrDigit(source.charAt(i))) {
                return "";
            }
        }
        return null;
    }
}
