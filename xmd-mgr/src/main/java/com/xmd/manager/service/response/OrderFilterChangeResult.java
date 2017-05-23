package com.xmd.manager.service.response;

/**
 * Created by sdcm on 16-1-12.
 */
public class OrderFilterChangeResult {

    public String filterText;
    public String startTime;
    public String endTime;

    public OrderFilterChangeResult(String filterText, String startTime, String endTime) {
        this.filterText = filterText;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
