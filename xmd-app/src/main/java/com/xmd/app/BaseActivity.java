package com.xmd.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.shidou.commonlibrary.widget.XProgressDialog;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.event.EventTokenExpired;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class BaseActivity extends AppCompatActivity {
    private XProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onTokenExpired(EventTokenExpired event) {
        finish();
    }

    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new XProgressDialog(this);
        }
        progressDialog.show();
    }

    public void hideLoading() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    public void showError(String error) {
        new AlertDialog.Builder(this)
                .setMessage(error)
                .create()
                .show();
    }

    public void showToast(String toast) {
        XToast.showLong(toast);
    }
}