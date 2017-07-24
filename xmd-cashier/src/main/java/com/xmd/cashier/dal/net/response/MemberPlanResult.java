package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.MemberPlanInfo;
import com.xmd.m.network.BaseBean;

/**
 * Created by zr on 17-7-17.
 */

public class MemberPlanResult extends BaseBean<MemberPlanResult.RespData> {
    public class RespData {
        public MemberPlanInfo activity;
    }
}
