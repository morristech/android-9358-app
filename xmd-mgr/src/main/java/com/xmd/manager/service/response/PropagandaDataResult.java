package com.xmd.manager.service.response;

/**
 * Created by Lhj on 17-4-25.
 */

public class PropagandaDataResult extends BaseResult {


    public RespDataBean respData;

    public static class RespDataBean {

        public String deviceCount;
        public String todayUv;
        public String totalUv;
        public String totalWifiCount;
        public String uv;
        public String wifiCount;
        public String yesterdayUv;
    }
}
