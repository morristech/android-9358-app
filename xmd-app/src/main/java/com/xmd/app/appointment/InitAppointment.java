package com.xmd.app.appointment;

import android.content.Context;
import android.content.Intent;

import com.xmd.app.Constants;
import com.xmd.app.IInit;
import com.xmd.app.Init;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by heyangya on 17-5-23.
 * 初始化预约功能
 */

public class InitAppointment implements IInit {
    @Override
    public void init(Context context) {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getCmd()) {
            case Event.CMD_SHOW:
                Intent intent = new Intent(Init.getContext(), AppointmentActivity.class);
                intent.putExtra(Constants.EXTRA_DATA, event.getData());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Init.getContext().startActivity(intent);
                break;
        }
    }
}
