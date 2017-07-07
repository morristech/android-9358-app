package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.shidou.commonlibrary.widget.XProgressDialog;
import com.shidou.commonlibrary.widget.XToast;
import com.umeng.analytics.MobclickAgent;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.VerifyManager;
import com.xmd.cashier.service.CustomService;
import com.xmd.cashier.widget.CustomToolbar;
import com.xmd.m.network.EventTokenExpired;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class BaseActivity extends AppCompatActivity {
    private XProgressDialog progressDialog;

    protected static final int TOOL_BAR_NAV_NONE = 0;
    protected static final int TOOL_BAR_NAV_HOME = 1;
    protected static final int TOOL_BAR_NAV_BACK = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MainApplication.getInstance().addActivity(this);
        progressDialog = new XProgressDialog(this);
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(EventTokenExpired event) {
        if (!(this instanceof LoginActivity)) {
            // 关闭轮询
            CustomService.refreshOrderRecordNotify(false);
            CustomService.refreshOnlinePayNotify(false);
            AccountManager.getInstance().cleanUserInfo();
            VerifyManager.getInstance().clearVerifyList();
            showToast(getString(R.string.token_expired));
            UiNavigation.gotoLoginActivity(BaseActivity.this);
        }
    }

    public Toolbar showToolbar(int toolbarResourceId, String title) {
        return showToolbar(toolbarResourceId, title, TOOL_BAR_NAV_BACK);
    }

    public Toolbar showToolbar(int toolbarResourceId, int titleResourceId) {
        return showToolbar(toolbarResourceId, titleResourceId, TOOL_BAR_NAV_BACK);
    }

    public Toolbar showToolbar(int toolbarResourceId, int titleResourceId, int navigationMode) {
        return showToolbar(toolbarResourceId, getString(titleResourceId), navigationMode);
    }

    public Toolbar showToolbar(int toolbarResourceId, String title, int navigationMode) {
        CustomToolbar toolbar = (CustomToolbar) findViewById(toolbarResourceId);
        setSupportActionBar(toolbar);
        if (toolbar != null && !TextUtils.isEmpty(title)) {
            toolbar.setTitleString(title);
            if (navigationMode == TOOL_BAR_NAV_HOME) {
                toolbar.setNavigationIcon(R.drawable.ic_nav_menu);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onNavigationMenu();
                    }
                });
            } else if (navigationMode == TOOL_BAR_NAV_BACK) {
                toolbar.setNavigationIcon(R.drawable.ic_nav_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onKeyEventBack();
                    }
                });
            }
        }
        return toolbar;
    }

    public void onNavigationMenu() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.getInstance().removeActivity(this);
        progressDialog.dismiss();
        EventBus.getDefault().unregister(this);
    }

    public void showLoading() {
        progressDialog.show();
    }

    public void hideLoading() {
        progressDialog.hide();
    }

    public void showError(String error) {
        Utils.showAlertDialogMessage(this, error);
    }

    public void showToast(String toast) {
        XToast.showLong(toast);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && onKeyEventBack()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyEventBack() {
        finish();
        return true;
    }
}
