package com.xmd.cashier.dal.net.response;


import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.Item;

import java.util.List;

/**
 * Created by sdcm on 15-12-14.
 */
public class UserCouponViewResult extends BaseResult {

    public Content respData;

    public static class Content {
        public String placeImageUrl;
        public CouponInfo userAct;
        public String clubAddress;
        public String clubId;
        public List<Item> items;
        public String imageUrl;
        public String clubName;
    }
}
