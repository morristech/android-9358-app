package com.xmd.chat.xmdchat.messagebean;

/**
 * Created by Lhj on 18-1-26.
 */

public class OrderMessageBean {

    private String orderTechId;
    private String orderTechName;
    private String orderTechAvatar;
    private String orderServiceId;
    private String orderServiceName;
    private Long orderServiceTime;
    private Integer orderServiceDuration;
    private String orderCustomerId;
    private String orderCustomerName;
    private String orderCustomerPhone;

    public String getOrderCustomerId() {
        return orderCustomerId;
    }

    public void setOrderCustomerId(String orderCustomerId) {
        this.orderCustomerId = orderCustomerId;
    }

    public String getOrderCustomerName() {
        return orderCustomerName;
    }

    public void setOrderCustomerName(String orderCustomerName) {
        this.orderCustomerName = orderCustomerName;
    }

    public String getOrderCustomerPhone() {
        return orderCustomerPhone;
    }

    public void setOrderCustomerPhone(String orderCustomerPhone) {
        this.orderCustomerPhone = orderCustomerPhone;
    }




    public String getOrderTechId() {
        return orderTechId;
    }

    public void setOrderTechId(String orderTechId) {
        this.orderTechId = orderTechId;
    }

    public String getOrderTechName() {
        return orderTechName;
    }

    public void setOrderTechName(String orderTechName) {
        this.orderTechName = orderTechName;
    }

    public String getOrderTechAvatar() {
        return orderTechAvatar;
    }

    public void setOrderTechAvatar(String orderTechAvatar) {
        this.orderTechAvatar = orderTechAvatar;
    }

    public String getOrderServiceId() {
        return orderServiceId;
    }

    public void setOrderServiceId(String orderServiceId) {
        this.orderServiceId = orderServiceId;
    }

    public String getOrderServiceName() {
        return orderServiceName;
    }

    public void setOrderServiceName(String orderServiceName) {
        this.orderServiceName = orderServiceName;
    }

    public Long getOrderServiceTime() {
        return orderServiceTime;
    }

    public void setOrderServiceTime(Long orderServiceTime) {
        this.orderServiceTime = orderServiceTime;
    }

    public Integer getOrderServiceDuration() {
        return orderServiceDuration;
    }

    public void setOrderServiceDuration(Integer orderServiceDuration) {
        this.orderServiceDuration = orderServiceDuration;
    }

}
