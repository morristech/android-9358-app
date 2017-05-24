package com.xmd.app.appointment;

/**
 * Created by heyangya on 17-5-23.
 */

public class Event {
    public static final int CMD_SHOW = 1;
    public static final int CMD_HIDE = 2;

    private int cmd;
    private AppointmentData data;

    public Event(int cmd, AppointmentData data) {
        this.cmd = cmd;
        this.data = data;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public AppointmentData getData() {
        return data;
    }

    public void setData(AppointmentData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "cmd=" + cmd +
                ", data=" + data +
                '}';
    }
}
