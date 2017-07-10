package com.xmd.app;

/**
 * Created by heyangya on 17-5-24.
 */

public class Constants {
    public static final String EXTAR_EVENT_TAG = "extra_event_tag";
    public static final String EXTRA_DATA = "extra_data";

    public static final String EXTRA_CHAT_ID = "extra_chat_id";

    //技师状态
    public static final String TECH_STATUS_VALID = "valid"; //未加入会所
    public static final String TECH_STATUS_REJECT = "reject";//加入会所后被会所拒绝
    public static final String TECH_STATUS_UNCERT = "uncert";//等待会所审核
    public static final String TECH_STATUS_BUSY = "busy";//已成功加入会所，忙状态
    public static final String TECH_STATUS_FREE = "free";//已成功加入会所，休状态
}
