package com.xmd.app.event;

/**
 * Created by heyangya on 17-5-24.
 */

public class EventCommon<T> {
    private String tag;
    private int cmd;
    private T data;

    public EventCommon(int cmd, T data) {
        this.cmd = cmd;
        this.data = data;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "EventCommon{" +
                "tag=" + tag +
                ", cmd=" + cmd +
                ", data=" + data +
                '}';
    }
}
