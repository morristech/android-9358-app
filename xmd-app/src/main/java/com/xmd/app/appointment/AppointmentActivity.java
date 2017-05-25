package com.xmd.app.appointment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.xmd.app.BaseActivity;
import com.xmd.app.Constants;
import com.xmd.app.R;
import com.xmd.app.databinding.ActivityAppointmentBinding;

import org.greenrobot.eventbus.EventBus;

public class AppointmentActivity extends BaseActivity {

    private ActivityAppointmentBinding mBinding;
    private AppointmentData mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_appointment);

        mData = (AppointmentData) getIntent().getSerializableExtra(Constants.EXTRA_DATA);
        mBinding.setData(mData);
        mBinding.setHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_HIDE, mData));
    }

    public void onClickSelectTech() {

    }

    public void onClickSelectService() {

    }

    public void onClickSelectTime() {

    }

    public void onClickSubmit() {

    }
}
