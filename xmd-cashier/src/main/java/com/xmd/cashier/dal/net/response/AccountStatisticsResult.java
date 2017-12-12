package com.xmd.cashier.dal.net.response;


import com.xmd.cashier.dal.bean.OfflineAccountStatisticInfo;
import com.xmd.cashier.dal.bean.OnlineAccountStatisticInfo;
import com.xmd.m.network.BaseBean;

/**
 * Created by zr on 16-1-22.
 * 对账统计
 */
public class AccountStatisticsResult extends BaseBean<AccountStatisticsResult.Statistics> {
    public class Statistics {
        public OnlineAccountStatisticInfo online;
        public OfflineAccountStatisticInfo offline;
    }
}
