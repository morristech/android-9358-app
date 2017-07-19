package com.xmd.m.comment.httprequest;

/**
 * Created by Lhj on 17-7-1.
 */

public class RequestConstant {

    public static final String BASE_URL = "/spa-manager/api";
    public static final String URL_COMMENT_LIST = BASE_URL + "/v2/comment/list";
    public static final String URL_COMMENT_TECH_LIST = BASE_URL + "/v2/manager/tech/base/list";
    public static final String URL_USER_BAD_COMMENT_STATUS_UPDATE = BASE_URL + "/v2/manager/user/badcomment/status/update";
    public static final String URL_CUSTOMER_USER_DETAIL = BASE_URL + "/v2/manager/user/detail";
    public static final String URL_USER_CONSUME_LIST = BASE_URL + "/v2/manager/user/consume/list";
    public static final String URL_USER_SHOP_LIST = BASE_URL + "/v2/manager/user/shop/list";
    public static final String URL_USER_REWARD_LIST = BASE_URL + "/v2/manager/user/reward/list";

    public static final String URL_TECH_CUSTOMER_DETAIL = BASE_URL + "/v2/tech/customer/detail";
    public static final String URL_TECH_USER_CONSUME_LIST = BASE_URL + "/v2/tech/customer/consume/list";
    public static final String URL_TECH_USER_SHOP_LIST = BASE_URL + "/v2/tech/customer/shop/list";
    public static final String URL_TECH_USER_REWARD_LIST = BASE_URL + "/v2/tech/customer/reward/list";
    // 将联系人加入黑名单
    public static final String URL_ADD_TO_BLACKLIST = BASE_URL + "/v2/tech/customer/add/emchat/blacklist";
    // 将联系人移出黑名单
    public static final String URL_REMOVE_FROM_BLACKLIST = BASE_URL + "/v2/tech/customer/remove/emchat/blacklist";
    // 联系人是否在黑名单中
    public static final String URL_IN_BLACKLIST = BASE_URL + "/v2/tech/customer/in/emchat/blacklist";
    //本店同事详情
    public static final String URL_CLUB_COLLEAGUE_DETAIL = BASE_URL + "/v2/tech/colleague/detail";
    // 查询同客户的联系限制
    public static final String URL_GET_CONTACT_PERMISSION = BASE_URL + "/v2/tech/contact/permission/{id}";
    //删除联系人
    public static final String URL_DELETE_CONTACT = BASE_URL + "/v2/tech/customer/delete";
    //标签
    public static final String URL_GET_CONTACT_MARK = BASE_URL + "/v2/club/impression/list";
    //修改备注
    public static final String URL_EDIT_CUSTOMER = BASE_URL + "/v2/tech/customer/edit";

    public static final String URL_DO_GROUP_USER_EDIT_GROUP = BASE_URL + "/v2/manager/group/user/editGroup";

    public static final String KEY_COMMENT_TYPE = "commentType";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PAGE_SIZE = "pageSize";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_TECH_ID = "techId";
    public static final String KEY_TYPE = "type";//1:差评，２，中评，３，好评
    public static final String KEY_USER_NAME = "userName";//complaint:只看投诉，comment只看评论
    public static final String KEY_USER_SEARCH = "search";
    public static final String KEY_USER_ID = "userId";//用户id
    public static final String KEY_RETURN_STATUS = "returnStatus"; //回访状态
    public static final String KEY_STATUS = "status";//评论状态

    public static final String KEY_COMMENT_ID = "commentId";
    public static final String KEY_COMMENT_STATUS = "status";
    public static final String VALID_COMMENT = "valid";//未回访
    public static final String INDEX_COMMENT = "index";//首页
    public static final String FINISH_COMMENT = "finish";//已回访
    public static final String DELETE_COMMENT = "delete";
    public static final String CUSTOMER_TYPE_FANS_WX = "fans_wx_user";
    public static final String CUSTOMER_TYPE_FANS = "fans_user";
    public static final String CUSTOMER_TYPE_WX = "wx_user";
    public static final String CUSTOMER_TYPE_ADD = "temp_user";
    public static final String KEY_EMP_ID = "empId";
}
