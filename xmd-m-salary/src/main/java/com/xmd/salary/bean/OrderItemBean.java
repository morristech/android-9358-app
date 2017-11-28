package com.xmd.salary.bean;

/**
 * Created by Lhj on 17-11-24.
 */

public class OrderItemBean {

    public String orderTypeName;
    public int orderCommission;

    public OrderItemBean(String orderTypeName, int orderCommission) {
        this.orderTypeName = orderTypeName;
        this.orderCommission = orderCommission;
    }

}
