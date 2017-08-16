package com.xmd.black.httprequest;

/**
 * Created by Lhj on 17-7-22.
 */

public class RequestConstant {


    public static final String BASE_URL = "/spa-manager/api";
    /**
     * --------------------------------------> 聊天黑名单 <----------------------------------------
     */
    // 将联系人加入黑名单
    public static final String URL_ADD_TO_BLACKLIST = BASE_URL + "/v2/tech/customer/add/emchat/blacklist";
    // 将联系人移出黑名单
    public static final String URL_REMOVE_FROM_BLACKLIST = BASE_URL + "/v2/tech/customer/remove/emchat/blacklist";
    // 联系人是否在黑名单中
    public static final String URL_IN_BLACKLIST = BASE_URL + "/v2/tech/customer/in/emchat/blacklist";
    // 获取黑名单列表
    public static final String URL_GET_TECH_BLACKLIST = BASE_URL + "/v2/tech/customer/emchat/blacklist";
    // 是否在联系人黑名单中
    public static final String URL_IN_USER_BLACKLIST = BASE_URL + "/v2/tech/customer/in/user/blacklist";
    //标签
    public static final String URL_GET_CONTACT_MARK = BASE_URL + "/v2/club/impression/list";
    //技师修改备注
    public static final String URL_EDIT_CUSTOMER = BASE_URL + "/v2/tech/customer/edit";
    //添加
    public static final String URL_ADD_CUSTOMER = BASE_URL + "/v2/tech/customer/create";
    //管理者修改备注
    public static final String URL_MANAGER_USER_EDIT = BASE_URL + "/v2/manager/user/edit";

}
