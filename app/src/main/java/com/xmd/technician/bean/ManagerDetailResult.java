package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/7/7.
 */
public class ManagerDetailResult extends BaseResult {
    public RespDataBean respData;

    public class RespDataBean {
        public String id;
        public String phoneNum;
        public Object avatarUrl;
        public String name;
        public String emchatId;
        public String loginName;
    }
}
