package com.xmd.manager.service;

/**
 * Created by sdcm on 15-10-23.
 */
public class RequestConstant {

    /*********************************************************************************************/
    /*                                           urls                                            */
    /*********************************************************************************************/

    public static final String BASE_URL = "/spa-manager/api";

    public static final String URL_LOGIN = BASE_URL + "/v1/manager/login";
    public static final String URL_LOGOUT = BASE_URL + "/v1/manager/logout";
    public static final String URL_FEEDBACK_CREATE = BASE_URL + "/v2/manager/club/feedback/create";

    public static final String URL_CLUB_INFO = BASE_URL + "/v2/manager/club/info";
    public static final String URL_CLUB_COUPON_LIST = BASE_URL + "/v2/manager/club/coupon/list";
    public static final String URL_CLUB_COUPON_VIEW = BASE_URL + "/v2/manager/club/coupons/view";

    public static final String URL_USER_COUPON_VIEW = BASE_URL + "/v2/manager/user/coupon/view";
    public static final String URL_USER_COUPON_USE = BASE_URL + "/v2/manager/user/coupon/use";
    public static final String URL_COUPON_USE_DATA = BASE_URL + "/v2/club/redpacket/consumes";

    public static final String URL_CLUB_ORDER_LIST = BASE_URL + "/v2/manager/club/order/list";
    public static final String URL_MANAGE_ORDER = BASE_URL + "/v2/manager/club/order/";
    public static final String URL_NEW_ORDER_COUNT = BASE_URL + "/v2/manager/club/newordercount";

    public static final String URL_GETUI_BIND_CLIENT_ID = BASE_URL + "/v2/push/clientid";
    public static final String URL_GETUI_UNBIND_CLIENT_ID = BASE_URL + "/v2/push/unbind/clientid";

    public static final String URL_RANKING = "/spa-manager/manager/index.html?v=%s&userType=%S&sessionType=%S&token=%S";

    public static final String URL_CLUB_PAID_ORDER_LIST = BASE_URL + "/v2/manager/club/paid_order/list";
    public static final String URL_USE_PAID_ORDER = BASE_URL + "/v2/manager/user/paid_order/use";
    public static final String URL_USER_PAID_ORDER_VIEW = BASE_URL + "/v2/manager/user/paid_order/view";
    public static final String URL_CHECK_PAID_ORDER_SWITCH = BASE_URL + "/v2/manager/paid_order/open_status";

    public static final String URL_GET_STATISTICS_HOME_DATA = BASE_URL + "/v2/manager/datastatistics/home_data";
    public static final String URL_GET_ORDER_DETAIL_DATA = BASE_URL + "/v2/manager/datastatistics/order_count";
    public static final String URL_GET_DIANZHONG_DETAIL_DATA = BASE_URL + "/v2/manager/datastatistics/coupon_paid_data";
    public static final String URL_GET_REGISTER_DETAIL_DATA = BASE_URL + "/v2/manager/datastatistics/user_register_count";

    public static final String URL_GET_REGISTER_STATISTICS_DATA = BASE_URL + "/v2/manager/datastatistics/user_count";

    public static final String URL_GET_STATISTICS_FOR_MAIN_PAGE = BASE_URL + "/v2/manager/datastatistics/index";
    public static final String URL_GET_CUSTOMERS = BASE_URL + "/v2/manager/user/register/list";

    // public static final String URL_GET_CUSTOMER_COMMENTS = BASE_URL + "/v2/manager/user/comment/list"; 3.7.1版本废弃
    public static final String URL_DELETE_CUSTOMER_COMMENT = BASE_URL + "/v2/manager/tech/comments/delete";
    public static final String URL_GET_CUSTOMER_ORDERS = BASE_URL + "/v2/manager/user/order/list";
    public static final String URL_GET_CUSTOMER_COUPONS = BASE_URL + "/v2/manager/user/coupon/list";
    public static final String URL_GET_ORDER_COUPON_VIEW = BASE_URL + "/v2/manager/user/paid_order_or_coupon/view";

    public static final String URL_GET_TECH_CUSTOMERS = BASE_URL + "/v2/manager/user/list";


    public static final String URL_GET_CLUB_LIST = BASE_URL + "/v2/manager/club/viewclubs";
    public static final String URL_LOGIN_CLUB = BASE_URL + "/v2/manager/club/viewclub/view";

    public static final String URL_CHECK_COUPON = BASE_URL + "/v2/financial/authcode/check";
    public static final String URL_TO_PAY = BASE_URL + "/v2/financial/user/pay/save";
    public static final String URL_TO_PAY_BY_CONSUME = BASE_URL + "/v2/financial/account/payforother/check";

    public static final String URL_APP_UPDATE_CONFIG = "/app-upgrade-system/appUpgrade";

    public static final String URL_CLUB_MENU_CONFIG = BASE_URL + "/v2/manager/current/menu/list";

    public static final String URL_WIFI_REPORT_DATA_DETAIL = BASE_URL + "/v2/manager/datastatistics/wifi/report";
    public static final String URL_VISITOR_REPORT_DATA_DETAIL = BASE_URL + "/v2/manager/datastatistics/visit/report";
    public static final String URL_REGISTER_REPORT_DATA_DETAIL = BASE_URL + "/v2/manager/datastatistics/user/register/report";
    public static final String URL_COUPON_REPORT_DATA_DETAIL = BASE_URL + "/v2/manager/datastatistics/coupon/data";
    public static final String URL_GET_VISITOR_LIST_DATA = BASE_URL + "/v2/manager/datastatistics/visit/list";
    public static final String URL_GET_GROUP_MESSAGE_LIST = BASE_URL + "/v2/manager/group/message/list";
    public static final String URL_GET_GROUP_MESSAGE_EDIT_INFO = BASE_URL + "/v2/manager/group/message/edit/info";
    public static final String URL_GROUP_MESSAGE_SEND = BASE_URL + "/v2/manager/group/message/send";
    public static final String URL_GROUP_MESSAGE_STAT_SWITCH = BASE_URL + "/v2/manager/group/message/stat/switch";
    public static final String URL_GET_USER_REGISTER_LIST = BASE_URL + "/v2/manager/user/customer/list";
    /**
     * 电子期刊
     */
    public static final String URL_JOURNAL_LIST = BASE_URL + "/v2/manager/journal/list";
    public static final String URL_JOURNAL_CONTENT = BASE_URL + "/v2/manager/journal/detail";
    public static final String URL_JOURNAL_CONTENT_TYPES = BASE_URL + "/v2/manager/journal/items";
    public static final String URL_JOURNAL_UPDATE_STATUS = BASE_URL + "/v2/manager/journal/status/update";
    public static final String URL_JOURNAL_SAVE_CONTENT = BASE_URL + "/v2/manager/journal/save";
    public static final String URL_JOURNAL_TEMPLATE_LIST = BASE_URL + "/v2/manager/journal/template/list";
    public static final String URL_JOURNAL_ITEM_BASE_LIST = BASE_URL + "/v2/manager/service/items/base/list";
    public static final String URL_TECHNICIAN_LIST = BASE_URL + "/v2/manager/tech/base/list";
    public static final String URL_GET_STATISTICS_WIFI_DATA = BASE_URL + "/v2/manager/datastatistics/wifi_data";//3.7.1废弃
    public static final String URL_GET_STATISTICS_COUPON_DATA_INDEX = BASE_URL + "/v2/manager/datastatistics/coupon_data_index";
    public static final String URL_GET_STATISTICS_REGISTRY_DATA = BASE_URL + "/v2/manager/datastatistics/registry_data";
    public static final String URL_GET_STATISTICS_CLUB_VISIT_DATA = BASE_URL + "/v2/manager/datastatistics/club_visit_data";
    public static final String URL_GET_STATISTICS_TECH_RANK_INDEX = BASE_URL + "/v2/manager/datastatistics/tech_rank_index";
    public static final String URL_GET_STATISTICS_ORDER_DATA = BASE_URL + "/v2/manager/datastatistics/order_data";
    public static final String URL_GET_CUSTOMER_VIEW = BASE_URL + "/v2/manager/user/view";
    public static final String URL_GET_CLUB_TECH_LIST = BASE_URL + "/v2/manager/club/tech/list";
    public static final String URL_GET_CLUB_FAVOURABLE_ACTIVITY = BASE_URL + "/v2/manager/group/message/activity/list";


    public static final String URL_TECHNICIAN_RANKING_LIST = BASE_URL + "/v2/manager/ranking_list";
    public static final String URL_GET_MARKETING_ITEMS = BASE_URL + "/v2/manager/club/marketing/items";

    public static final String URL_USER_BAD_COMMENT_STATUS_UPDATE = BASE_URL + "/v2/manager/user/badcomment/status/update";
    public static final String URL_GET_TECH_BAD_COMMENT_DATA = BASE_URL + "/v2/manager/tech/badcomment/data";
    public static final String URL_CHANGE_USER_PASSWORD = BASE_URL + "/v1/profile/manager/password/modify";
    public static final String URL_JOURNAL_UPLOAD_PHOTO = BASE_URL + "/v2/manager/journal/album/upload";
    public static final String URL_JOURNAL_PHOTO_IDS_TO_URLS = BASE_URL + "/v2/manager/journal/album/urls";
    public static final String URL_JOURNAL_DELETE_PHOTO = BASE_URL + "/v2/manager/journal/album/delete";
    public static final String URL_JOURNAL_COUPON_ACTIVITIES = BASE_URL + "/v2/manager/journal/activities";
    public static final String URL_JOURNAL_VIDEO_CONFIG = BASE_URL + "/v2/manager/qcloud/video/info";
    public static final String URL_JOURNAL_VIDEO_UPLOAD_SIGN = BASE_URL + "/v2/manager/qcloud/video/sign";
    public static final String URL_JOURNAL_DELETE_VIDEO = BASE_URL + "/v2/manager/qcloud/video/file/delete";
    public static final String URL_JOURNAL_VIDEO_DETAIL = BASE_URL + "/v2/manager/qcloud/video/detail";
    public static final String URL_JOURNAL_ARTICLES = BASE_URL + "/v2/manager/journal/articles";
    public static final String URL_JOURNAL_ARTICLE_DETAIL = BASE_URL + "/v2/user/journal/article";
    public static final String URL_JOURNAL_IMAGE_ARTICLE_TEMPLATE = BASE_URL + "/v2/manager/journal/news/template";

    //核销
    public static final String URL_USER_COUPON_LIST = BASE_URL + "/v2/manager/user/coupons";
    public static final String URL_CHECK_INFO = BASE_URL + "/v2/manager/checkinfo/info/{number}";
    public static final String URL_CHECK_INFO_TYPE_GET = BASE_URL + "/v2/manager/checkinfo/type/get";
    public static final String URL_GET_PAY_ORDER_DETAIL = BASE_URL + "/v2/manager/checkinfo/paid_order/detail";//订单详情
    public static final String URL_GET_COUPON_DETAIL = BASE_URL + "/v2/manager/checkinfo/coupon/detail";//优惠券详情
    public static final String URL_SERVICE_ITEM_COUPON_DETAIL = BASE_URL + "/v2/manager/checkinfo/service_item_coupon/detail";//项目券
    public static final String URL_GET_VERIFICATION_COMMON_DETAIL = BASE_URL + "/v2/manager/checkinfo/common/detail";
    public static final String URL_VERIFICATION_COMMON_SAVE = BASE_URL + "/v2/manager/checkinfo/common/save";
    public static final String URL_CHECK_INFO_ORDER_SAVE = BASE_URL + "/v2/manager/checkinfo/paid_order/save";
    public static final String URL_CHECK_INFO_COUPON_SAVE = BASE_URL + "/v2/manager/checkinfo/coupon/save";
    public static final String URL_CHECK_INFO_SERVICE_ITEM_COUPON_SAVE = BASE_URL + "/v2/manager/checkinfo/service_item_coupon/save";
    public static final String URL_CHECK_INFO_LUCKY_WHEEL_DETAIL = BASE_URL + "/v2/manager/checkinfo/lucky_wheel/detail";
    public static final String URL_CHECK_INFO_AWARD_SAVE = BASE_URL + "/v2/manager/checkinfo/lucky_wheel/save";
    public static final String URL_GET_DATASTATISTICS_INDEX_ORDER_DATA = BASE_URL + "/v2/manager/datastatistics/index_order_data";
    /**
     * 群发消息，用户分组
     */
    public static final String URL_GET_GROUP_LIST = BASE_URL + "/v2/manager/group/list";
    public static final String URL_DO_GROUP_SAVE = BASE_URL + "/v2/manager/group/save";
    public static final String URL_GET_GROUP_USER_LIST = BASE_URL + "/v2/manager/group/user/list";
    public static final String URL_DO_GROUP_DELETE = BASE_URL + "/v2/manager/group/delete";
    public static final String URL_GET_GROUP_DETAILS = BASE_URL + "/v2/manager/group/details";
    public static final String URL_DO_USER_ADD_GROUP = BASE_URL + "/v2/manager/group/user/addGroup";
    public static final String URL_DO_GROUP_USER_EDIT_GROUP = BASE_URL + "/v2/manager/group/user/editGroup";
    public static final String URL_DO_GROUP_MESSAGE_ALBUM_UPLOAD = BASE_URL + "/v2/manager/group/message/album/upload";
    public static final String URL_GET_GROUP_TAG_LIST = BASE_URL + "/v2/manager/group/message/tag/list";
    /**
     * 自动领取优惠券
     */
    public static final String URL_GET_CLUB_USER_GET_COUPON = BASE_URL + "/v2/club/user/get/coupon";
    /**
     * 核销记录
     */
    public static final String URL_GET_CHECK_INFO_TYPE_LIST = BASE_URL + "/v2/manager/checkinfo/type/list";
    public static final String URL_GET_CHECK_INFO_RECORD_LIST = BASE_URL + "/v2/manager/checkinfo/record/list";
    public static final String URL_GET_CHECK_INFO_RECORD_DETAIL = BASE_URL + "/v2/manager/checkinfo/record/detail";
    /**
     * 评价标签修改
     */
    public static final String URL_GET_APP_COMMENT_LIST = BASE_URL + "/v2/comment/app/comment/list";
    public static final String URL_GET_USER_BAD_COMMENT_LIST = BASE_URL + "/v2/comment/app/badcomment/list";
    public static final String URL_GET_BAD_COMMENT_DETAIL = BASE_URL + "/v2/comment/app/badcomment/detail";
    /**
     * 管理者首页数据展示
     */
    public static final String URL_GET_MANAGER_DATA_STATISTICS_WIFI_DATA = BASE_URL + "/v2/manager/datastatistics/wifi/data";
    public static final String URL_GET_MANAGER_DATA_STATISTICS_ACCOUNT_DATA = BASE_URL + "/v2/manager/datastatistics/account_data";
    public static final String URL_GET_MANAGER_FASTPAY_ORDER_LIST = BASE_URL + "/v2/manager/fastpay/order/list";
    public static final String URL_GET_DATA_STATISTICS_SALE_DATA = BASE_URL + "/v2/manager/datastatistics/sale_data";
    /**
     * PK排行榜
     */
    public static final String URL_GET_TECH_PK_ACTIVITY_RANKING = BASE_URL + "/v1/tech/pk/activity/ranking/index";
    public static final String URL_GET_TECH_PK_ACTIVITY_LIST = BASE_URL + "/v1/tech/pk/activity/list";
    public static final String URL_GET_PK_TEAM_RANKING_LIST = BASE_URL + "/v1/tech/pk/activity/team/ranking/list";
    public static final String URL_GET_PK_PERSONAL_RANKING_LIST = BASE_URL + "/v1/tech/pk/activity/personal/ranking/list";
    public static final String URL_GET_PERSONAL_RANKING_LIST = BASE_URL + "/v2/manager/ranking_list";
    /**
     * 消息发送成功后回调
     */
    public static final String URL_SAVE_CONTACT_MARK_CHATTO_USER = BASE_URL + "/v1/emchat/markchattouser";

    public static final String URL_COMMENT_LIST = BASE_URL + "/v2/comment/list";

    /**
     * 订单优化
     */
    public static final String URL_GET_ORDER_SUMMARY_DATA = BASE_URL + "/v2/manager/datastatistics/order_count_data";
    public static final String URL_GET_CLUB_TECH_ORDER_RANK = BASE_URL + "/v2/manager/club/tech/order_rank";
    public static final String URL_GET_CLUB_SELECT_TECHNICIAN_LIST = BASE_URL + "/v2/manager/club/select/technician/list";
    public static final String URL_GET_CLUB_SELECT_ITEM_LIST = BASE_URL + "/v2/manager/club/select/item/list";
    /**
     * 运营报表
     */
    public static final String URL_CLUB_FINANCIAL_REPORT_CUSTOMER = BASE_URL + "/v2/club/financial/report/custom";
    public static final String URL_CLUB_FINANCIAL_REPORT_DELETE = BASE_URL + "/v2/club/financial/report/delete";
    public static final String URL_GET_CLUB_FINANCIAL_REPORT = BASE_URL + "/v2/club/financial/report";
    public static final String URL_CLUB_FINANCIAL_REPORT_CONFIG = BASE_URL + "/v2/club/financial/report/config";
    public static final String URL_GET_CLUB_FINANCIAL_REPORT_NEWS = BASE_URL + "/v2/club/financial/report/news";
    public static final String URL_GET_CLUB_FINANCIAL_REPORT_BY_ID = BASE_URL + "/v2/club/financial/report/read/{id}";
    /**
     * 优惠券改版
     */
    public static final String URL_GET_USE_EXPIRE_COUNT_TOTAL = BASE_URL + "/v2/manager/datastatistics/coupon/get_use_expire/total/count";
    public static final String URL_GET_USE_EXPIRE_COUNT_LIST = BASE_URL + "/v2/manager/datastatistics/coupon/get_use_expire/count";
    public static final String URL_GET_MANAGER_COUPON_RECORD = BASE_URL + "/v2/manager/coupon/record";
    public static final String URL_GET_ONLINE_COUPON_LIST = BASE_URL + "/v2/manager/club/online/coupon/list";


    /**
     * 管理者报表
     */
    // 技师工资
    public static final String URL_GET_COMMISSION_SUM_LIST = BASE_URL + "/v2/manager/native/commission/sum/data";   //获取指定时间段会所所有技师工资汇总列表
    public static final String URL_GET_COMMISSION_SUM_AMOUNT = BASE_URL + "/v2/manager/native/commission/tech/all/sum"; //获取指定时间段会所提成汇总金额
    public static final String URL_GET_ALL_TECH_COMMISSION_LIST = BASE_URL + "/v2/manager/native/commission/tech/list"; //获取具体某天会所所有技师提成列表
    public static final String URL_GET_TECH_COMMISSION_AMOUNT = BASE_URL + "/v2/manager/native/commission/tech/detail/sum"; //获取具体某天某技师提成汇总金额
    public static final String URL_GET_TECH_COMMISSION_DETAIL_LIST = BASE_URL + "/v2/manager/native/commission/tech/detail/records"; //获取具体某天某技师提成明细列表
    public static final String URL_GET_TECH_COMMISSION_DETAIL_INFO = BASE_URL + "/v2/manager/native/commission/detail"; //获取提成明细的具体详情

    // 买单收银
    public static final String URL_GET_CASHIER_SUM_LIST = BASE_URL + "/v2/manager/native/statistic/summary/day";    //获取指定时间段会所买单收银按日汇总
    public static final String URL_GET_CLUB_CASHIER_DETAIL_LIST = BASE_URL + "/v2/manager/native/statistic/item/details";   //获取具体某天会所买单详情列表

    /**********************************************************************dengyixia***********************/
    /*                                           keys                                            */
    /*********************************************************************************************/

    public static final String USER_TYPE_MANAGER = "manager";
    public static final String USER_TYPE_TECH = "tech";
    public static final String KEY_USER_TYPE = "userType";

    public static final String KEY_TYPE = "type";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";

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
    public static final String KEY_ORDER_NO = "orderNo";

    public static final String KEY_PHONE_NUMBER = "phoneNum";

    public static final String KEY_NUMBER = "number";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_ORDER_STATUS = "status";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PAGE_SIZE = "pageSize";
    public static final String KEY_SEARCH_TELEPHONE = "telephone";

    public static final String KEY_PROCESS_TYPE = "processType";
    public static final String KEY_ID = "id";
    public static final String KEY_REASON = "reason";

    public static final String KEY_COMMENTS = "comments";

    public static final String KEY_COMMENT_ID = "commentId";

    public static final String SESSION_TYPE = "app";

    public static final String KEY_APP_VERSION = "appVersion";
    public static final String KEY_STAT_CATEGORY = "statCate";
    public static final String KEY_STAT_EVENT = "statEvent";
    public static final String KEY_APP_MODEL = "appModel";
    public static final String KEY_APP_BRAND = "appBrand";
    public static final String KEY_APP_IMEI = "appImei";
    public static final String KEY_CUSTOMER_USERNAME = "userName";
    public static final String KEY_UPDATE_APP_ID = "appId";
    public static final String KEY_UPDATE_VERSION = "version";
    public static final String KEY_UPDATE_USER_ID = "userId";

    public static final String KEY_CLUB_NAME = "clubName";
    public static final String KEY_LOGIN_CHANEL = "loginChanel";
    public static final String KEY_CLUB_ID = "clubId";

    public static final String CLUB_ANDROID_CHANEL = "android_viewclubs";

    public static final String KEY_CONSUME_CODE = "consume_code";
    public static final String KEY_PAY_AMOUNT = "amount";
    public static final String KEY_PAY_CODE = "code";
    public static final String KEY_PAY_AMOUNT_BY_CODE = "usedAmount";
    public static final String KEY_PAY_QRNO = "qrNo";
    public static final String KEY_PAY_RID = "rid";
    public static final String KEY_TIME = "time";

    public static final String KEY_SORT = "sort";
    public static final String KEY_SORT_TYPE = "sortType";
    public static final String KEY_TECH_ID = "techId";

    public static final String KEY_WIFI = "wifi";
    public static final String KEY_LINK = "link";
    public static final String KEY_CLUB_QR_CODE = "club_qrcode";
    public static final String KEY_TECH_QR_CODE = "tech_qrcode";
    public static final String KEY_9358 = "9358";

    public static final String KEY_GROUP_GROUP_MEAAGE_ID = "id";
    public static final String KEY_GROUP_ACT_ID = "actId";
    public static final String KEY_GROUP_ACT_NAME = "actName";
    public static final String KEY_GROUP_COUPON_CONTENT = "couponContent";
    public static final String KEY_GROUP_MESSAGE_CONTENT = "messageContent";
    public static final String KEY_GROUP_USER_GROUP_TYPE = "userGroupType";
    public static final String KEY_GROUP_SUB_GROUP_LABELS = "subGroupLabels";
    public static final String KEY_GROUP_MESSAGE_TYEP = "msgType";
    public static final String VALUE_TYPE_ACTIVE = "active";
    public static final String VALUE_TYPE_UNATIVE = "unactive";
    public static final String VALUE_TYPE_SPECIFIED = "specified";
    public static final String KEY_GROUP_IDS = "groupIds";
    public static final String KEY_GROUP_IMAGE_ID = "imageId";

    public static final String KEY_GROUP_MESSAGE = "message";
    public static final String KEY_GROUP_SEND_COUNT = "sendCount";
    public static final String KEY_GROUP_USER_TYPE = "userType";
    public static final String GROUP_ACTIVE_TYPE = "activeUser";
    public static final String GROUP_ALL_TYPE = "allUser";
    public static final String GROUP_UNACTIVE_TYPE = "unactiveUser";

    public static final String KEY_SELECTED_START_TIME = "startTime";
    public static final String KEY_SELECTED_END_TIME = "endTime";
    public static final String KEY_MAIN_TITLE = "title";
    public static final String KEY_LIMIT_TIME = "limitTime";

    public static final String KEY_CODE_PAY_TYPR = "pay_for_other";
    public static final String KEY_CODE_ORDER_TYPE = "order";
    public static final String KEY_CODE_PAID_SERVICE_TYPE = "paid_service_item";
    public static final String KEY_CODE_COUPON_TYPE = "coupon";
    public static final String KEY_MESSAGE_TYPE_YUAN = "oneYuan";
    public static final String KEY_MESSAGE_TYPE_TIME_LIMIT = "timeLimit";
    public static final String KEY_MESSAGE_TYPE_LUCKY_WHEEL = "lucky_wheel";
    public static final String KEY_MESSAGE_TYPE_ORDINARY_COUPON = "ordinaryCoupon";
    public static final String KEY_ONE_YUAN = "one_yuan";
    public static final String KEY_TIME_CARD = "item_card";


    /**
     * 电子期刊
     */
    public static final String KEY_JOURNAL_ID = "journalId";
    public static final String KEY_STATUS = "status";
    public static final String KEY_TITLE = "title";
    public static final String KEY_SUB_TITLE = "subTitle";
    public static final String KEY_JOURNAL_ITEMS = "journalItems";
    public static final String KEY_TEMPLATE_ID = "templateId";
    public static final String KEY_IMG_FILE = "imgFile";
    public static final String KEY_ALBUMIDS = "albumIds";
    public static final String KEY_IMAGE_ID = "imageId";
    public static final String KEY_RESOURCE_PATH = "resourcePath";

    /**
     * ADMIN_PC-管理者PC;ADMIN_APP-管理者APP
     */
    public static final String KEY_PLATFORM = "platform";
    public static final String PLATFORM_TYPE_ADMIN_APP = "ADMIN_APP";

    /**
     * 首页改版
     */
    public static final String KEY_OLD_PASSWORD = "oldPassword";
    public static final String KEY_NEW_PASSWORD = "newPassword";
    public static final String KEY_COMMENT_STATUS = "status";
    public static final String KEY_BAD_COMMENT_ID = "commentId";
    public static final String KEY_IS_DELETED = "isDeleted";
    public static final String KEY_DATE = "date";
    public static final String VALID_COMMENT = "valid";//未回访
    public static final String INDEX_COMMENT = "index";//首页
    public static final String FINISH_COMMENT = "finish";//已回访

    public static final String DELETE_COMMENT = "delete";
    public static final String COMMENT_TYPE_ORDER = "order";
    public static final String COMMENT_TYPE_TECH_QRCODE = "tech_qrcode";
    public static final String COMMENT_TYPE_TECH = "tech";
    public static final String COMMENT_TYPE_CLUB = "club";
    public static final String COMMENT_USER_ID = "userId";
    public static final String COMMENT_SORT_COUNT = "count";//差评数排序
    public static final String COMMENT_SORT_RATE = "rate";//差评率排序
    /**
     * 核销优化
     */
    public static final String KEY_VERIFICATION_OBJECT = "verification_object";
    public static final String KEY_VERIFICATION_CODE = "code";
    public static final String KEY_VERIFICATION_TYPE = "type";
    public static final String KEY_VERIFICATION_PAY_ORDER_NO = "orderNo";
    public static final String KEY_VERIFICATION_COUPON_NO = "couponNo";
    public static final String KEY_VERIFICATION_AMOUNT = "amount";
    public static final String KEY_PAY_ORDER_PROCESS_TYPE = "processType";
    public static final String ORDER_VERIFICATION_EXPIRE = "expire";
    public static final String ORDER_VERIFICATION_VERIFIED = "verified";
    public static final String KEY_VERIFICATION_VERIFY_CODE = "verifyCode";
    public static final String KEY_VERIFICATION_RECORD_ID = "recordId";
    public static final String KEY_IS_TIME = "isTime";
    public static final String KEY_VERIFICATION_SOME = "verification_some";
    /**
     * 用户分组
     */
    public static final String KEY_GROUP_DESCRIPTION = "description";
    public static final String KEY_GROUP_ID = "groupId";
    public static final String KEY_GROUP_NAME = "groupName";
    public static final String KEY_GROUP_USER_ID = "userIds";
    public static final String KEY_GROUP_USER_NAME = "userName";
    public static final String KEY_GROUP_IMG_FILE = "imgFile";
    /**
     * 自动领券
     */
    public static final String KEY_USER_COUPON_ACT_ID = "actId";
    public static final String KEY_USER_COUPON_CHANEL = "chanel"; //tech,manager,qrCode,link,index
    public static final String KEY_USER_COUPON_EMCHAT_ID = "emchatId";
    public static final String KEY_USER_COUPON_INFO = "couponInfo";
    public static final String KEY_USER_COUPON_EMCHAT_MESSAGE = "messageContent";

    //报表
    public static final String KEY_REQUEST_TYPE = "requestType";
    public static final String KEY_EVENT_TYPE = "eventType";
    public static final String KEY_SCOPE = "scope";


    /*********************************************************************************************/
    /*                                        configs                                            */
    /*********************************************************************************************/
    public static final int REQUEST_TIMEOUT = 30 * 1000;

    public static final int RESP_OK = 200;
    public static final int RESP_OK_NO_CONTENT = 204;

    public static final int RESP_TOKEN_EXPIRED = 401;
    public static final int RESP_ERROR = 400;

    public static final int RESP_ERROR_CODE_FOR_LOCAL = 9999;
    public static final int PAY_RESULT_ERROR_CODE = 500;

    public static final String RESP_STATUS_FAIL = "fail";

    /**
     * 技师排行榜
     */
    public static final String TECHNICIAN_RANKING_TYPE_USER = "userList";
    public static final String TECHNICIAN_RANKING_TYPE_COMMENT = "commentList";
    public static final String TECHNICIAN_RANKING_TYPE_SALE = "paidList";
    /**
     * 在线买单
     */
    public static final String KEY_ONLINE_PAY_TECH_NAME = "techName";
    public static final String KEY_IS_SEARCH = "isSearch";//0表示不是查找，1表示是查找
    /**
     * PK排行榜
     */
    public static final String KEY_SORT_BY_COMMENT = "commentStat";
    public static final String KEY_SORT_BY_CUSTOMER = "customerStat";
    public static final String KEY_SORT_BY_SALE = "saleStat";
    public static final String KEY_SORT_BY_COUPON = "couponStat";
    public static final String KEY_SORT_BY_PANIC = "paidServiceItemStat";
    public static final String KEY_PK_ACTIVITY_ID = "pkActivityId";
    public static final String KEY_SORT_KEY = "sortKey";
    public static final String KEY_TECH_SORT_BY_USER = "userList";
    public static final String KEY_TECH_SORT_BY_COMMENT = "commentList";
    public static final String KEY_TECH_SORT_BY_PAID = "paidList";
    public static final String KEY_TECH_RANKING_SOR_TYPE = "type";
    public static final String KEY_SELECT = "select";
    public static final String KEY_SELECT_BY_DAY = "day";
    public static final String KEY_SELECT_BY_WEEK = "week";
    public static final String KEY_SELECT_BY_MONTH = "month";
    public static final String KEY_TEAM_ID = "teamId";
    /**
     * 环信消息回调
     */
    public static final String USER_TYPE_USER = "user";
    public static final String KEY_MSG_TYPE_TEXT = "text";
    public static final String KEY_CURRENT_CHAT_ID = "currentChatId";
    public static final String KEY_CURRENT_USER_TYPE = "currentUserType";
    public static final String KEY_FRIEND_CHAT_ID = "friendChatId";
    public static final String KEY_FRIEND_USER_TYPE = "friendUserType";
    public static final String KEY_FRIEND_MESSAGE_TYPE = "msgType";
    public static final String KEY_CHAT_MSG_ID = "msgId";

    /**
     * 订单优化
     */
    public static final String KEY_ORDER_FILTER_START_DATE = "startDate";
    public static final String KEY_ORDER_FILTER_END_DATE = "endDate";
    public static final String KEY_ORDER_FILTER_ITEM_ID = "itemId";
    public static final String KEY_ORDER_FILTER_STATUS = "status";
    public static final String KEY_ORDER_FILTER_TECH_ID = "techId";
    public static final String KEY_ORDER_FILTER_TELEPHONE = "telephone";

    /**
     * 自定义报表
     */

    public static final String KEY_REPORT_CUSTOM_START_TIME = "startTime";
    public static final String KEY_REPORT_CUSTOM_END_TIME = "endTime";
    public static final String KEY_REPORT_CUSTOM_NAME = "name";
    public static final String KEY_REPORT_DATE = "date";
    public static final String KEY_REPORT_TYPE = "type";

    /**
     * 优惠券优化
     */

    public static final String KEY_COUPON_ID = "couponId";
    public static final String KEY_COUPON_START_DATE = "startDate";
    public static final String KEY_COUPON_END_DATE = "endDate";
    public static final String KEY_COUPON_START_TIME = "startTime";
    public static final String KEY_COUPON_END_TIME = "endTime";
    public static final String KEY_COUPON_TIME_TYPE = "timeType";
    public static final String KEY_COUPON_STATUS = "status";
    public static final String KEY_COUPON_PHONE_NUM_OR_COUPON_NO = "phoneNumOrCouponNo";
    public static final String KEY_COUPON_SEARCH_MARK = "searchMark";
    public static final String KEY_COUPON_TYPE = "couponType";
    public static final String KEY_COUPON_ONLINE = "online";
    public static final String KEY_COUPON_LIST_TYPE = "listType";

}

