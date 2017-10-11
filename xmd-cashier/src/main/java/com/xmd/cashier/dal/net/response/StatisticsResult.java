package com.xmd.cashier.dal.net.response;


import com.xmd.cashier.dal.bean.OfflineStatisticInfo;
import com.xmd.cashier.dal.bean.OnlineStatisticInfo;
import com.xmd.m.network.BaseBean;

/**
 * Created by zr on 16-1-22.
 * 对账统计
 */
public class StatisticsResult extends BaseBean<StatisticsResult.Statistics> {
    public class Statistics {
        public OnlineStatisticInfo online;
        public OfflineStatisticInfo offline;
    }
}
