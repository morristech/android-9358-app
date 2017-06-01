package com.xmd.appointment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.BaseActivity;
import com.xmd.app.Constants;
import com.xmd.app.net.NetworkSubscriber;
import com.xmd.appointment.beans.AppointmentSetting;
import com.xmd.appointment.beans.AppointmentSettingResult;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.Technician;
import com.xmd.appointment.databinding.ActivityAppointmentBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class AppointmentActivity extends BaseActivity
        implements TechSelectFragment.Listener,
        ServiceItemSelectFragment.Listener,
        TimeSelectFragment.Listener {

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
        String techId = null;
        String itemId = null;
        if (mData.getTechnician() != null) {
            techId = mData.getTechnician().getId();
        }
        if (mData.getServiceItem() != null) {
            itemId = mData.getServiceItem().getId();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("TechSelectFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        TechSelectFragment fragment = TechSelectFragment.newInstance(techId, itemId);
        fragment.show(ft, "TechSelectFragment");
    }

    public void onClickSelectService() {
        if (mData.getTechnician() != null && mData.getAppointmentSetting() == null) {
            //未加载到技师项目数据，这里需要再次加载
            showLoading();
            DataManager.getInstance().loadAppointmentExt(mData.getTechnician().getId(), null, new NetworkSubscriber<AppointmentSettingResult>() {
                @Override
                public void onCallbackSuccess(AppointmentSettingResult result) {
                    hideLoading();
                    XLogger.i("appointment ext:" + result.getRespData());
                    mData.setAppointmentSetting(result.getRespData());
                    gotoServiceSelect();
                }

                @Override
                public void onCallbackError(Throwable e) {
                    hideLoading();
                    showError("加载技师项目信息失败:" + e.getLocalizedMessage());
                }
            });
        } else {
            gotoServiceSelect();
        }
    }

    private void gotoServiceSelect() {
        String selectedItemId = null;
        ArrayList<String> showItemIdList = null;
        if (mData.getServiceItem() != null) {
            selectedItemId = mData.getServiceItem().getId();
        }
        if (mData.getAppointmentSetting() != null) {
            showItemIdList = (ArrayList<String>) mData.getAppointmentSetting().getTechItemIds();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("ServiceItemSelectFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        ServiceItemSelectFragment fragment = ServiceItemSelectFragment.newInstance(selectedItemId, showItemIdList);
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dialog);
        fragment.show(ft, "ServiceItemSelectFragment");
    }

    public void onClickSelectTime() {
        if (mData.getAppointmentSetting() == null) {
            showLoading();
            DataManager.getInstance().loadAppointmentExt(mData.getTechnician() == null ? null : mData.getTechnician().getId(),
                    null, new NetworkSubscriber<AppointmentSettingResult>() {
                        @Override
                        public void onCallbackSuccess(AppointmentSettingResult result) {
                            hideLoading();
                            XLogger.i("appointment ext:" + result.getRespData());
                            mData.setAppointmentSetting(result.getRespData());
                            gotoTimeSelect();
                        }

                        @Override
                        public void onCallbackError(Throwable e) {
                            hideLoading();
                            showError("加载预约配置信息失败:" + e.getLocalizedMessage());
                        }
                    });
        } else {
            gotoTimeSelect();
        }
    }

    private void gotoTimeSelect() {
        if (mData.getAppointmentSetting() == null) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("TimeSelectFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        TimeSelectFragment fragment = TimeSelectFragment.newInstance(mData.getTimeSection(), mData.getAppointmentSetting());
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dialog);
        fragment.show(ft, "TimeSelectFragment");
    }

    public void onClickSubmit() {

    }

    @Override
    public void onSelectTechnician(Technician technician) {
        if (mData.getTechnician() == null || !mData.getTechnician().getId().equals(technician.getId())) {
            //获取项目和时间信息
            mData.setAppointmentSetting(null);
            DataManager.getInstance().loadAppointmentExt(technician.getId(), null, new NetworkSubscriber<AppointmentSettingResult>() {
                @Override
                public void onCallbackSuccess(AppointmentSettingResult result) {
                    mData.setAppointmentSetting(result.getRespData());
                }

                @Override
                public void onCallbackError(Throwable e) {

                }
            });
        }
        mData.setTechnician(technician);
        mBinding.setData(mData);
    }

    @Override
    public void onCleanTechnician() {
        mData.setTechnician(null);
        mData.setAppointmentSetting(null);
        mBinding.setData(mData);
    }

    @Override
    public void onSelectServiceItem(ServiceItem serviceItem) {
        mData.setServiceItem(serviceItem);
        mBinding.setData(mData);
    }

    @Override
    public void onCleanServiceItem() {
        mData.setServiceItem(null);
        mBinding.setData(mData);
    }

    @Override
    public void onSelectTime(AppointmentSetting.TimeSection timeSection) {

    }
}
