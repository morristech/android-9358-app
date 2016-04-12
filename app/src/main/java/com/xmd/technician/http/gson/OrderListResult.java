package com.xmd.technician.http.gson;

import com.xmd.technician.beans.Order;

import java.util.List;

/**
 * Created by sdcm on 16-3-15.
 */
public class OrderListResult extends BaseResult {
    public List<Order> respData;
    public int pageCount;
}
