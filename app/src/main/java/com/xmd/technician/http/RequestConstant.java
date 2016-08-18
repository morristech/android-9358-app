package com.xmd.technician.http;

import com.xmd.technician.TechApplication;

/**
 * Created by sdcm on 15-10-23.
 */
public class RequestConstant {

    /*********************************************************************************************/
    /*                                           urls                                            */
    /*********************************************************************************************/
    public static final String SERVER_HOST = "http://spa.93wifi.com";
    public static final String BASE_URL = "/spa-manager/api";

    public static final String URL_LOGIN = BASE_URL + "/v1/tech/login";
    public static final String URL_LOGOUT = BASE_URL + "/v1/tech/logout";
    public static final String URL_REGISTER = BASE_URL + "/v1/tech/register";
    public static final String URL_EDIT_INFO = BASE_URL + "/v1/profile/tech/editInfo";
    public static final String URL_UPDATE_TECH_INFO = BASE_URL + "/v1/profile/tech";
    public static final String URL_CURRENT_INFO = BASE_URL + "/v1/profile/tech/current";
    public static final String URL_COMMENT_LIST = BASE_URL + "/v1/profile/tech/comments";
    public static final String URL_COMMENT_ORDER_REDPK_COUNT = BASE_URL + "/v2/tech/profile/notice";
    public static final String URL_MODIFY_PASSWORD = BASE_URL + "/v1/profile/tech/modifyPassword";
    public static final String URL_RESET_PASSWORD = BASE_URL + "/v1/tech/resetPassword";
    public static final String URL_FEEDBACK_CREATE = BASE_URL + "/v2/tech/feedback/create";
    public static final String URL_GET_ORDER_LIST = BASE_URL + "/v2/tech/profile/orders";
    public static final String URL_MANAGE_ORDER = BASE_URL + "/v2/tech/profile/order/manage";
    public static final String URL_HIDE_ORDER = BASE_URL + "/v2/tech/hide/order";
    public static final String URL_GETUI_BIND_CLIENT_ID = BASE_URL + "/v2/push/clientid";
    public static final String URL_GETUI_UNBIND_CLIENT_ID = BASE_URL + "/v2/push/unbind/clientid";
    public static final String URL_GET_ICODE = BASE_URL + "/v1/icode";
    public static final String URL_INVITE_CODE = BASE_URL + "/v1/profile/tech/club/change";
    public static final String URL_GET_WORKTIME = BASE_URL + "/v1/profile/tech/calendar";
    public static final String URL_UPDATE_WORKTIME = BASE_URL + "/v1/profile/tech/calendar/update";
    public static final String URL_DELETE_ALBUM = BASE_URL + "/v1/profile/tech/album/delete";
    public static final String URL_UPLOAD_ALBUM = BASE_URL + "/v1/profile/tech/album/base64/upload";
    public static final String URL_SORT_ALBUM = BASE_URL + "/v1/profile/tech/album/sort";
    public static final String URL_UPLOAD_AVATAR = BASE_URL + "/v1/profile/tech/avatar/base64/upload";
    public static final String URL_UPDATE_WORKSTATUS = BASE_URL + "/v1/profile/tech/status/update";

    public static final String URL_QUIT_CLUB = BASE_URL + "/v2/tech/quit/club";

    public static final String URL_GET_SERVICE_LIST = BASE_URL + "/v2/tech/profile/service";
    public static final String URL_UPDATE_SERVICE_LIST = BASE_URL + "/v1/profile/tech/service/update";
    public static final String URL_GET_ACCOUNT_MONEY = BASE_URL + "/v1/profile/tech/money";
    public static final String URL_CONSUME_DETAIL = BASE_URL + "/v2/tech/profile/account/details";

    public static final String URL_GET_COUPON_LIST = BASE_URL + "/v1/profile/redpack/list";
    public static final String URL_GET_COUPON_INFO = BASE_URL + "/v1/profile/club/redpacketinfo";
    public static final String URL_COUPON_SHARE_QR_CODE = BASE_URL + "/v1/profile/redpacket/useqrcode";
    public static final String URL_PAID_COUPON_USER_DETAIL = BASE_URL + "/v1/profile/tech/coupon/userdetails";
    public static final String URL_ADD_CUSTOMER = BASE_URL +"/v2/tech/customer/edit";
    public static final String URL_GET_CUSTOMER_LIST = BASE_URL+"/v2/tech/customer/user/list";
    public static final String URL_GET_CUSTOMER_INFO_DETAIL = BASE_URL+"/v2/tech/customer/user/view";
    public static final String URL_GET_CLUB_LIST = BASE_URL+"/v2/tech/customer/tech/list";
    public static final String URL_GET_TECH_INFO_DETAIL =BASE_URL+"/v2/tech/customer/tech/view";
    public static final String URL_GET_MANAGER_INFO_DETAIL =BASE_URL+"/v2/tech/customer/manager/view";
    public static final String URL_DELETE_CONTACT = BASE_URL+"/v2/tech/customer/delete";
    public static final String URL_DO_DRAW_MONEY = BASE_URL+"/v2/wx/pay/user_reward_redpack";
    public static final String URL_GET_USER_WX_OPEN_ID = BASE_URL+"/v2/wx/oauth2/openid";
    public static final String URL_GET_USER_WX_CODE = BASE_URL+"/v2/wx/oauth2/code";
    public static final String URL_GET_USER_IS_BIND_WX = BASE_URL+"/v2/tech/is_bind_wx";

    public static final String URL_GET_CREDIT_SWITCH_STATUS = BASE_URL +"/v2/credit/switch/status";
    public static final String URL_GET_CREDIT_EXCHANGE_APPLY = BASE_URL +"/v2/credit/exchange/apply";
    public static final String URL_GET_CREDIT_USER_RECORDS = BASE_URL +"/v2/credit/user/records";
    public static final String URL_GET_CREDIT_USER_ACCOUNT = BASE_URL +"/v2/credit/user/account";
    public static final String URL_GET_CONTACT_MARK = BASE_URL +"/v2/club/impression/list";
    public static final String URL_INTRODUCE_BIND ="/spa-manager/follow9358/index.html";







    public static final String URL_COUPON_SHARE_EVENT_COUNT = BASE_URL + "/v1/profile/redpacket/share";
    public static final String URL_RANKING = "/spa-manager/manager/index.html?v=%s&userType=%s&sessionType=%s&token=%s";

    //    public static final String URL_APP_UPDATE_CONFIG = "mockjs/17/app-upgrade-system/appUpgrade";
    public static final String URL_APP_UPDATE_CONFIG = "/app-upgrade-system/appUpgrade";

    /*********************************************************************************************/
    /*                                           keys                                            */
    /*********************************************************************************************/

    public static final String USER_TYPE_TECH = "tech";

    public static final String KEY_USER_TYPE = "userType";

    public static final String KEY_ACTIVE_DATE = "activeDate";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CLIENT_ID = "clientId";
    public static final String KEY_APP_TYPE = "appType";
    public static final String APP_TYPE_ANDROID = "android";
    public static final String KEY_SECRET = "secret";
    public static final String userTypeKEY_SECRET = "secret";
    public static final String KEY_TELEPHONE = "telephone";
    public static final String KEY_REQ_DATE = "reqDate";

    public static final String KEY_SESSION_TYPE = "sessionType";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_ACT_ID = "actId";
    public static final String KEY_SUA_ID = "suaId";
    public static final String KEY_COUPON_NO = "couponNo";

    public static final String KEY_PHONE_NUMBER = "phoneNum";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_INVITE_CODE = "inviteCode";
    public static final String KEY_ICODE = "code";
    public static final String KEY_CLUB_CODE = "clubCode";
    public static final String KEY_CHANEL = "chanel";
    public static final String KEY_LOGIN_CHANEL = "loginChanel";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER = "user";
    public static final String KEY_STATUS = "status";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PAGE_NUMBER = "pageNumber";
    public static final String KEY_PAGE_SIZE = "pageSize";
    public static final String KEY_OLD_PASSWORD = "oldPassword";
    public static final String KEY_NEW_PASSWORD = "newPassword";

    public static final String KEY_PROCESS_TYPE = "processType";
    public static final String KEY_SORT_TYPE = "sortType";
    public static final String KEY_ID = "id";
    public static final String KEY_IDS = "ids";
    public static final String KEY_REASON = "reason";
    public static final String KEY_ORDER_ID = "orderId";

    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_DAY_RANGE = "dayRange";
    public static final String KEY_BEGIN_TIME = "beginTime";
    public static final String KEY_END_TIME = "endTime";
    public static final String KEY_END_DAY = "endDay";

    public static final String KEY_CONSUME_TYPE = "consumeType";

    public static final String KEY_IMG_FILE = "imgFile";
    public static final String KEY_TIME_STAMP = "timestamp";

    public static final String SESSION_TYPE = "app";
    public static  final String KEY_DRAW_MONEY_TYPE = "type";

    public static final String KEY_APP_VERSION = "appVersion";
    public static final String KEY_STAT_CATEGORY = "statCate";
    public static final String KEY_STAT_EVENT = "statEvent";
    public static final String KEY_APP_MODEL = "appModel";
    public static final String KEY_APP_BRAND = "appBrand";
    public static final String KEY_APP_IMEI = "appImei";
    public static final String KEY_NOTE_NAME="noteName";
    public static final String KEY_REMARK = "remark";
    public static final String KEY_CUSTOMER_ID = "contactId";
    public static final String KEY_CONTACT_TYPE = "contactType";
    public static final String KEY_MANAGER_URL = "managerHeadUrl";
    public static final String KEY_TAG_TYPE = "tagType";
    public static final String TECH_CUSTOMER = "tech_customer";
    public static final String KEY_MARK_IMPRESSION = "impression";
    public static final String KEY_CUSTOMER_TYPE = "customerType";
    public static final String TECH_ADD = "tech_add";
    public static final String FANS_USER = "fans_user";
    public static final String CONTACT_TYPE = "type";
    public static final String WX_USER = "wx_user";





    public static final String KEY_FILTER_ORDER = "filterOrder";
    public static final String KEY_COUPON_STATUS = "couponStatus";
    public static final String KEY_IS_EMPTY = "isEmpty";

    public static final String KEY_UPDATE_APP_ID = "appId";
    public static final String KEY_UPDATE_VERSION = "version";
    public static final String KEY_UPDATE_USER_ID = "userId";

    public static final String KEY_BIND_WX_SUCCESS = "bindSuccess";

    public static final String KEY_USER_WX_OPEN_ID = "openId";
    public static final String KEY_USER_WX_UNION_ID ="userWXUnionId";
    public static final String KEY_TRADE_AMOUNT = "tradeAmount";

    public static final String KEY_USER_WX_CODE = "code";
    public static final String KEY_USER_WX_SCOPE = "scope";
    public static final String KEY_USER_WX_STATE = "state";
    public static final String KEY_USER_WX_WXMP ="wxmp";
    public static final String KEY_USER_WX_PAGE_URL ="pageUrl";

    public static final String KEY_UER_CREDIT_AMOUNT = "amount";
    public static final String KEY_USER_CLUB_ID = "clubId";


    /*********************************************************************************************/
    /*                                        configs                                            */
    /*********************************************************************************************/

    public static final int REQUEST_TIMEOUT = 30 * 1000;
    public static final int RESP_TOKEN_EXPIRED = 401;
    public static final int RESP_ERROR = 400;

    public static final int RESP_ERROR_CODE_FOR_LOCAL = 9999;

}

