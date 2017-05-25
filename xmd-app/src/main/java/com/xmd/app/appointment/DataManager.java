package com.xmd.app.appointment;

import com.xmd.app.appointment.beans.TechnicianListResult;
import com.xmd.app.net.NetworkEngine;
import com.xmd.app.net.NetworkSubscriber;
import com.xmd.app.net.RetrofitFactory;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by heyangya on 17-5-24.
 */

class DataManager {
    private static final DataManager ourInstance = new DataManager();

    static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
        EventBus.getDefault().register(this);
    }

    public void getTechnicianList(NetworkSubscriber<TechnicianListResult> listener) {
        NetworkEngine.doRequest(RetrofitFactory.getService(NetService.class).getTechnicianList(null, null, null, null), listener);
    }
}
