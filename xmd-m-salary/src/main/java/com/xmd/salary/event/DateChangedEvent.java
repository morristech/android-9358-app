package com.xmd.salary.event;

/**
 * Created by Lhj on 17-11-21.
 */

public class DateChangedEvent {
    public String selectedType;
    public String startTime;
    public String endTime;

    public DateChangedEvent(String selectedType, String startTime, String endTime) {
        this.selectedType = selectedType;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
