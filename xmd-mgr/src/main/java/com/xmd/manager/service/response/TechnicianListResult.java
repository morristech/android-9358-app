package com.xmd.manager.service.response;

/**
 * Created by heyangya on 16-11-4.
 */

public class TechnicianListResult extends BaseListResult<TechnicianListResult.Item> {
    public static class Item {
        public String techId;
        public String avatarUrl;
        public String techName;
        public String techNo;
    }
}
