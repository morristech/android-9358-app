package com.xmd.manager.service.response;


import com.xmd.manager.beans.VerificationDetailBean;

import java.util.List;

/**
 * Created by Lhj on 2017/1/12.
 */

public class VerificationRecordListResult extends BaseResult {


    public RespDataBean respData;

    public static class RespDataBean {

        public String total;
        public int remainderCount;
        public List<VerificationDetailBean> data;
    }
}
