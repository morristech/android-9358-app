package com.xmd.manager.beans;

import com.xmd.manager.service.response.BaseResult;

/**
 * Created by Administrator on 2016/8/5.
 */
public class CheckCouponResult extends BaseResult {
    public String respData;

    public CheckCouponResult(String respData, String msg) {
        this.respData = respData;
        this.msg = msg;
    }
}
