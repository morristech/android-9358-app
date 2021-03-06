package com.xmd.appointment;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.view.KeyEvent;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.Constants;
import com.xmd.appointment.beans.AppointmentSetting;
import com.xmd.appointment.beans.AppointmentSettingResult;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.Technician;
import com.xmd.appointment.databinding.ActivityAppointmentBinding;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentActivity extends BaseActivity
        implements TechSelectFragment.Listener,
        ServiceItemSelectFragment.Listener,
        TimeSelectFragment.Listener {

    private ActivityAppointmentBinding mBinding;
    private AppointmentData mData;
    private ServiceItemDuration mSelectedDuration;

    private String eventTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_appointment);

        eventTag = getIntent().getStringExtra(Constants.EXTRA_EVENT_TAG);
        mData = (AppointmentData) getIntent().getSerializableExtra(Constants.EXTRA_DATA);

        mBinding.setData(mData);
        mBinding.setHandler(this);

        setTitle("完善预约");
        initToolbar();
        setBackVisible(true);

        //初始化服务时长选择列表
        mBinding.durationRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        CommonRecyclerViewAdapter<ServiceItemDuration> durationAdapter = new CommonRecyclerViewAdapter<>();
        List<ServiceItemDuration> durations = new ArrayList<>();
        durations.add(new ServiceItemDuration(45));
        durations.add(new ServiceItemDuration(60));
        durations.add(new ServiceItemDuration(90));
        durations.add(new ServiceItemDuration(120));
        durationAdapter.setData(R.layout.list_item_service_duration, com.xmd.appointment.BR.data, durations);
        durationAdapter.setHandler(com.xmd.appointment.BR.handler, this);
        mBinding.durationRecyclerView.setAdapter(durationAdapter);

        if (mData.getDuration() == 0) {
            mSelectedDuration = durations.get(1);
            mSelectedDuration.viewSelected.set(true);
            mData.setDuration(mSelectedDuration.duration);
        } else {
            for (ServiceItemDuration duration : durations) {
                if (duration.getDuration() == mData.getDuration()) {
                    mSelectedDuration = duration;
                }
            }
            if (mSelectedDuration == null) {
                mSelectedDuration = durations.get(0);
            }
            mSelectedDuration.viewSelected.set(true);
        }

        //加载预约数据
        loadSetting();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }

    private void loadSetting() {
        showLoading();
        DataManager.getInstance().loadAppointmentExt(mData.getTechnician() == null ? null : mData.getTechnician().getId(),
                mData.getCustomerId(), new NetworkSubscriber<AppointmentSettingResult>() {
                    @Override
                    public void onCallbackSuccess(AppointmentSettingResult result) {
                        hideLoading();
                        if (AppointmentSetting.APPOINT_TYPE_CALL.equals(result.getRespData().getAppointType())) {
                            XToast.show("抱歉，会所已开通电话预约，该功能暂时无法使用。");
                            EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_HIDE, eventTag, null));
                            finish();
                            return;
                        }
                        mData.setAppointmentSetting(result.getRespData());
                        //检查客户预约时间是否有效
                        if (mData.getAppointmentTime() != null && !AppointmentData.isTimeSuitable(mData.getAppointmentTime(), mData.getAppointmentSetting())) {
                            mData.setAppointmentTime(null);
                        }
                        mBinding.setData(mData);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        hideLoading();
                        showDialog("加载配置失败：" + e.getLocalizedMessage());
                    }
                });
    }

    /************************
     * 技师选择
     ***********************/
    //点击选择技师
    public void onClickSelectTech() {
        if (mData.isFixTechnician()) {
            return;
        }
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
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dialog_Alert);
        fragment.show(ft, "TechSelectFragment");
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
                    if (!AppointmentData.isTimeSuitable(mData.getAppointmentTime(), mData.getAppointmentSetting())) {
                        mData.setAppointmentTime(null);
                    }
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


    /************************
     * 服务选择
     ***********************/
    //点击选择服务
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
                    showDialog("加载技师项目信息失败:" + e.getLocalizedMessage());
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
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dialog_Alert);
        fragment.show(ft, "ServiceItemSelectFragment");
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


    /**************************
     * 时间选择
     ******************************/
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
                            showDialog("加载预约配置信息失败:" + e.getLocalizedMessage());
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
        TimeSelectFragment fragment = TimeSelectFragment.newInstance(mData.getAppointmentTime(), mData.getAppointmentSetting());
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dialog_Alert);
        fragment.show(ft, "TimeSelectFragment");
    }

    @Override
    public void onSelectTime(Date time) {
        mData.setAppointmentTime(time);
        mBinding.setData(mData);
    }

    public void onClickServiceDuration(ServiceItemDuration duration) {
        if (mSelectedDuration != duration) {
            mSelectedDuration.viewSelected.set(false);
            mSelectedDuration = duration;
            mSelectedDuration.viewSelected.set(true);
            mData.setDuration(mSelectedDuration.getDuration());
        }
    }

    /***************
     * 联系人信息
     *******************/

    public void onCustomerNameChange(Editable e) {
        mData.setCustomerName(e.toString());
    }

    public void onCustomerPhoneChange(Editable e) {
        mData.setCustomerPhone(e.toString());
    }


    /***************
     * 提交
     *******************/
    public void onClickSubmit() {
        if (mData.isNeedSubmit()) {
            showLoading();
            DataManager.getInstance().submitAppointment(mData, new NetworkSubscriber<BaseBean>() {
                @Override
                public void onCallbackSuccess(BaseBean result) {
                    hideLoading();
                    XToast.show("创建预约成功！");
                    EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_HIDE, eventTag, mData));
                    finish();
                }

                @Override
                public void onCallbackError(Throwable e) {
                    hideLoading();
                    showDialog("创建预约失败：" + e.getLocalizedMessage());
                }
            });
        } else {
            EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_HIDE, eventTag, mData));
            finish();
        }
        EventBus.getDefault().post(new AppointmentUmengStatisticsEvent(Constants.UMENG_STATISTICS_BOOK_SEND));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("确定退出？")
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_HIDE, eventTag, null));
                        finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class ServiceItemDuration {
        private int duration;
        private String name;
        public ObservableBoolean viewSelected;

        public ServiceItemDuration(int duration) {
            viewSelected = new ObservableBoolean();
            this.duration = duration;
            if (duration < 60) {
                name = duration + "分钟";
            } else {
                if (duration % 60 == 0) {
                    name = duration / 60 + "小时";
                } else {
                    name = String.format(Locale.getDefault(), "%.1f小时", duration / 60.0);
                }
            }
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
