package com.xmd.manager.beans;

import com.xmd.manager.service.response.BaseResult;

/**
 * Created by Administrator on 2016/6/27.
 */
public class OrderDetailResult extends BaseResult {
    public OrderDetail respData;
    public String type;
    public String starTime;
    public String endTime;

    public class OrderDetail {
        public int sumCount;
        public int completeCount;
        public int acceptCount;
        public int rejectCount;
        public int failureCount;
        public int overtimeCount;
        public int submitCount;
        public Object createdAt;
        public Object expireCount;
        public Object expireAmount;
        public Object completeAmount;
        public Object sumAmount;
    }


}
