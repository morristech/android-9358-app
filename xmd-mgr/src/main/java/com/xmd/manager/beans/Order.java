package com.xmd.manager.beans;

import java.io.Serializable;

/**
 * Created by sdcm on 15-11-23.
 */
public class Order implements Serializable {

    public String id;

    public String headImgUrl;

    /**
     * 环信聊天id
     */
    public String emchatId;
    public String formatCreateTime;
    public String formatAppointTime;

    public String customerName;
    public String phoneNum;
    public String createdAt;

    public String techName;
    public String status;
    public String statusName;
    public String itemName;
    public String appointTime;

    public String orderType;
    public String itemId;
    public String commented;
    public String cardNo;

    public String orderNo;

    public String gender;
    public String serviceTime;
    public String serviceItemId;
    public int downPayment;

    public String remainTime;

    public String closedAt;

    public String techSerialNo;

    public boolean isExpire;
    public String payDate;
    public int payType;

    public Order(String avatarUrl, String customerName, String telephone, String appointTime, String createdAt, String techName, String status, String orderType, int downPayment) {
        this.headImgUrl = avatarUrl;
        this.customerName = customerName;
        this.phoneNum = telephone;
        this.appointTime = appointTime;
        this.createdAt = createdAt;
        this.techName = techName;
        this.status = status;
        this.orderType = orderType;
        this.downPayment = downPayment;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", emchatId='" + emchatId + '\'' +
                ", formatCreateTime='" + formatCreateTime + '\'' +
                ", formatAppointTime='" + formatAppointTime + '\'' +
                ", customerName='" + customerName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", techName='" + techName + '\'' +
                ", status='" + status + '\'' +
                ", statusName='" + statusName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", appointTime='" + appointTime + '\'' +
                ", orderType='" + orderType + '\'' +
                ", itemId='" + itemId + '\'' +
                ", commented='" + commented + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", gender='" + gender + '\'' +
                ", serviceTime='" + serviceTime + '\'' +
                ", serviceItemId='" + serviceItemId + '\'' +
                ", downPayment=" + downPayment +
                ", remainTime='" + remainTime + '\'' +
                ", closedAt='" + closedAt + '\'' +
                ", techSerialNo='" + techSerialNo + '\'' +
                ", isExpire=" + isExpire +
                '}';
    }
}
