package com.xmd.manager.service.response;

/**
 * Created by sdcm on 16-7-20.
 */
public class RegisterStatisticsResult extends BaseResult {

    public Content respData;
    public String type;

    public class Content {
        public int tempUserCount;
        public int userCount;
        public int userTotal;
        public int weixinCount;
    }
}
