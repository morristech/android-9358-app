package com.xmd.technician.http.gson;

import java.util.List;

/**
 * Created by Lhj on 2017/2/9.
 */

public class ActivityListResult extends BaseResult {


    public List<RespDataBean> respData;

    public static class RespDataBean {
        /**
         * count : 2
         * actName : 限时抢
         * actType : paidItem
         */

        public String count;
        public String actName;
        public String actType;
    }
}
