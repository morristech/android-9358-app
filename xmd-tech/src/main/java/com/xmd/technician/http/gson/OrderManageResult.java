package com.xmd.technician.http.gson;

/**
 * Created by sdcm on 16-4-11.
 */
public class OrderManageResult {
    public String orderId;
    public String content;

    public OrderManageResult(String id, String content) {
        this.orderId = id;
        this.content = content;
    }

    public OrderManageResult(String id) {
        this.orderId = id;
    }
}
