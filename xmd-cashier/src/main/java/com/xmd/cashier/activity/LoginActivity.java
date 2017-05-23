package com.xmd.cashier.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.LoginContract;
import com.xmd.cashier.presenter.LoginPresenter;

public class LoginActivity extends BaseActivity implements LoginContract.View {
    private LoginContract.Presenter mPresenter;
    private Button mLoginButton;
    private EditText mLoginNameEditText;
    private EditText mLoginPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_login);
        initView();

        mPresenter = new LoginPresenter(this, this);
        mPresenter.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    private void initView() {
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mLoginNameEditText = (EditText) findViewById(R.id.edt_username);
        mLoginPasswordEditText = (EditText) findViewById(R.id.edt_password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        mLoginNameEditText.setInputType(0);
        mLoginPasswordEditText.setInputType(0);
    }

    @Override
    public String getUserName() {
        return ((EditText) findViewById(R.id.edt_username)).getText().toString();
    }

    @Override
    public String getPassword() {
        return ((EditText) findViewById(R.id.edt_password)).getText().toString();
    }

    @Override
    public void setLoginName(String userName) {
        mLoginNameEditText.setText(userName);
        if (!TextUtils.isEmpty(userName)) {
            mLoginPasswordEditText.requestFocus();
        }
    }

    @Override
    public void cleanPassword() {
        mLoginPasswordEditText.setText("");
    }

    @Override
    public void onLoginStart() {
        showLoading();
        mLoginButton.setEnabled(false);
    }

    @Override
    public void onLoginEnd(String error) {
        hideLoading();
        mLoginButton.setEnabled(true);
        if (error != null) {
            Utils.showAlertDialogMessage(LoginActivity.this, error);
        }
    }

    @Override
    public void showVersionName(String versionName) {
        ((TextView) findViewById(R.id.tv_versionName)).setText(versionName);
    }

    public void onClickLogin(View view) {
        mPresenter.onClickLoginButton();
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {

    }

    public void onVersionClick(View view) {
        mPresenter.onClickVersionView();
    }

    @Override
    public void finishSelf() {
        finish();
    }
}
