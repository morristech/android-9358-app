package com.xmd.manager.service.response;

import com.xmd.manager.beans.Order;

import java.util.List;

/**
 * Created by sdcm on 15-11-24.
 */
public class OrderListResult extends BaseResult {
    public List<Order> respData;
    public String type;
    public String startTime;
    public String endTime;
}
