package com.xmd.manager.service.response;

import com.xmd.manager.beans.CouponInfo;

import java.util.List;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */
public class CouponListResult extends BaseResult {

    public Content respData;

    public class Content {
        public String techCode;
        public List<CouponInfo> coupons;
    }
}
