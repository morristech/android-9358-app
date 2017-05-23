package com.xmd.manager.service.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/9/28.
 */
public class GroupInfoResult extends BaseResult {


    /**
     * allCount : 88
     * limitNumber : 0
     * activeCount : 10
     * switch : on
     * unactiveCount : 78
     */

    public RespDataBean respData;

    public static class RespDataBean {
        public int allCount;
        public int limitNumber;
        public int activeCount;
        public String sendInterval;
        @SerializedName("switch")
        public String switchX;
        public int unactiveCount;
        public int imageSize;


    }
}
