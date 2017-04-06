package com.xmd.technician.bean;

/**
 * Created by zr on 17-3-28.
 */

public class SayHiNearbyResult extends SayHiBaseResult {
    public int cusPosition;
    public String cusSayHiTime;

    public SayHiNearbyResult() {

    }

    public SayHiNearbyResult(SayHiBaseResult result) {
        this.statusCode = result.statusCode;
        this.msg = result.msg;
        this.pageCount = result.pageCount;
        this.userName = result.userName;
        this.userEmchatId = result.userEmchatId;
        this.userAvatar = result.userAvatar;
        this.userType = result.userType;
        this.respData = result.respData;
    }
}
