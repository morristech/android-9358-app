package com.xmd.manager.service.response;

/**
 * Created by lhj on 2016/12/7.
 */

public class VerificationSaveResult extends BaseResult {
    public String respData;

    public VerificationSaveResult(String error) {
        this.msg = error;
    }
}
