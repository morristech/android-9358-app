package com.xmd.manager.service.response;

import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.Item;

import java.util.List;

/**
 * Created by sdcm on 15-12-14.
 */
public class UserCouponViewResult extends BaseResult {

    public Content respData;

    public class Content {
        public String placeImageUrl;
        public CouponInfo userAct;
        public String clubAddress;
        public String clubId;
        public List<Item> items;
        public String imageUrl;
        public String clubName;
    }
}
