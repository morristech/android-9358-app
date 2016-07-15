package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/7/8.
 */
public class AddOrEditResult extends BaseResult {
    public String msg;
    public int resultcode;
    public AddOrEditResult(String msg, int resultCode){
        this.msg =msg;
        this.resultcode = resultCode;

    }

}
