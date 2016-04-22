package com.xmd.technician.http.gson;

import com.xmd.technician.model.CouponInfo;

import java.util.List;

/**
 * Created by sdcm on 16-4-14.
 */
public class RedpackResult extends BaseResult{

    public Content respData;

    public class Content{
        public String techCode;
        public List<CouponInfo> coupons;
    }
}
