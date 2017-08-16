package com.xmd.contact.httprequest;

/**
 * Created by Lhj on 17-7-5.
 */

public class ConstantResources {

    public static final String INTENT_APP_TYPE = "appType";
    public static final String APP_TYPE_MANAGER = "manager";
    public static final String APP_TYPE_TECH = "tech";
    public static final String CUSTOMER_TYPE_USER_ADD = "userAdd";//添加联系人
    public static final String CUSTOMER_TYPE_RECENT_VISIT = "recent_visit";

    /**
     * 联系人优化
     */
    public static final int FILTER_ALL_CONTACT = 0; //所有联系人
    public static final int FILTER_WX_CONTACT = 1;//wx联系人
    public static final int FILTER_FANS_CONTACT = 2;//粉丝用户/手机用户
    public static final int FILTER_FANS_AND_WX_CONTACT = 3;//粉丝用户+wx用户
    public static final int FILTER_PHONE_CONTACT = 4;//手机联系人
    public static final String USER_WX = "wx_user";
    public static final String USER_FANS = "fans_user";
    public static final String USER_FANS_WX = "fans_wx_user";
    public static final String USER_ZFB = "alipay";
    public static final String USER_TECH_ADD = "tech_add";
    public static final String USER_ALL = "";
    public static final String USER_MARK_TECH_ADD = "通讯录";//通讯录
    public static final String USER_MARK_NEW_ADD = "新客";//新客
    public static final String USER_MARK_ACTIVATION = "待激活";//待激活
    public static final String USER_MARK_BIG = "大客";//大客
    public static final String USER_MARK_NORMAL = "普客";//普客
    public static final int CONTACT_ALL_INDEX = 0; //所有联系人
    public static final int CONTACT_REGISTER_INDEX = 1; //我的拓客
    public static final int CONTACT_VISITOR_INDEX = 2; //最近访客
    public static final int CONTACT_CLUB_INDEX = 3; //本店同事
    public static final int CONTACT_CLUB_ALL_INDEX = 0; //管理者所有用户
    public static final int CONTACT_CLUB_RECENT_INDEX = 1;//管理者最新访客
    public static final int CONTACT_CLUB_TECHNICIAN_INDEX = 2;//管理者联系人
    public static final String CLUB_EMPLOYEE_HAS_NONE_GROUP = "has_none_group";//会所用户未进行分组
    public static final String CLUB_EMPLOYEE_DEFAULT_GROUP = "未分组";//会所用户未进行分组名称
    public static final int CLUB_EMPLOYEE_DEFAULT_GROUP_ID = 999;//会所用户未进行分组ID
    public static final int CONTACT_RECENT_TYPE_NORMAL = 0;
    public static final int CONTACT_RECENT_TYPE_COMMENT = 1;
    public static final int CONTACT_RECENT_TYPE_COLLECTION = 2;
    public static final int CONTACT_RECENT_TYPE_COUPON = 3;
    public static final int CONTACT_RECENT_TYPE_PAID_COUPON = 4;
    public static final int CONTACT_RECENT_TYPE_REWARD = 5;
    public static final int CONTACT_RECENT_TYPE_VISIT_CLUB = 10;
    public static final int CONTACT_RECENT_TYPE_CONSUME = 11;
}
