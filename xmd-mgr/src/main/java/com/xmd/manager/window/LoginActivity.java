package com.xmd.manager.window;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.xmd.app.XmdActivityManager;
import com.xmd.app.event.EventLogout;
import com.xmd.app.utils.PermissionUtils;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.m.network.OkHttpUtil;
import com.xmd.manager.AppConfig;
import com.xmd.manager.Constant;
import com.xmd.manager.ManagerAccountManager;
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
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.ClearableEditText;
import com.xmd.manager.widget.LoadingDialog;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import rx.Subscription;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.username)
    ClearableEditText mEtUsername;
    @BindView(R.id.password)
    ClearableEditText mEtPassword;
    @BindView(R.id.server_host)
    Spinner mSpServerHost;
    @BindView(R.id.tv_version)
    TextView mTvVersion;

    private String mSelectedServerHost;
    private Subscription mLoginSubscription;
    private InputMethodManager mInputManager;
    private LoadingDialog mLoadingDialog;
    private boolean mNeedRestartApp;

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

        mSpServerHost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String serverHost = (String) parent.getItemAtPosition(position);
                SharedPreferenceHelper.setServerHost(serverHost);
                RetrofitServiceFactory.recreateService();
                if (SharedPreferenceHelper.isDevelopMode() && SharedPreferenceHelper.getServerHost().contains("spa.93wifi.com")) {
                    mNeedRestartApp = true;
                } else if (!SharedPreferenceHelper.isDevelopMode() && !SharedPreferenceHelper.getServerHost().contains("spa.93wifi.com")) {
                    mNeedRestartApp = true;
                }
                SharedPreferenceHelper.setDevelopMode(!SharedPreferenceHelper.getServerHost().contains("spa.93wifi.com"));
                if (mNeedRestartApp) {
                    new AlertDialogBuilder(LoginActivity.this).setMessage("切换运行环境，需要重新打开应用")
                            .setCancelable(false)
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    XmdActivityManager.getInstance().exitApplication();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        if (PermissionUtils.isNotificationEnabled(LoginActivity.this)) {
            if (mLoadingDialog == null) {
                mLoadingDialog = new LoadingDialog(this);
            }
            mLoadingDialog.show();
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN, params);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("提示：")
                    .setMessage(ResourceUtils.getString(R.string.notification_manager_apply))
                    .setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.toSetting(LoginActivity.this);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

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

            ManagerAccountManager.getInstance().onLogin();

            if (Constant.MULTI_CLUB_ROLE.equals(loginResult.roles) || Constant.CHAIN_MANAGER_ROLE.equals(loginResult.roles)) {
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

    @Override
    public void onTokenExpired(EventTokenExpired event) {
        //do nothing
    }

    @Subscribe
    public void onLogoutEvent(EventLogout logout) {
        //do nothing
    }
}
