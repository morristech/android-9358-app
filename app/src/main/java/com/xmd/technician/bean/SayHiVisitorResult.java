package com.xmd.technician.bean;

/**
 * Created by zr on 17-3-28.
 */

public class SayHiVisitorResult extends SayHiBaseResult {
    public String position;

    public SayHiVisitorResult() {

    }

    public SayHiVisitorResult(SayHiBaseResult result) {
        this.statusCode = result.statusCode;
        this.msg = result.msg;
        this.pageCount = result.pageCount;
        this.userName = result.userName;
        this.userEmchatId = result.userEmchatId;
    }
}
