package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.SettleRecordInfo;

import java.util.List;

/**
 * Created by zr on 17-4-24.
 */

public class SettleRecordResult extends BaseResult {
    public int pageCount;
    public RespData respData;

    public class RespData {
        public List<SettleRecordInfo> records;
        public int totalCount;
        public int totalRecordCount;
    }
}
