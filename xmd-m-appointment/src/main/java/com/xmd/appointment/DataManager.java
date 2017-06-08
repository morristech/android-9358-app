package com.xmd.appointment;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.net.BaseBean;
import com.xmd.app.net.NetworkEngine;
import com.xmd.app.net.NetworkSubscriber;
import com.xmd.app.net.RetrofitFactory;
import com.xmd.appointment.beans.AppointmentSettingResult;
import com.xmd.appointment.beans.ServiceListResult;
import com.xmd.appointment.beans.TechnicianListResult;

import rx.Subscription;

/**
 * Created by heyangya on 17-5-24.
 */

class DataManager {
    private static final DataManager ourInstance = new DataManager();

    static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    private Subscription mLoadTechnicianList;
    private Subscription mLoadServiceList;
    private Subscription mLoadAppointmentExt;

    //加载技师列表
    public void loadTechnicianList(String serviceItemId, final NetworkSubscriber<TechnicianListResult> listener) {
        cancelLoadTechnicianList();
        mLoadTechnicianList = NetworkEngine.doRequest(
                RetrofitFactory.getService(NetService.class).getTechnicianList(1, Integer.MAX_VALUE, serviceItemId, null, null, null), listener);
    }

    public void cancelLoadTechnicianList() {
        if (mLoadTechnicianList != null) {
            mLoadTechnicianList.unsubscribe();
            mLoadTechnicianList = null;
        }
    }


    //加载服务列表
    public void loadServiceList(final NetworkSubscriber<ServiceListResult> listener) {
        cancelLoadServiceList();
        mLoadServiceList = NetworkEngine.doRequest(
                RetrofitFactory.getService(NetService.class).getServiceList(), listener);
    }

    public void cancelLoadServiceList() {
        if (mLoadServiceList != null) {
            mLoadServiceList.unsubscribe();
            mLoadServiceList = null;
        }
    }

    //加载额外预约信息，包括技师预约时间，技师项目信息
    public void loadAppointmentExt(String techId, String userId, final NetworkSubscriber<AppointmentSettingResult> listener) {
        cancelLoadAppointmentExt();
        mLoadAppointmentExt = NetworkEngine.doRequest(
                RetrofitFactory.getService(NetService.class).getAppointmentExt(techId, userId), listener);
    }

    public void cancelLoadAppointmentExt() {
        if (mLoadAppointmentExt != null) {
            mLoadAppointmentExt.unsubscribe();
            mLoadAppointmentExt = null;
        }
    }


    //创建订单
    public void submitAppointment(AppointmentData data, NetworkSubscriber<BaseBean> listener) {
        int dateId = (int) ((data.getTime().getTime() - data.getAppointmentSetting().getNowTime()) / DateUtils.DAY_TIME_MS);
        NetworkEngine.doRequest(
                RetrofitFactory.getService(NetService.class).submitAppointment(
                        data.getCustomerName(),
                        data.getCustomerPhone(),
                        dateId,
                        DateUtils.getSdf("HH:mm").format(data.getTime()),
                        data.getTechnician() == null ? null : data.getTechnician().getId(),
                        data.getCustomerId(),
                        data.getServiceItem() == null ? null : data.getServiceItem().getId(),
                        data.getDuration()), listener);
    }
}
