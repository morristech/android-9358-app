package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.BillInfo;

import java.util.List;

/**
 * Created by zr on 16-11-23.
 * 流水交易记录
 */
public class BillRecordResult extends BaseResult {
    public int pageCount;
    public RespData respData;

    public class RespData {
        public List<BillInfo> list;
        public int count;
        public int payMoney;
    }
}
