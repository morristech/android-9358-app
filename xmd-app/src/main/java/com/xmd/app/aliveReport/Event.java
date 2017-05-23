package com.xmd.app.aliveReport;

/**
 * Created by heyangya on 17-5-23.
 * 响应事件
 */

public class Event {
    public static final int CMD_START = 1; //开始事件,必传token
    public static final int CMD_STOP = 2; //停止事件

    private int cmd;
    private String token;

    public Event(int cmd, String token) {
        this.cmd = cmd;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public int getCmd() {
        return cmd;
    }

    @Override
    public String toString() {
        return "Event{" +
                "cmd=" + cmd +
                ", token='" + token + '\'' +
                '}';
    }
}
