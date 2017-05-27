package com.xmd.appointment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xmd.app.BaseActivity;
import com.xmd.app.Constants;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.Technician;
import com.xmd.appointment.databinding.ActivityAppointmentBinding;

import org.greenrobot.eventbus.EventBus;

public class AppointmentActivity extends BaseActivity implements TechSelectFragment.Listener, ServiceItemSelectFragment.Listener {

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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("TechSelectFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        TechSelectFragment fragment = TechSelectFragment.newInstance(null);
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
        fragment.show(ft, "TechSelectFragment");
    }

    public void onClickSelectService() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("ServiceItemSelectFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        ServiceItemSelectFragment fragment = ServiceItemSelectFragment.newInstance(null);
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
        fragment.show(ft, "ServiceItemSelectFragment");
    }

    public void onClickSelectTime() {

    }

    public void onClickSubmit() {

    }

    @Override
    public void onSelectTechnician(Technician technician) {
        mData.setTechnician(technician);
        mBinding.setData(mData);
    }

    @Override
    public void onCleanTechnician() {
        mData.setTechnician(null);
        mBinding.setData(mData);
    }

    @Override
    public void onSelectServiceItem(ServiceItem serviceItem) {

    }

    @Override
    public void onCleanServiceItem() {

    }
}
