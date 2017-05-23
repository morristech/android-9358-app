package com.xmd.cashier.dal.net.response;


import com.xmd.cashier.dal.bean.CouponInfo;

import java.util.List;

/**
 * Created by sdcm on 15-12-14.
 */
public class UserCouponListResult extends BaseResult {

    public DATA respData;

    public static class DATA {
        public List<CouponInfo> canUseList;
    }
}
