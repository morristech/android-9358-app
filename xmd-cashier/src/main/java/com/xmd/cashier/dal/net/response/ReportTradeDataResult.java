package com.xmd.cashier.dal.net.response;

import com.xmd.m.network.BaseBean;

/**
 * Created by heyangya on 16-9-18.
 */

public class ReportTradeDataResult extends BaseBean<ReportTradeDataResult.RespData> {
    public class RespData {
        public int cashierPoints;
    }
}
