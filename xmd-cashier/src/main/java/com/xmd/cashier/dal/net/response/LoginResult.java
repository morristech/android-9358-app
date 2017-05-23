package com.xmd.cashier.dal.net.response;

/**
 * Created by heyangya on 16-8-22.
 */

public class LoginResult extends BaseResult {
    public String status;
    public String token;
    public String name;
    public String userId;
    public String loginName;
    public String phoneNum;
    public String avatarUrl;
    public String message;
    //Manager's inviteCode
    public String inviteCode;
    public String emchatId;
    public String emchatPassword;
    public String roles;

    @Override
    public String toString() {
        return "LoginResult{" +
                "avatarUrl='" + avatarUrl + '\'' +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", loginName='" + loginName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", message='" + message + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", emchatId='" + emchatId + '\'' +
                ", emchatPassword='" + emchatPassword + '\'' +
                '}';
    }
}
