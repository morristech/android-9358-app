package com.xmd.technician.onlinepaynotify.event;

import com.xmd.technician.onlinepaynotify.model.PayNotifyInfo;

/**
 * Created by heyangya on 17-1-20.
 */

public class PayNotifyArchiveEvent {
    public PayNotifyInfo info;

    public PayNotifyArchiveEvent(PayNotifyInfo info) {
        this.info = info;
    }
}
