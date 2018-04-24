package com.xmd.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XProgressDialog;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.event.EventLogout;
import com.xmd.app.receiver.NetWorkStatusListener;
import com.xmd.app.receiver.NetworkConnectChangedReceiver;
import com.xmd.m.network.EventTokenExpired;

import org.greenrobot.eventbus.Subscribe;

/**
 * 应当作为所有activity的基类
 */

public class BaseActivity extends AppCompatActivity implements NetWorkStatusListener {
    protected XProgressDialog progressDialog;
    protected Toolbar mToolbar;
    private RelativeLayout toolbarBack;
    private RelativeLayout toolbarRightImage;
    private ImageView rightImage;
    private NetworkConnectChangedReceiver networkReceiver;
    private boolean networkIsUsable;
    private boolean currentNetworkCanUse;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusSafeRegister.register(this);
        XmdActivityManager.getInstance().addActivity(this);
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusSafeRegister.unregister(this);
        XmdActivityManager.getInstance().removeActivity(this);
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        XmdActivityManager.getInstance().onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        XmdActivityManager.getInstance().onStop(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initToolbar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initToolbar();
    }

    protected void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            toolbarBack = (RelativeLayout) findViewById(R.id.rl_toolbar_back);
            if (toolbarBack != null) {
                toolbarBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }

            toolbarRightImage = (RelativeLayout) findViewById(R.id.rl_toolbar_right);
            rightImage = (ImageView) findViewById(R.id.img_toolbar_right);
            setRightVisible(false, -1);

        }
    }

    public void setBackVisible(boolean visible) {
        if (toolbarBack != null) {
            toolbarBack.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setRightVisible(boolean isVisible, int srcId) {
        if (toolbarRightImage != null) {
            int visible = isVisible ? View.VISIBLE : View.GONE;
            toolbarRightImage.setVisibility(visible);
            if (isVisible && srcId != -1) {
                rightImage.setImageResource(srcId);
                toolbarRightImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightImageClickedListener();
                    }
                });
            }
        }

    }

    public void onRightImageClickedListener() {

    }


    @Subscribe
    public void onTokenExpired(EventTokenExpired event) {
        finish();
    }

    @Subscribe
    public void onLogoutEvent(EventLogout logout) {
        finish();
    }

    public void showLoading(String message) {
        if (progressDialog == null) {
            progressDialog = new XProgressDialog(this);
        }
        progressDialog.setTitle(message);
        progressDialog.show(message);
    }

    public void showLoading() {
        showLoading("");
    }

    public void showLoading(String message, boolean cancelAble) {
        if (progressDialog == null) {
            progressDialog = new XProgressDialog(this);
        }
        progressDialog.setCancelable(cancelAble);
        progressDialog.setTitle(message);
        progressDialog.show(message);
    }

    public void hideLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void showDialog(String error) {
        new AlertDialog.Builder(this)
                .setMessage(error)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    public void showToast(String toast) {
        XToast.showLong(toast);
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        if (titleView != null) {
            titleView.setText(title);
        } else {
            super.setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        if (titleView != null) {
            titleView.setText(titleId);
        } else {
            super.setTitle(titleId);
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        networkReceiver = new NetworkConnectChangedReceiver();
        registerReceiver(networkReceiver, filter);
        networkReceiver.initNetWorkListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (networkIsUsable && currentNetworkCanUse) {
            XToast.show("网络已连接，请刷新或重新进入页面");
            networkIsUsable = false;
        }
    }

    @Override
    public void netWorkUsable(boolean isCanUse) {
        if (isCanUse) {
            currentNetworkCanUse = true;
        } else {
            currentNetworkCanUse = false;
            networkIsUsable = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            builder.setTitle("网络设置提示")
                    .setMessage("网络连接不可用，是否进行设置？")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            BaseActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }
}
