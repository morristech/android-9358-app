package com.xmd.manager.service.response;

/**
 * Created by linms@xiaomodo.com on 16-5-27.
 */
public class StatisticsMainPageResult extends BaseResult {

    public Content respData;

    public class Content {

        public String acceptCount;
        public String completeCount;
        public String submitCount;
        public String couponGetCount;
        public String totalCouponGetCount;
        public String userCount;
        public String totalUserCount;
        public String uv;
        public String totalUv;
        public String wifiCount;
        public String totalWifiCount;
        public RankingTechnican commentRanking;
        public RankingTechnican paidRanking;
        public RankingTechnican userRanking;

        public class RankingTechnican {
            public String avatar;
            public String avatarUrl;
            public String counts;
            public String id;
            public String name;
            public String serialNo;
        }
    }

}
