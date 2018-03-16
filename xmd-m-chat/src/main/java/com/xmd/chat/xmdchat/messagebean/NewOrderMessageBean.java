package com.xmd.chat.xmdchat.messagebean;

/**
 * Created by Lhj on 18-3-15.
 */

public class NewOrderMessageBean {

    private String appointTime;
    private String orderId;

    public NewOrderMessageBean(String appointTime, String orderId, String serviceItemName, String orderStatus) {
        this.appointTime = appointTime;
        this.orderId = orderId;
        this.serviceItemName = serviceItemName;
        this.orderStatus = orderStatus;
    }

    private String serviceItemName;
    private String orderStatus;

    public String getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(String appointTime) {
        this.appointTime = appointTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getServiceItemName() {
        return serviceItemName;
    }

    public void setServiceItemName(String serviceItemName) {
        this.serviceItemName = serviceItemName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
