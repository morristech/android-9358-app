package com.xmd.manager.service.response;

import com.xmd.manager.beans.CouponInfo;

import java.util.List;

/**
 * Created by sdcm on 15-10-27.
 */
public class DeliveryClubCouponResult extends BaseListResult<CouponInfo> {
    public DeliveryClubCouponResult(List<CouponInfo> list) {
        statusCode = 200;
        respData = list;
    }
}
