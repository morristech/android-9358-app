package com.xmd.technician.http.gson;

/**
 * Created by sdcm on 16-3-10.
 */
public class LoginResult extends BaseResult {
    public String status;
    public String token;
    public String name;
    public String userId;
    public String loginName;
    public String phoneNum;
    public String avatarUrl;
    public String inviteCode;
    public String roles;
    public String emchatId;
    public String emchatPassword;
    public String message;
    public String spareTechId;
    public String clubId;
    public String clubName;
    public String IMPlatform; //"tencent" 聊天消息类型，为腾讯IM

    public Object respData;
}
