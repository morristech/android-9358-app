package com.xmd.technician.window;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

import com.xmd.technician.R;
import com.xmd.technician.contract.CompleteRegisterInfoContract;
import com.xmd.technician.databinding.ActivityCompleteRegisterInfoBinding;
import com.xmd.technician.presenter.CompleteRegisterInfoPresenter;

public class CompleteRegisterInfoActivity extends BaseActivity implements CompleteRegisterInfoContract.View {
    private CompleteRegisterInfoPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompleteRegisterInfoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_complete_register_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0xffff936f);
        }

        mPresenter = new CompleteRegisterInfoPresenter(this, this, binding);
        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mPresenter.onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
