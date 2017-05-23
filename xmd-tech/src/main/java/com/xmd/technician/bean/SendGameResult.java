package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/8/23.
 */
public class SendGameResult extends BaseResult {


    /**
     * gameId : 767979236991463425
     */

    public RespDataBean respData;

    public static class RespDataBean {
        public String gameId;
    }
}
