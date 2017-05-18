package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Lhj on 2016/7/8.
 */
public class DeleteContactResult extends BaseResult {
    public String msg;
    public int resultcode;
    public  DeleteContactResult(String msg,int resultCode){
        this.msg =msg;
        this.resultcode = resultCode;
    }

}
