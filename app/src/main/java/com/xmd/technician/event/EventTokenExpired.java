package com.xmd.technician.event;

/**
 * Created by sdcm on 16-1-12.
 */
public class EventTokenExpired {

    public String expiredReason;

    public EventTokenExpired(String expiredReason) {
        this.expiredReason = expiredReason;
    }
}
