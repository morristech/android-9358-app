package com.xmd.technician.clubinvite.beans;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.xmd.app.BaseViewModel;
import com.xmd.technician.common.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mo on 17-8-11.
 * 会所邀请
 */

public class ClubInvite extends BaseViewModel implements Serializable{
    public static final String STATUS_INVITING = "inviting";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILED = "failed";

    private Long id;
    private String clubId;
    private String clubName;
    private String clubLogo;
    private String clubTelephone;
    private String userId;
    private String userTelephone;
    private String position;
    private String positionName;
    private String serialNo;
    private String status;
    private String message;
    private Long createTime;
    private String userName;
    private String operator;
    private String customerService;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserTelephone() {
        return userTelephone;
    }

    public void setUserTelephone(String userTelephone) {
        this.userTelephone = userTelephone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCustomerService() {
        return customerService;
    }

    public void setCustomerService(String customerService) {
        this.customerService = customerService;
    }

    public String getClubTelephone() {
        return clubTelephone;
    }

    public void setClubTelephone(String clubTelephone) {
        this.clubTelephone = clubTelephone;
    }

    public boolean getShowOperateButton() {
        return STATUS_INVITING.equals(getStatus());
    }

    public String getStatusString(){
        switch (getStatus()){
            case STATUS_SUCCESS:
                return "已接受";
            case STATUS_FAILED:
                return "已拒绝";
        }
        return "";
    }


    @BindingAdapter("time")
    public static void bindTime(TextView view, Long time) {
        view.setText(DateUtils.getSdf("yyyy.MM.dd HH:mm:ss").format(new Date(time)));
    }

    @Override
    public String toString() {
        return "ClubInvite{" +
                "id=" + id +
                ", clubId='" + clubId + '\'' +
                ", clubName='" + clubName + '\'' +
                ", clubLogo='" + clubLogo + '\'' +
                ", clubTelephone='" + clubTelephone + '\'' +
                ", userId='" + userId + '\'' +
                ", userTelephone='" + userTelephone + '\'' +
                ", position='" + position + '\'' +
                ", positionName='" + positionName + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", createTime=" + createTime +
                ", userName='" + userName + '\'' +
                ", operator='" + operator + '\'' +
                ", customerService='" + customerService + '\'' +
                '}';
    }
}
