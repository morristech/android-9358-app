package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/8/10.
 */
public class CreditStatusResult extends BaseResult {

    /**
     * clubSwich : 测试内容2tn4
     * exchangeLimitation : 200
     * exchangeRatio : 100
     * systemSwitch : off
     */


    public RespDataBean respData;
    public  class RespDataBean {
        public String clubSwitch;
        public int exchangeLimitation;
        public int exchangeRatio;
        public String systemSwitch;
        public String diceGameSwitch;
        public int gameTimeoutSeconds;
    }
}
