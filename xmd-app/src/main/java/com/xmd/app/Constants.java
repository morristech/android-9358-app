package com.xmd.app;

/**
 * Created by heyangya on 17-5-24.
 */

public class Constants {
    public static final String EXTAR_EVENT_TAG = "extra_event_tag";
    public static final String EXTRA_DATA = "extra_data";

    //技师状态
    public static final String TECH_STATUS_VALID = "valid"; //未加入会所
    public static final String TECH_STATUS_REJECT = "reject";//加入会所后被会所拒绝
    public static final String TECH_STATUS_UNCERT = "uncert";//等待会所审核
    public static final String TECH_STATUS_BUSY = "busy";//已成功加入会所，忙状态
    public static final String TECH_STATUS_FREE = "free";//已成功加入会所，休状态
    public static final String USER_MARK_TECH_ADD = "通讯录";//通讯录
    public static final String USER_MARK_NEW_ADD = "新客";//新客
    public static final String USER_MARK_ACTIVATION = "待激活";//待激活
    public static final String USER_MARK_BIG = "大客";//大客
    public static final String USER_MARK_NORMAL = "普客";//普客
}
