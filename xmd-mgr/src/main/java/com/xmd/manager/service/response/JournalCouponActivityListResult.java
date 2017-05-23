package com.xmd.manager.service.response;

import java.util.List;

/**
 * Created by heyangya on 16-11-18.
 */

public class JournalCouponActivityListResult extends BaseListResult<JournalCouponActivityListResult.Category> {
    public static class Category {
        public String actType;
        public String actTypeName;
        public List<Item> details;
    }

    public static class Item {
        public String desc;
        public String value;
    }
}
