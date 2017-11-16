package com.xmd.manager.service.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/11/10.
 */
public class MarketingResult extends BaseResult {

    /**
     * 04 : 0
     * 05 : 0
     * 06 : 0
     * 07 : 4
     * 01 : 0
     * 02 : 0
     * 03 : 0
     */

    public RespDataBean respData;


    public static class RespDataBean {
        @SerializedName("04")
        public int value04;
        @SerializedName("05")
        public int value05;
        @SerializedName("06")
        public int value06;
        @SerializedName("07")
        public int value07;
        @SerializedName("01")
        public int value01;
        @SerializedName("02")
        public int value02;
        @SerializedName("03")
        public int value03;
        @SerializedName("09")
        public int value09;
        @SerializedName("10")
        public int value10;
    }
}