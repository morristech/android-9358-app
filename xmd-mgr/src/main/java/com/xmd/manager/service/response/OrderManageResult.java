package com.xmd.manager.service.response;

/**
 * Created by sdcm on 16-1-12.
 */
public class OrderManageResult {

    public OrderManageResult(boolean isSuccessful, String msg) {
        this.isSuccessful = isSuccessful;
        this.msg = msg;
    }

    public boolean isSuccessful;
    public String msg;
    public String orderId;
}
