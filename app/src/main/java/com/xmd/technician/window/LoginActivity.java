package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContact;
import com.hyphenate.chat.EMGroupManager;
import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Util;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.RetrofitServiceFactory;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ClearableEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import rx.Subscription;

public class LoginActivity extends BaseActivity implements TextWatcher{

    @Bind(R.id.login_username_txt) ClearableEditText mEtUsername;
    @Bind(R.id.login_password_txt) ClearableEditText mEtPassword;
    @Bind(R.id.login_btn) Button mBtnLogin;
    @Bind(R.id.toggle_server_host) Button mBtnToggleServerHost;
    @Bind(R.id.server_host) Spinner mSpServerHost;

    private Subscription mLoginSubscription;
    private String mUsername;
    private String mPassword;
    private String mSelectedServerHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setTitle(R.string.login);

        initContent();

        mLoginSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                loginResult -> handleLoginResult(loginResult));

        mEtPassword.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        }, new InputFilter.LengthFilter(20)});
        mEtPassword.addTextChangedListener(this);
        mEtUsername.addTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mLoginSubscription);
    }

    public void initContent() {
        mEtUsername.setText(SharedPreferenceHelper.getUserAccount());

        ArrayAdapter<String> serverHosts = new ArrayAdapter<>(this, R.layout.spinner_item, AppConfig.sServerHosts);
        serverHosts.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpServerHost.setAdapter(serverHosts);

        mSelectedServerHost = SharedPreferenceHelper.getServerHost();
        int selection = 0;
        if (Utils.isNotEmpty(mSelectedServerHost)) {
            selection = AppConfig.sServerHosts.indexOf(mSelectedServerHost);
            if(selection < 0) {
                selection = 0;
            }
            mSpServerHost.setSelection(selection);
        }
    }

    @OnClick(R.id.login_btn)
    public void login() {

        mUsername = mEtUsername.getText().toString();
        if(TextUtils.isEmpty(mUsername)) {
            makeShortToast(ResourceUtils.getString(R.string.login_activity_hint_username_not_empty));
            return;
        }

        mPassword = mEtPassword.getText().toString();
        if(TextUtils.isEmpty(mPassword)) {
            makeShortToast(ResourceUtils.getString(R.string.login_activity_hint_password_not_empty));
            return;
        }

        mSelectedServerHost = mSpServerHost.getSelectedItem().toString();

        //Save the usernames
        SharedPreferenceHelper.setUserAccount(mUsername);
        //Save the server host
        SharedPreferenceHelper.setServerHost(mSelectedServerHost);
        // Recreate the retrofit service
        RetrofitServiceFactory.recreateService();

        showProgressDialog(getString(R.string.login_signing));
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USERNAME, mUsername);
        params.put(RequestConstant.KEY_PASSWORD, mPassword);
        params.put(RequestConstant.KEY_APP_VERSION, "android."+ AppConfig.getAppVersionNameAndCode());

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN, params);
    }

    @OnLongClick(R.id.toggle_server_host)
    public boolean toggleServerHostSpinner() {
        if(mSpServerHost.getVisibility() == View.GONE) {
            mSpServerHost.setVisibility(View.VISIBLE);
        } else {
            mSpServerHost.setVisibility(View.GONE);
        }
        return true;
    }

    @OnClick(R.id.find_password)
    public void gotoFindPassword(){
        startActivity(new Intent(this, ResetPasswordActivity.class));
    }

    private void handleLoginResult(LoginResult loginResult) {
        if (loginResult.status.equals("fail")) {
            dismissProgressDialogIfShowing();
            makeShortToast(loginResult.message);

            if(loginResult.statusCode == 2){
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra(RegisterActivity.EXTRA_USERNAME, mUsername);
                intent.putExtra(RegisterActivity.EXTRA_PASSWORD, mPassword);
                startActivity(intent);
            }
        } else {
            SharedPreferenceHelper.setUserToken(loginResult.token);
            SharedPreferenceHelper.setUserName(loginResult.name);
            SharedPreferenceHelper.setUserId(loginResult.userId);
            SharedPreferenceHelper.setEmchatId(loginResult.emchatId);
            SharedPreferenceHelper.setEMchatPassword(loginResult.emchatPassword);
            SharedPreferenceHelper.setUserAvatar(loginResult.avatarUrl);
            EMClient.getInstance().login(loginResult.emchatId, loginResult.emchatPassword, new EMCallBack() {
                @Override
                public void onSuccess() {
                    dismissProgressDialogIfShowing();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    UserProfileProvider.getInstance().initContactList();
                    // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                    if (!EMClient.getInstance().updateCurrentUserNick(loginResult.name)) {
                        Logger.e("LoginActivity", "update current user nick fail");
                    }
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {
                    dismissProgressDialogIfShowing();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            });
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
        String password = mEtPassword.getText().toString();
        String account = mEtUsername.getText().toString();
        if (Util.matchPassWordFormat(password) && Util.matchPhoneNumFormat(account)) {
            mBtnLogin.setEnabled(true);
        } else {
            mBtnLogin.setEnabled(false);
        }
    }
}
