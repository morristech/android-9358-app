package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.VerifyRecordInfo;
import com.xmd.m.network.BaseBean;

import java.util.List;

/**
 * Created by zr on 17-5-2.
 */

public class VerifyRecordResult extends BaseBean<VerifyRecordResult.RespData> {

    public class RespData {
        public List<VerifyRecordInfo> data;
        public int remainderCount;  //当前时段或之前的总剩余记录数
        public int total;           //当前时段总记录数
    }
}

