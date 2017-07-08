package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.SettleSummaryInfo;
import com.xmd.m.network.BaseBean;

import java.util.List;

/**
 * Created by zr on 17-4-24.
 */

public class SettleSummaryResult extends BaseBean<SettleSummaryResult.RespData> {

    public class RespData {
        public SettleSummaryInfo obj;
        public List<SettleSummaryInfo> recordDetailList;
    }
}
