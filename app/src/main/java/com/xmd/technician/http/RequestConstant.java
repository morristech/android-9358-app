package com.xmd.technician.http;

/**
 * Created by sdcm on 15-10-23.
 */
public class RequestConstant {

    /*********************************************************************************************/
    /*                                           urls                                            */
    /*********************************************************************************************/

    public static final String SERVER_HOST = "http://192.168.2.36:8051";
//    public static final String SERVER_HOST = "http://192.168.1.210:9880";
//    public static final String SERVER_HOST = "http://spa.93wifi.com";

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

    public static final String URL_GETUI_BIND_CLIENT_ID = BASE_URL + "/v2/push/clientid";
    public static final String URL_GETUI_UNBIND_CLIENT_ID = BASE_URL + "/v2/push/unbind/clientid";
    public static final String URL_GET_ICODE = BASE_URL + "/v1/icode";
    public static final String URL_INVITE_CODE = BASE_URL + "/v1/profile/tech/club/change";
    public static final String URL_GET_WORKTIME = BASE_URL + "/v1/profile/tech/calendar";
    public static final String URL_UPDATE_WORKTIME = BASE_URL + "/v1/profile/tech/calendar/update";
    public static final String URL_DELETE_ALBUM = BASE_URL + "/v1/profile/tech/album/delete";
    public static final String URL_UPLOAD_ALBUM = BASE_URL + "/v1/profile/tech/album/base64/upload";
    public static final String URL_UPLOAD_AVATAR = BASE_URL + "/v1/profile/tech/avatar/base64/upload";
    public static final String URL_UPDATE_WORKSTATUS = BASE_URL + "/v1/profile/tech/status/update";

    public static final String URL_GET_SERVICE_LIST = BASE_URL + "/v2/tech/profile/service";
    public static final String URL_UPDATE_SERVICE_LIST = BASE_URL + "/v1/profile/tech/service/update";
    public static final String URL_GET_ACCOUNT_MONEY = BASE_URL + "/v1/profile/tech/money";
    public static final String URL_CONSUME_DETAIL = BASE_URL + "/v2/tech/profile/account/details";

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

    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_DAY_RANGE = "dayRange";
    public static final String KEY_BEGIN_TIME = "beginTime";
    public static final String KEY_END_TIME = "endTime";
    public static final String KEY_END_DAY = "endDay";

    public static final String KEY_CONSUME_TYPE = "consumeType";

    public static final String KEY_IMG_FILE = "imgFile";
    public static final String KEY_TIME_STAMP = "timestamp";

    public static final String SESSION_TYPE = "app";

    public static final String KEY_APP_VERSION = "appVersion";
    public static final String KEY_STAT_CATEGORY = "statCate";
    public static final String KEY_STAT_EVENT = "statEvent";
    public static final String KEY_APP_MODEL = "appModel";
    public static final String KEY_APP_BRAND = "appBrand";
    public static final String KEY_APP_IMEI = "appImei";


    public static final String KEY_FILTER_ORDER = "filterOrder";



    /*********************************************************************************************/
    /*                                        configs                                            */
    /*********************************************************************************************/

    public static final int REQUEST_TIMEOUT = 30 * 1000;
    public static final int RESP_TOKEN_EXPIRED = 401;
    public static final int RESP_ERROR = 400;

    public static final int RESP_ERROR_CODE_FOR_LOCAL = 9999;

}

