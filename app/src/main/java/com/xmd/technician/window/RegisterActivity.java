package com.xmd.technician.window;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;

import com.xmd.technician.R;
import com.xmd.technician.contract.RegisterContract;
import com.xmd.technician.databinding.ActivityRegisterBinding;
import com.xmd.technician.presenter.RegisterPresenter;

public class RegisterActivity extends BaseActivity implements RegisterContract.View {
    private RegisterPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0xffff936f);
        }

        mPresenter = new RegisterPresenter(this, this, binding);

        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void finishSelf() {
        finish();
    }
}
