package com.xmd.technician.http.gson;

import com.xmd.technician.bean.CouponInfo;

import java.util.List;

/**
 * Created by sdcm on 16-4-14.
 */
public class CouponListResult extends BaseResult {

    public Content respData;

    public class Content {
        public String techCode;
        public List<CouponInfo> coupons;
    }
}
