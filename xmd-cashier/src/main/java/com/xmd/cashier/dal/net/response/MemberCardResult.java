package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.m.network.BaseBean;

/**
 * Created by zr on 17-7-19.
 */

public class MemberCardResult extends BaseBean<MemberCardResult.RespData> {
    public class RespData {
        public MemberInfo member;
        public String orderId;
        public String payUrl;
    }
}
