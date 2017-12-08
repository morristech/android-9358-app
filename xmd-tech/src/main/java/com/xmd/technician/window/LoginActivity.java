package com.xmd.technician.window;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.crazyman.library.PermissionTool;
import com.xmd.app.XmdActivityManager;
import com.xmd.app.event.EventLogout;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.technician.AppConfig;
import com.xmd.technician.BuildConfig;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.contract.LoginContract;
import com.xmd.technician.http.RetrofitServiceFactory;
import com.xmd.technician.presenter.LoginPresenter;
import com.xmd.technician.widget.AlertDialogBuilder;
import com.xmd.technician.widget.ClearableEditText;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R.id.layout_phone_login)
    View mLayoutPhoneLogin;
    @BindView(R.id.layout_tech_no_login)
    View mLayoutTechNoLogin;

    @BindView(R.id.phone_number)
    ClearableEditText mEtUsername;
    @BindView(R.id.password1)
    ClearableEditText mEtPassword;
    @BindView(R.id.login_btn1)
    Button mBtnLogin1;

    @BindView(R.id.invite_code)
    EditText mInviteCodeEditText;
    @BindView(R.id.tech_no)
    EditText mTechNoEditText;
    @BindView(R.id.password2)
    ClearableEditText mPasswordEditText2;
    @BindView(R.id.login_btn2)
    Button mBtnLogin2;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.server_host)
    Spinner mSpServerHost;
    private String mSelectedServerHost;

    private boolean mNeedRestartApp;

    LoginContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0xffff7d6b);
        }

        ButterKnife.bind(this);

        setTitle(R.string.login);
        setBackVisible(true);

        mPresenter = new LoginPresenter(this, this);
        mEtPassword.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.setPassword(s.toString());
            }
        });
        mEtUsername.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.setPhoneNumber(s.toString());
            }
        });

        mInviteCodeEditText.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.setInviteCode(s.toString());
            }
        });

        mTechNoEditText.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.setTechNumber(s.toString());
            }
        });

        mPasswordEditText2.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.setPassword(s.toString());
            }
        });

        initServerHost();

        mPresenter.onCreate();
        mTvVersion.setText("v" + AppConfig.getAppVersionNameAndCode());

        if (BuildConfig.FLAVOR.equals("dev") || BuildConfig.BUILD_TYPE.equals("debug")) {
            PermissionTool.requestPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new String[]{"需要访问存储器"}, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    public void initServerHost() {
        mEtUsername.setText(SharedPreferenceHelper.getUserAccount());
        ArrayAdapter<String> serverHosts = new ArrayAdapter<>(this, R.layout.spinner_item, AppConfig.sServerHosts);
        serverHosts.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpServerHost.setAdapter(serverHosts);

        mSelectedServerHost = SharedPreferenceHelper.getServerHost();
        int selection = 0;
        if (!TextUtils.isEmpty(mSelectedServerHost)) {
            selection = AppConfig.sServerHosts.indexOf(mSelectedServerHost);
            if (selection < 0) {
                selection = 0;
            }
            mSpServerHost.setSelection(selection);
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
    }

    @OnClick({R.id.login_btn1, R.id.login_btn2})
    public void login() {
        mPresenter.onClickLogin();
    }

    @OnClick({R.id.switch_login1, R.id.switch_login2})
    public void switchLogin() {
        mPresenter.onClickSwitchLoginMethod();
    }

    @OnClick(R.id.register_btn)
    public void register() {
        mPresenter.onClickRegister();
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

    @OnClick(R.id.find_password)
    public void gotoFindPassword() {
        mPresenter.onClickFindPassword();
    }

    @Override
    public void showTechNoLogin() {
        mLayoutPhoneLogin.setVisibility(View.GONE);
        mLayoutTechNoLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPhoneLogin() {
        mLayoutPhoneLogin.setVisibility(View.VISIBLE);
        mLayoutTechNoLogin.setVisibility(View.GONE);
    }

    @Override
    public void setPhoneNumber(String value) {
        mEtUsername.setText(value);
    }

    @Override
    public void setInviteCode(String value) {
        mInviteCodeEditText.setText(value);
    }

    @Override
    public void setTechNo(String value) {
        mTechNoEditText.setText(value);
    }

    @Override
    public void enableLogin(boolean enable) {
        if (mLayoutPhoneLogin.getVisibility() == View.VISIBLE) {
            mBtnLogin1.setEnabled(enable);
        } else {
            mBtnLogin2.setEnabled(enable);
        }
    }

    @Override
    public void finishSelf() {
        finish();
    }

    private class BaseTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

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
