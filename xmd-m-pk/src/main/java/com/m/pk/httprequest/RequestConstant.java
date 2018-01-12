package com.m.pk.httprequest;

/**
 * Created by Lhj on 18-1-3.
 */

public class RequestConstant {

    public static final String BASE_URL = "/spa-manager/api";
    /**
     * PK排行榜
     */
    public static final String URL_GET_TECH_PK_ACTIVITY_RANKING = BASE_URL + "/v1/tech/pk/activity/ranking/index";
    public static final String URL_GET_TECH_PK_ACTIVITY_LIST = BASE_URL + "/v1/tech/pk/activity/list";
    public static final String URL_GET_PK_TEAM_RANKING_LIST = BASE_URL + "/v1/tech/pk/activity/team/ranking/list";
    public static final String URL_GET_PK_PERSONAL_RANKING_LIST = BASE_URL + "/v1/tech/pk/activity/personal/ranking/list";
    public static final String URL_GET_PERSONAL_RANKING_LIST = BASE_URL + "/v2/manager/ranking_list";

    public static final String KEY_SORT_BY_COMMENT = "commentStat";
    public static final String KEY_SORT_BY_CUSTOMER = "customerStat";
    public static final String KEY_SORT_BY_SALE = "saleStat";
    public static final String KEY_SORT_BY_COUPON = "couponStat";
    public static final String KEY_SORT_BY_PANIC = "paidServiceItemStat";
    public static final String KEY_PK_ACTIVITY_ID = "pkActivityId";
    public static final String KEY_SORT_KEY = "sortKey";
    public static final String KEY_TECH_SORT_BY_USER = "userList"; //拓客之星
    public static final String KEY_TECH_SORT_BY_COMMENT = "commentList"; //服务之星
    public static final String KEY_TECH_SORT_BY_PAID = "paidList"; //销售之星
    public static final String KEY_TECH_RANKING_SOR_TYPE = "type";
    public static final String KEY_SELECT = "select";
    public static final String KEY_SELECT_BY_DAY = "day";
    public static final String KEY_SELECT_BY_WEEK = "week";
    public static final String KEY_SELECT_BY_MONTH = "month";
    public static final String KEY_TEAM_ID = "teamId";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PAGE_NUMBER = "pageNumber";
    public static final String KEY_PAGE_SIZE = "pageSize";
    public static final String KEY_USER_TYPE = "userType";
}
