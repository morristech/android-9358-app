package com.xmd.manager.event;

/**
 * Created by Lhj on 17-9-19.
 */

public class DateChangedEvent  {

    public String startTime;
    public String endTime;

    public DateChangedEvent(String startTime,String endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
