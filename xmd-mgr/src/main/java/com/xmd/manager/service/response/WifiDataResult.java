package com.xmd.manager.service.response;

/**
 * Created by Administrator on 2016/10/31.
 */
public class WifiDataResult extends BaseResult {

    /**
     * totalWifiCount : 0
     * wifiCount : 0
     */

    public RespDataBean respData;

    public static class RespDataBean {
        public int totalWifiCount;
        public int wifiCount;

    }
}
