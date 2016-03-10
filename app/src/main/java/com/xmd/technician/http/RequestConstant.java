package com.xmd.technician.http;

/**
 * Created by sdcm on 15-10-23.
 */
public class RequestConstant {

    /*********************************************************************************************/
    /*                                           urls                                            */
    /*********************************************************************************************/
    public static final String SERVER_HOST = "http://192.168.1.210:9880";

    public static final String BASE_URL = "/spa-manager/api";

    public static final String URL_LOGIN = BASE_URL + "/v1/tech/login";
    public static final String URL_LOGOUT = BASE_URL + "/v1/tech/logout";
    public static final String URL_FEEDBACK_CREATE = BASE_URL + "/v2/manager/club/feedback/create";

    public static final String URL_CLUB_INFO = BASE_URL + "/v2/manager/club/info";
    public static final String URL_CLUB_COUPON_LIST = BASE_URL + "/v2/manager/club/coupons";
    public static final String URL_CLUB_COUPON_VIEW = BASE_URL + "/v2/manager/club/coupons/view";
    public static final String URL_USER_COUPON_LIST = BASE_URL + "/v2/manager/user/coupons";
    public static final String URL_USER_COUPON_VIEW = BASE_URL + "/v2/manager/user/coupon/view";
    public static final String URL_USER_COUPON_USE = BASE_URL + "/v2/manager/user/coupon/use";
    public static final String URL_COUPON_USE_DATA = BASE_URL + "/v2/club/redpacket/consumes";

    public static final String URL_CLUB_ORDER_LIST = BASE_URL + "/v2/manager/club/order/list";
    public static final String URL_MANAGE_ORDER = BASE_URL + "/v2/manager/club/order/";
    public static final String URL_NEW_ORDER_COUNT = BASE_URL + "/v2/manager/club/newordercount";

    public static final String URL_GETUI_BIND_CLIENT_ID = BASE_URL + "/v2/push/clientid";
    public static final String URL_GETUI_UNBIND_CLIENT_ID = BASE_URL + "/v2/push/unbind/clientid";

    /*********************************************************************************************/
    /*                                           keys                                            */
    /*********************************************************************************************/

    public static final String USER_TYPE_MANAGER = "manager";

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

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_ORDER_STATUS = "status";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PAGE_SIZE = "pageSize";

    public static final String KEY_PROCESS_TYPE = "processType";
    public static final String KEY_ID = "id";
    public static final String KEY_REASON = "reason";

    public static final String KEY_COMMENTS = "comments";

    public static final String SESSION_TYPE = "app";

    public static final String KEY_APP_VERSION = "appVersion";
    public static final String KEY_STAT_CATEGORY = "statCate";
    public static final String KEY_STAT_EVENT = "statEvent";
    public static final String KEY_APP_MODEL = "appModel";
    public static final String KEY_APP_BRAND = "appBrand";
    public static final String KEY_APP_IMEI = "appImei";

    /*********************************************************************************************/
    /*                                        configs                                            */
    /*********************************************************************************************/

    public static final int REQUEST_TIMEOUT = 30 * 1000;
    public static final int RESP_TOKEN_EXPIRED = 401;
    public static final int RESP_ERROR = 400;

    public static final int RESP_ERROR_CODE_FOR_LOCAL = 9999;

}

