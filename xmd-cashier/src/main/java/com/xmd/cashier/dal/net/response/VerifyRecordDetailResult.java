package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.VerifyRecordDetailInfo;
import com.xmd.cashier.dal.bean.VerifyRecordInfo;
import com.xmd.m.network.BaseBean;

import java.util.List;

/**
 * Created by zr on 17-5-2.
 */

public class VerifyRecordDetailResult extends BaseBean<VerifyRecordDetailResult.RespData> {

    public class RespData {
        public List<VerifyRecordDetailInfo> detail;
        public VerifyRecordInfo record;
    }
}
