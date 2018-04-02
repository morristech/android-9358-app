package com.xmd.cashier.dal.event;

/**
 * Created by zr on 18-4-2.
 */

public class UploadDoneEvent {
    private String desc;

    public UploadDoneEvent(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
