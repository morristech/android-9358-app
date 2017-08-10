package com.xmd.contact.httprequest;

/**
 * Created by Lhj on 17-7-1.
 */

public class RequestConstant {
    public static final String BASE_URL = "/spa-manager/api";
    // 获取所属会所附近客户数量
    public static final String URL_GET_NEARBY_CUS_COUNT = BASE_URL + "/v2/tech/club/nearby/user/count";
    //本店同事列表
    public static final String URL_GET_CLUB_EMPLOYEE_LIST = BASE_URL + "/v2/tech/colleague/list";
    //技师全部联系人
    public static final String URL_GET_TECH_CUSTOMER_USER_ALL_LIST = BASE_URL + "/v2/tech/customer/user/all/list";
    //技师拓客联系人
    public static final String URL_GET_TECH_CUSTOMER_USER_REGISTER_LIST = BASE_URL + "/v2/tech/customer/user/register/list";
    //最近访客
    public static final String URL_GET_CLUB_CUSTOMER_USER_RECENT_LIST = BASE_URL + "/v2/tech/customer/user/recent/list";
//    //本店同事详情
//    public static final String URL_CLUB_COLLEAGUE_DETAIL = BASE_URL + "/v2/tech/colleague/detail";

    //管理者全部联系人
    public static final String URL_GET_MANAGER_CUSTOMER_USER_ALL_LIST = BASE_URL + "/v2/manager/user/customer/all/list";
    //管理者最近访客
    public static final String URL_GET_MANAGER_CUSTOMER_USER_RECENT_LIST = BASE_URL + "/v2/manager/user/recentvisit/all/list";
    //标签列表
    public static final String URL_GET_MANAGER_TAG_ALL_LIST = BASE_URL + "/v2/manager/user/tag/all/list";


    public static final String KEY_PAGE = "page";
    public static final String KEY_PAGE_SIZE = "pageSize";
    public static final String KEY_CUSTOMER_LEVEL = "customerLevel";
    public static final String KEY_CUSTOMER_TYPE = "customerType";
    public static final String KEY_REMARK = "remark";
    public static final String KEY_TECH_ID = "techId";
    public static final String KEY_USER_GROUP = "userGroup";
    public static final String KEY_USER_NAME = "userName";

}
