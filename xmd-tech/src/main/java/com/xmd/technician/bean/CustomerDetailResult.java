package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class CustomerDetailResult extends BaseResult {

    public RespDataBean respData;

    public class RespDataBean {
        public CustomerInfo techCustomer;
        public List<OrderBean> orders;
    }
}
