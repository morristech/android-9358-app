package com.xmd.manager.service.response;

import com.xmd.manager.beans.CouponInfo;

import java.util.List;

/**
 * Created by sdcm on 15-12-14.
 */
public class UserCouponListResult extends BaseResult {

    public Content respData;

    public class Content {
        public List<CouponInfo> canUseList;
        public List<CouponInfo> settledList;
    }
}
