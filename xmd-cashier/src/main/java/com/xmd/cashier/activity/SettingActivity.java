package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.sp.SPManager;

/**
 * Created by zr on 17-5-24.
 */

public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private Switch mOrderAcceptSwitch;
    private Switch mOrderRejectSwitch;
    private Switch mOnlinePassSwitch;
    private Switch mOnlineUnpassSwitch;
    private Switch mPrintClientSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "系统设置");
        mOnlinePassSwitch = (Switch) findViewById(R.id.sw_online_pass);
        mOnlineUnpassSwitch = (Switch) findViewById(R.id.sw_online_unpass);
        mOrderAcceptSwitch = (Switch) findViewById(R.id.sw_order_accept);
        mOrderRejectSwitch = (Switch) findViewById(R.id.sw_order_reject);
        mPrintClientSwitch = (Switch) findViewById(R.id.sw_print_client);

        mOnlinePassSwitch.setChecked(SPManager.getInstance().getOnlinePassSwitch());
        mOnlineUnpassSwitch.setChecked(SPManager.getInstance().getOnlineUnpassSwitch());
        mOrderAcceptSwitch.setChecked(SPManager.getInstance().getOrderAcceptSwitch());
        mOrderRejectSwitch.setChecked(SPManager.getInstance().getOrderRejectSwitch());
        mPrintClientSwitch.setChecked(SPManager.getInstance().getPrintClientSwitch());

        mOnlinePassSwitch.setOnCheckedChangeListener(this);
        mOnlineUnpassSwitch.setOnCheckedChangeListener(this);
        mOrderAcceptSwitch.setOnCheckedChangeListener(this);
        mOrderRejectSwitch.setOnCheckedChangeListener(this);
        mPrintClientSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_online_pass:
                SPManager.getInstance().setOnlinePassSwitch(isChecked);
                break;
            case R.id.sw_online_unpass:
                SPManager.getInstance().setOnlineUnpassSwitch(isChecked);
                break;
            case R.id.sw_order_accept:
                SPManager.getInstance().setOrderAcceptSwitch(isChecked);
                break;
            case R.id.sw_order_reject:
                SPManager.getInstance().setOrderRejectSwitch(isChecked);
                break;
            case R.id.sw_print_client:
                showToast(isChecked ? "打印客户联" : "不打印客户联");
                SPManager.getInstance().setPrintClientSwitch(isChecked);
                break;
            default:
                break;
        }
    }
}
