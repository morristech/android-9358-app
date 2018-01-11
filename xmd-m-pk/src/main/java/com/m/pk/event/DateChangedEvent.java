package com.m.pk.event;

/**
 * Created by Lhj on 18-1-9.
 */

public class DateChangedEvent {
    public String startDate;
    public String endDate;

    public DateChangedEvent(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
