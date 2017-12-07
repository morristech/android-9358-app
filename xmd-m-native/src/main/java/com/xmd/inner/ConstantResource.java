package com.xmd.inner;


import com.xmd.app.utils.ResourceUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Lhj on 17-12-01.
 * 用于存放共用静态类
 */

public class ConstantResource {
    public static final String BILL_GOODS_TYPE = "goods";
    public static final String BILL_SPA_TYPE = "spa";

    public static final String STATUS_FREE = "free";
    public static final String STATUS_USING = "using";
    public static final String STATUS_BOOKED = "booked";
    public static final String STATUS_CLEAN = "clean";
    public static final String STATUS_DISABLED = "disabled";

    public static final String TEXT_STATUS_FREE = "空闲";
    public static final String TEXT_STATUS_USING = "使用";
    public static final String TEXT_STATUS_BOOKED = "预订";
    public static final String TEXT_STATUS_CLEAN = "清洁";
    public static final String TEXT_STATUS_DISABLED = "禁用";

    public static final int TYPE_COLOR_GREEN = 1;
    public static final int TYPE_COLOR_PINK = 2;
    public static final int TYPE_COLOR_BLUE = 3;
    public static final int TYPE_COLOR_YELLOW = 4;
    public static final int TYPE_COLOR_GRAY = 5;
    public static final int TYPE_COLOR_GRAPE = 6;

    public static final int SEAT_TYPE_BED = 0;
    public static final int SEAT_TYPE_SOFA = 2;
    public static final int SEAT_TYPE_BATH = 1;

    public static final String RESPONSE_YES = "Y";

    public static final Map<Integer, Integer> STATUS_TYPE_COLOR = new LinkedHashMap<Integer, Integer>() {
        {
            put(TYPE_COLOR_GREEN, ResourceUtils.getColor(R.color.status_color_green));
            put(TYPE_COLOR_PINK, ResourceUtils.getColor(R.color.status_color_pink));
            put(TYPE_COLOR_BLUE, ResourceUtils.getColor(R.color.status_color_blue));
            put(TYPE_COLOR_YELLOW, ResourceUtils.getColor(R.color.status_color_yellow));
            put(TYPE_COLOR_GRAY, ResourceUtils.getColor(R.color.status_color_gray));
            put(TYPE_COLOR_GRAPE, ResourceUtils.getColor(R.color.status_color_grape));
        }
    };

    public static final Map<String, Integer> DEFAULT_STATUS_COLOR_TYPE = new LinkedHashMap<String, Integer>() {
        {
            put(STATUS_FREE, TYPE_COLOR_GREEN);
            put(STATUS_USING, TYPE_COLOR_PINK);
            put(STATUS_CLEAN, TYPE_COLOR_BLUE);
            put(STATUS_BOOKED, TYPE_COLOR_YELLOW);
            put(STATUS_DISABLED, TYPE_COLOR_GRAY);
        }
    };

}
