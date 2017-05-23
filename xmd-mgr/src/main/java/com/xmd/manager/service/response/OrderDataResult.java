package com.xmd.manager.service.response;

/**
 * Created by Administrator on 2016/10/31.
 */
public class OrderDataResult extends BaseResult {

    /**
     * submitCount : 0
     * acceptCount : 13
     * completeCount : 0
     */

    public RespDataBean respData;

    public static class RespDataBean {
        public String submitCount;
        public String acceptCount;
        public String completeCount;
    }
}
