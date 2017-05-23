package com.xmd.manager.beans;

import com.xmd.manager.service.response.BaseResult;

/**
 * Created by Administrator on 2016/12/26.
 */

public class IndexOrderData extends BaseResult {


    /**
     * respData : {"submitCount":"0","acceptCount":"25","completeCount":"0"}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        /**
         * submitCount : 0
         * acceptCount : 25
         * completeCount : 0
         */

        public String submitCount;
        public String acceptTotal;
        public String acceptCount;
    }
}
