package com.xmd.manager.service.response;

/**
 * Created by linms@xiaomodo.com on 16-5-26.
 */
public class StatisticsHomeDataResult extends BaseResult {

    public Content respData;
    public String type;

    public class Content {
        /**
         * 会所点钟券收入
         */
        public float clubAmount;

        /**
         * 优惠券领取数
         */
        public int couponGetCount;
        /**
         * 优惠券浏览数
         */
        public int couponOpenCount;
        /**
         * 优惠券分享数
         */
        public int couponShareCount;
        /**
         * 优惠券使用数
         */
        public int couponUseCount;
        /**
         * 订单数
         */
        public int orderCount;
        /**
         * 技师提成
         */
        public float techCommission;
        /**
         * 用户注册数
         */
        public int userCount;
        /**
         * 会所访问数
         */
        public int uv;
        /**
         * wifi宣传数
         */
        public int wifiCount;
    }
}
