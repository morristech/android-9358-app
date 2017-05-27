package com.xmd.appointment;

import android.content.Context;
import android.content.Intent;

import com.xmd.app.Constants;
import com.xmd.app.IFunctionInit;
import com.xmd.app.XmdApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by heyangya on 17-5-23.
 * 初始化预约功能
 */

public class XmdModuleAppointment implements IFunctionInit {
    private static final XmdModuleAppointment ourInstance = new XmdModuleAppointment();

    public static XmdModuleAppointment getInstance() {
        return ourInstance;
    }

    private XmdModuleAppointment() {

    }

    @Override
    public void init(Context context) {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(AppointmentEvent event) {
        switch (event.getCmd()) {
            case AppointmentEvent.CMD_SHOW:
                Intent intent = new Intent(XmdApp.getInstance().getContext(), AppointmentActivity.class);
                intent.putExtra(Constants.EXTRA_DATA, event.getData());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                XmdApp.getInstance().getContext().startActivity(intent);
                break;
        }
    }
}
