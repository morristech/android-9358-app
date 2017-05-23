package com.xmd.manager.service.response;

import java.util.List;

/**
 * Created by lhj 2016/9/21.
 */
public class LineChartDataResult extends BaseResult {


    public RespDataBean respData;

    public static class RespDataBean {
        public String clubAmount;
        public String couponGetCount;
        public String couponOpenCount;
        public String couponShareCount;
        public String couponUseCount;
        public String techCommission;
        public List<Long> dateTime;
        public List<Integer> data;

    }
}
