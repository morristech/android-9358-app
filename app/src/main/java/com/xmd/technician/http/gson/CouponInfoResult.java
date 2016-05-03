package com.xmd.technician.http.gson;

import com.xmd.technician.bean.CouponInfo;

import java.util.List;

/**
 * Created by linms@xiaomodo.com on 16-4-29.
 */
public class CouponInfoResult extends BaseResult {

    public Content respData;
    public class Content {
        public CouponInfo activities;
        public String shareUrl;
        public String imgUrl;
        public String clubName;
        public List<ItemInfo> items;

        public class ItemInfo{
            public String name;
        }
    }
}
