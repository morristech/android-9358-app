package com.xmd.manager.service.response;

import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.Item;

import java.util.List;

/**
 * Created by sdcm on 15-12-11.
 */
public class ClubCouponViewResult extends BaseResult {

    public Content respData;

    public class Content {
        public CouponInfo coupon;
        public String shareTitle;
        public String shareUrl;
        public List<Item> items;
    }
}
