package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
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

public class PreLoginActivity extends BaseActivity implements TextWatcher {

    @Bind(R.id.login_username_txt) ClearableEditText mEtUsername;
    @Bind(R.id.login_btn) Button mBtnLogin;
    @Bind(R.id.server_host) Spinner mSpServerHost;
    @Bind(R.id.update_server_host) Spinner mSpUpdateServerHost;

    private String mUsername;
    private String mSelectedServerHost;
    private String mSelectedUpdateServerHost;
    private Subscription mLoginSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);

        ButterKnife.bind(this);

        setTitle(R.string.login);

        initContent();

        mEtUsername.addTextChangedListener(this);

        /*mLoginSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                loginResult -> handleLoginResult(loginResult));*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                loginResult -> handleLoginResult(loginResult));
        mEtUsername.setText(SharedPreferenceHelper.getUserAccount());
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxBus.getInstance().unsubscribe(mLoginSubscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initContent() {
        //mEtUsername.setText(SharedPreferenceHelper.getUserAccount());

        ArrayAdapter<String> serverHosts = new ArrayAdapter<>(this, R.layout.spinner_item, AppConfig.sServerHosts);
        serverHosts.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpServerHost.setAdapter(serverHosts);

        ArrayAdapter<String> updateServerHosts = new ArrayAdapter<>(this, R.layout.spinner_item, AppConfig.sServerHosts);
        updateServerHosts.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpUpdateServerHost.setAdapter(updateServerHosts);

        mSelectedServerHost = SharedPreferenceHelper.getServerHost();
        int selection = 0;
        if (Utils.isNotEmpty(mSelectedServerHost)) {
            selection = AppConfig.sServerHosts.indexOf(mSelectedServerHost);
            if (selection < 0) {
                selection = 0;
            }
            mSpServerHost.setSelection(selection);
        }

        mSelectedUpdateServerHost = SharedPreferenceHelper.getUpdateServer();
//        if (Utils.isNotEmpty(mSelectedUpdateServerHost)) {
//            selection = AppConfig.sServerHosts.indexOf(mSelectedUpdateServerHost);
//            if (selection < 0) {
//                selection = 0;
//            }
//            mSpUpdateServerHost.setSelection(selection);
//        }
        Log.i("TAGG","ServerHost"+mSelectedServerHost);

    }

    @OnClick(R.id.login_btn)
    public void login() {

        mUsername = mEtUsername.getText().toString();
        if (TextUtils.isEmpty(mUsername)) {
            makeShortToast(ResourceUtils.getString(R.string.login_activity_hint_username_not_empty));
            return;
        }

        mSelectedServerHost = mSpServerHost.getSelectedItem().toString();
      //  mSelectedUpdateServerHost = mSpUpdateServerHost.getSelectedItem().toString();
       // mSelectedUpdateServerHost = "http://service.xiaomodo.com";

        //Save the usernames
        SharedPreferenceHelper.setUserAccount(mUsername);
        //Save the server host
        SharedPreferenceHelper.setServerHost(mSelectedServerHost);
        //Save the update server host
        SharedPreferenceHelper.setUpdateServer(mSelectedUpdateServerHost);
        // Recreate the retrofit service
        RetrofitServiceFactory.recreateService();

        showProgressDialog(getString(R.string.login_signing));
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USERNAME, mUsername);
        params.put(RequestConstant.KEY_PASSWORD, "");
        params.put(RequestConstant.KEY_APP_VERSION, "android." + AppConfig.getAppVersionNameAndCode());

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN, params);
    }

    @OnLongClick(R.id.toggle_server_host)
    public boolean toggleServerHostSpinner() {
        if (mSpServerHost.getVisibility() == View.GONE) {
            mSpServerHost.setVisibility(View.VISIBLE);
            mSpUpdateServerHost.setVisibility(View.VISIBLE);
        } else {
            mSpServerHost.setVisibility(View.GONE);
            mSpUpdateServerHost.setVisibility(View.GONE);
        }
        return true;
    }

    private void handleLoginResult(LoginResult loginResult) {
        dismissProgressDialogIfShowing();
        if (loginResult.statusCode == 2) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (loginResult.statusCode == 1) {
            startActivity(new Intent(this, LoginActivity.class));
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
        String account = mEtUsername.getText().toString();
        if (Util.matchPhoneNumFormat(account)) {
            mBtnLogin.setEnabled(true);
        } else {
            mBtnLogin.setEnabled(false);
        }
    }
}