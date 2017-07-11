package com.xmd.chat.beans;

import java.util.List;

/**
 * Created by mo on 17-7-11.
 * 营销活动请求结果
 */

public class MarketingCategory {

    public static final String TIME_LIMIT = "paid_service_item";
    public static final String ONE_YUAN = "one_yuan";
    public static final String LUCKY_WHEEL = "lucky_wheel";

    public String categoryName;
    public String category;
    public List<Marketing> list;
}
