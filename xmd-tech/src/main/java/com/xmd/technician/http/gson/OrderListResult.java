package com.xmd.technician.http.gson;

import com.xmd.technician.bean.Order;

import java.util.List;

/**
 * Created by sdcm on 16-3-15.
 */
public class OrderListResult extends BaseResult {
    public String isIndexPage;
    public List<Order> respData;
}
