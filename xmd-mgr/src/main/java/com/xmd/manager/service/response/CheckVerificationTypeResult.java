package com.xmd.manager.service.response;

/**
 * Created by lhj on 2016/12/7.
 */

public class CheckVerificationTypeResult extends BaseResult {
    public String respData;
    public String code;

    public CheckVerificationTypeResult(int statusCode, String respData, String code, String msg) {
        this.statusCode = statusCode;
        this.respData = respData;
        this.code = code;
        this.msg = msg;
    }
}
