package com.xmd.appointment;

import com.xmd.app.event.EventCommon;

/**
 * Created by heyangya on 17-5-23.
 */

public class AppointmentEvent extends EventCommon<AppointmentData> {
    public static final int CMD_SHOW = 1; //显示预约界面
    public static final int CMD_HIDE = 2; //预约界面隐藏
    public static final int CMD_SUBMIT = 3; //提交预约
    public static final int CMD_SUBMIT_RESULT = 4; //提交预约结果

    public AppointmentEvent(int cmd, AppointmentData data) {
        super(cmd, data);
    }

    public AppointmentEvent(int cmd, String tag, AppointmentData data) {
        super(cmd, data);
        setTag(tag);
    }
}
