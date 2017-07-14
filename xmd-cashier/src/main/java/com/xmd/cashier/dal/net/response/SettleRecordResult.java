package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.SettleRecordInfo;
import com.xmd.m.network.BaseBean;

import java.util.List;

/**
 * Created by zr on 17-4-24.
 */

public class SettleRecordResult extends BaseBean<SettleRecordResult.RespData> {

    public class RespData {
        public List<SettleRecordInfo> records;
        public int totalCount;
        public int totalRecordCount;
    }
}
