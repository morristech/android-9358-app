package com.xmd.manager.beans;


import java.io.Serializable;
import java.util.List;

/**
 * Created by heyangya on 17-5-11.
 * 优惠券 返回前端的数据
 */
public class UserActData implements Serializable {

    private String actId;
    private String couponNo;
    private String useTypeName;
    private String useType;
    private String actTitle;
    private Integer consumeAmount;
    private Integer creditAmount;
    private Integer actAmount;
    private String paidType;
    private String actSubTitle;
    private String couponTypeName;
    private String couponType;
    private String couponPeriod;
    private String actStatusName;
    private String actStatus;
    private String suaId;
    private String consumeMoneyDescription;
    private String getDate;
    private String useTimePeriod;
    private String endUseDate;
    private String startDate;
    private String endDate;
    private String actContent;
    private List<String> itemNames;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public String getUseTypeName() {
        return useTypeName;
    }

    public void setUseTypeName(String useTypeName) {
        this.useTypeName = useTypeName;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public String getActTitle() {
        return actTitle;
    }

    public void setActTitle(String actTitle) {
        this.actTitle = actTitle;
    }

    public Integer getConsumeAmount() {
        return consumeAmount;
    }

    public void setConsumeAmount(Integer consumeAmount) {
        this.consumeAmount = consumeAmount;
    }

    public Integer getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Integer creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Integer getActAmount() {
        return actAmount;
    }

    public void setActAmount(Integer actAmount) {
        this.actAmount = actAmount;
    }

    public String getPaidType() {
        return paidType;
    }

    public void setPaidType(String paidType) {
        this.paidType = paidType;
    }

    public String getActSubTitle() {
        return actSubTitle;
    }

    public void setActSubTitle(String actSubTitle) {
        this.actSubTitle = actSubTitle;
    }

    public String getCouponTypeName() {
        return couponTypeName;
    }

    public void setCouponTypeName(String couponTypeName) {
        this.couponTypeName = couponTypeName;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getCouponPeriod() {
        return couponPeriod;
    }

    public void setCouponPeriod(String couponPeriod) {
        this.couponPeriod = couponPeriod;
    }

    public String getActStatusName() {
        return actStatusName;
    }

    public void setActStatusName(String actStatusName) {
        this.actStatusName = actStatusName;
    }

    public String getActStatus() {
        return actStatus;
    }

    public void setActStatus(String actStatus) {
        this.actStatus = actStatus;
    }

    public String getSuaId() {
        return suaId;
    }

    public void setSuaId(String suaId) {
        this.suaId = suaId;
    }

    public String getConsumeMoneyDescription() {
        return consumeMoneyDescription;
    }

    public void setConsumeMoneyDescription(String consumeMoneyDescription) {
        this.consumeMoneyDescription = consumeMoneyDescription;
    }

    public String getGetDate() {
        return getDate;
    }

    public void setGetDate(String getDate) {
        this.getDate = getDate;
    }

    public String getUseTimePeriod() {
        return useTimePeriod;
    }

    public void setUseTimePeriod(String useTimePeriod) {
        this.useTimePeriod = useTimePeriod;
    }

    public String getEndUseDate() {
        return endUseDate;
    }

    public void setEndUseDate(String endUseDate) {
        this.endUseDate = endUseDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getActContent() {
        return actContent;
    }

    public void setActContent(String actContent) {
        this.actContent = actContent;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

    @Override
    public String toString() {
        return "UserActData{" +
                "actId='" + actId + '\'' +
                ", couponNo='" + couponNo + '\'' +
                ", useTypeName='" + useTypeName + '\'' +
                ", useType='" + useType + '\'' +
                ", actTitle='" + actTitle + '\'' +
                ", consumeAmount=" + consumeAmount +
                ", creditAmount=" + creditAmount +
                ", actAmount=" + actAmount +
                ", paidType='" + paidType + '\'' +
                ", actSubTitle='" + actSubTitle + '\'' +
                ", couponTypeName='" + couponTypeName + '\'' +
                ", couponType='" + couponType + '\'' +
                ", couponPeriod='" + couponPeriod + '\'' +
                ", actStatusName='" + actStatusName + '\'' +
                ", actStatus='" + actStatus + '\'' +
                ", suaId='" + suaId + '\'' +
                ", consumeMoneyDescription='" + consumeMoneyDescription + '\'' +
                ", getDate='" + getDate + '\'' +
                ", useTimePeriod='" + useTimePeriod + '\'' +
                ", endUseDate='" + endUseDate + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", actContent='" + actContent + '\'' +
                ", itemNames=" + itemNames +
                '}';
    }


}
