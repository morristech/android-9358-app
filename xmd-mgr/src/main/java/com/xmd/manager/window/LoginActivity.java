package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.m.network.OkHttpUtil;
import com.xmd.manager.AppConfig;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.LoginResult;
import com.xmd.manager.widget.ClearableEditText;
import com.xmd.manager.widget.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;
import rx.Subscription;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.username)
    ClearableEditText mEtUsername;
    @Bind(R.id.password)
    ClearableEditText mEtPassword;
    @Bind(R.id.server_host)
    Spinner mSpServerHost;
    @Bind(R.id.tv_version)
    TextView mTvVersion;

    private String mSelectedServerHost;
    private Subscription mLoginSubscription;
    private InputMethodManager mInputManager;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setLeftVisible(false, -1);
        initContent();
        mInputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mLoginSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                loginResult -> handleLoginResult(loginResult));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.getInstance().unsubscribe(mLoginSubscription);
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
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
            if (selection < 0) {
                selection = 0;
            }
            mSpServerHost.setSelection(selection);
        }

        mTvVersion.setText("v" + AppConfig.getAppVersionNameAndCode());
    }

    @OnClick(R.id.login)
    public void login() {
        String username = mEtUsername.getText().toString();
        if (Utils.isEmpty(username)) {
            ToastUtils.showToastShort(LoginActivity.this, ResourceUtils.getString(R.string.login_activity_hint_username_not_empty));
            return;
        }

        String password = mEtPassword.getText().toString();
        if (Utils.isEmpty(password)) {
            ToastUtils.showToastShort(LoginActivity.this, ResourceUtils.getString(R.string.login_activity_hint_password_not_empty));
            return;
        }

        mSelectedServerHost = mSpServerHost.getSelectedItem().toString();

        if (Utils.isEmpty(mSelectedServerHost)) {
            ToastUtils.showToastShort(LoginActivity.this, ResourceUtils.getString(R.string.login_activity_hint_server_host_not_empty));
            return;
        }
        hideKeyboard();
        //Save the usernames
        SharedPreferenceHelper.setUserAccount(username);
        //Save the server host
        SharedPreferenceHelper.setServerHost(mSelectedServerHost);
        //Save the update server host
        // Recreate the retrofit service
        RetrofitServiceFactory.recreateService();


        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USERNAME, username);
        params.put(RequestConstant.KEY_PASSWORD, password);
        params.put(RequestConstant.KEY_APP_VERSION, "android." + AppConfig.getAppVersionNameAndCode());

        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.show();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN, params);
    }

    @OnLongClick(R.id.tv_version)
    public boolean toggleServerHostSpinner() {
        if (mSpServerHost.getVisibility() == View.GONE) {
            mSpServerHost.setVisibility(View.VISIBLE);
        } else {
            mSpServerHost.setVisibility(View.GONE);
        }
        return true;
    }

    private void handleLoginResult(LoginResult loginResult) {
        mLoadingDialog.dismiss();
        if (loginResult.status.equals("fail")) {
            makeShortToast(loginResult.message);
        } else {
            OkHttpUtil.getInstance().setCommonHeader("token", loginResult.token);
            SharedPreferenceHelper.saveUser(loginResult);
            SharedPreferenceHelper.setMultiClubToken(loginResult.token);

            EventBus.getDefault().removeStickyEvent(EventLogout.class);
            EventLogin eventLogin = new EventLogin(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserId());
            eventLogin.setChatId(SharedPreferenceHelper.getEmchatId());
            eventLogin.setChatPassword(SharedPreferenceHelper.getEmchatPassword());
            EventBus.getDefault().postSticky(eventLogin);

            if (Constant.MULTI_CLUB_ROLE.equals(loginResult.roles)) {
                startActivity(new Intent(LoginActivity.this, ClubListActivity.class));
                finish();
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                mInputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
