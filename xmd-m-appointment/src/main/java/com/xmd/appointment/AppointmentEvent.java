package com.xmd.appointment;

import com.xmd.app.event.EventCommon;

/**
 * Created by heyangya on 17-5-23.
 */

public class AppointmentEvent extends EventCommon<AppointmentData> {
    public static final int CMD_SHOW = 1;
    public static final int CMD_HIDE = 2;


    public AppointmentEvent(int cmd, AppointmentData data) {
        super(cmd, data);
    }
}
