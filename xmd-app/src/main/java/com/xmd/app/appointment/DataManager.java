package com.xmd.app.appointment;

import com.xmd.app.appointment.beans.TechnicianRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    @Subscribe
    public void getTechnicianList(TechnicianRequest event) {

    }
}
