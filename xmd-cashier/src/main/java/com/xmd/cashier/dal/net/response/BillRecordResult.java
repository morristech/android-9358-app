package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.m.network.BaseBean;

import java.util.List;

/**
 * Created by zr on 16-11-23.
 * 流水交易记录
 */
public class BillRecordResult extends BaseBean<BillRecordResult.RespData> {

    public class RespData {
        public List<BillInfo> list;
        public int count;
        public int payMoney;
    }
}
