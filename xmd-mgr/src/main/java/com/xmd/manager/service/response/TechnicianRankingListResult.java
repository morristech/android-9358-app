package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-11-4.
 */

public class TechnicianRankingListResult extends BaseListResult<TechnicianRankingListResult.Item> {
    public static class Item {
        public String id;
        public String avatarUrl;
        public String name;
        public String serialNo;
    }
}
