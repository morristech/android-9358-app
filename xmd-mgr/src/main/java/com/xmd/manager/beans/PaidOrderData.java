package com.xmd.manager.beans;
/**
 * Created by heyangya on 17-5-11.
 * 付费预约 返回前端的数据
 */
public class PaidOrderData {
    private String id;
    private String orderNo;
    private String customerName;
    private String userId;
    private String phoneNum;
    private String appointTime;
    private String createdAt;
    private String techName;
    private String technicianId;
    private Integer downPayment;
    private String refundStatus;
    private String status;
    private String orderType;
    private Boolean isExpire;
    private String statusName;
    private String innerProvider;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(String appointTime) {
        this.appointTime = appointTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public Integer getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Integer downPayment) {
        this.downPayment = downPayment;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Boolean getExpire() {
        return isExpire;
    }

    public void setExpire(Boolean expire) {
        isExpire = expire;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getInnerProvider() {
        return innerProvider;
    }

    public void setInnerProvider(String innerProvider) {
        this.innerProvider = innerProvider;
    }

    @Override
    public String toString() {
        return "PaidOrderData{" +
                "id='" + id + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", userId='" + userId + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", appointTime='" + appointTime + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", techName='" + techName + '\'' +
                ", technicianId='" + technicianId + '\'' +
                ", downPayment=" + downPayment +
                ", refundStatus='" + refundStatus + '\'' +
                ", status='" + status + '\'' +
                ", orderType='" + orderType + '\'' +
                ", isExpire=" + isExpire +
                ", statusName='" + statusName + '\'' +
                ", innerProvider='" + innerProvider + '\'' +
                '}';
    }
}
