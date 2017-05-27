package com.xmd.appointment;

import com.xmd.app.net.NetworkEngine;
import com.xmd.app.net.NetworkSubscriber;
import com.xmd.app.net.RetrofitFactory;
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

    //加载技师列表
    public void loadTechnicianList(final NetworkSubscriber<TechnicianListResult> listener) {
        mLoadTechnicianList = NetworkEngine.doRequest(
                RetrofitFactory.getService(NetService.class).getTechnicianList(1, Integer.MAX_VALUE, null, null, null, null), listener);
    }

    public void cancelLoadTechnicianList() {
        if (mLoadTechnicianList != null) {
            mLoadTechnicianList.unsubscribe();
            mLoadTechnicianList = null;
        }
    }


    //加载服务列表
    public void loadServiceList(final NetworkSubscriber<ServiceListResult> listener) {
        mLoadServiceList = NetworkEngine.doRequest(
                RetrofitFactory.getService(NetService.class).getServiceList(), listener);
    }

    public void cancelLoadServiceList() {
        if (mLoadServiceList != null) {
            mLoadServiceList.unsubscribe();
            mLoadServiceList = null;
        }
    }
}
