package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.SettleContentInfo;
import com.xmd.m.network.BaseBean;

import java.util.List;

/**
 * Created by zr on 17-4-24.
 */

public class SettleSummaryResult extends BaseBean<SettleSummaryResult.RespData> {

    public class RespData {
        public String startTime;
        public String endTime;
        public List<SettleContentInfo> settleList;

        public String createTime;
        public String settleName;
    }
}
