package com.xmd.manager.beans;

import com.xmd.manager.service.response.BaseResult;

/**
 * Created by Administrator on 2016/7/28.
 */
public class PayResult extends BaseResult {


    //  public String respData;
    public PayResult(int errorCode, String msg) {
        this.statusCode = errorCode;
        this.msg = msg;

    }

}
