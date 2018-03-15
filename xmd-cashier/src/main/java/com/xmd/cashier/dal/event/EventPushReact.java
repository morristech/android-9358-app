package com.xmd.cashier.dal.event;

/**
 * Created by zr on 18-3-14.
 */

public class EventPushReact {
    private String businessType;

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public EventPushReact(String businessType) {
        this.businessType = businessType;
    }
}
