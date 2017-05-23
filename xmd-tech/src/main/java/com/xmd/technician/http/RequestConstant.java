package com.xmd.technician.http;


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
    public static final String URL_LOGIN_BY_TECH_NO = BASE_URL + "/v1/spare_tech/login";
    public static final String URL_LOGOUT = BASE_URL + "/v1/tech/logout";
    public static final String URL_REGISTER = BASE_URL + "/v1/tech/register";
    public static final String URL_EDIT_INFO = BASE_URL + "/v1/profile/tech/editInfo";
    public static final String URL_UPDATE_TECH_INFO = BASE_URL + "/v1/profile/tech";
    public static final String URL_CURRENT_INFO = BASE_URL + "/v1/profile/tech/current";
    public static final String URL_COMMENT_LIST = BASE_URL + "/v1/profile/tech/comments";
    public static final String URL_TECH_PERSONAL_DATA = BASE_URL + "/v2/tech/profile/notice";
    public static final String URL_MODIFY_PASSWORD = BASE_URL + "/v1/profile/tech/modifyPassword";
    public static final String URL_RESET_PASSWORD = BASE_URL + "/v1/tech/resetPassword";
    public static final String URL_FEEDBACK_CREATE = BASE_URL + "/v2/tech/feedback/create";
    public static final String URL_GET_ORDER_LIST = BASE_URL + "/v2/tech/profile/orders";
    public static final String URL_HIDE_ORDER = BASE_URL + "/v2/tech/hide/order";
    public static final String URL_GETUI_BIND_CLIENT_ID = BASE_URL + "/v2/push/clientid";
    public static final String URL_GETUI_UNBIND_CLIENT_ID = BASE_URL + "/v2/push/unbind/clientid";
    public static final String URL_GET_ICODE = BASE_URL + "/v1/appVerifyCode";
    public static final String URL_JOIN_CLUB = BASE_URL + "/v1/profile/tech/club/change";
    public static final String URL_GET_WORKTIME = BASE_URL + "/v1/profile/tech/calendar";
    public static final String URL_UPDATE_WORKTIME = BASE_URL + "/v1/profile/tech/calendar/update";
    public static final String URL_DELETE_ALBUM = BASE_URL + "/v1/profile/tech/album/delete";
    public static final String URL_UPLOAD_ALBUM = BASE_URL + "/v1/profile/tech/album/base64/upload";
    public static final String URL_SORT_ALBUM = BASE_URL + "/v1/profile/tech/album/sort";
    public static final String URL_UPLOAD_AVATAR = BASE_URL + "/v1/profile/tech/avatar/base64/upload";


    public static final String URL_QUIT_CLUB = BASE_URL + "/v2/tech/check/password";
    public static final String URL_GET_SERVICE_LIST = BASE_URL + "/v2/tech/profile/service";
    public static final String URL_UPDATE_SERVICE_LIST = BASE_URL + "/v1/profile/tech/service/update";
    public static final String URL_GET_ACCOUNT_MONEY = BASE_URL + "/v1/profile/tech/money";
    public static final String URL_CONSUME_DETAIL = BASE_URL + "/v2/tech/profile/account/details";

    public static final String URL_GET_COUPON_LIST = BASE_URL + "/v1/profile/redpack/list";
    public static final String URL_GET_COUPON_INFO = BASE_URL + "/v1/profile/club/redpacketinfo";
    public static final String URL_COUPON_SHARE_QR_CODE = BASE_URL + "/v1/profile/redpacket/useqrcode";
    public static final String URL_PAID_COUPON_USER_DETAIL = BASE_URL + "/v1/profile/tech/coupon/userdetails";
    public static final String URL_ADD_CUSTOMER = BASE_URL + "/v2/tech/customer/edit";
    public static final String URL_GET_CUSTOMER_LIST = BASE_URL + "/v2/tech/customer/user/list";
    public static final String URL_GET_CUSTOMER_INFO_DETAIL = BASE_URL + "/v2/tech/customer/user/view";
    public static final String URL_GET_CLUB_LIST = BASE_URL + "/v2/tech/customer/tech/list";
    public static final String URL_GET_TECH_INFO_DETAIL = BASE_URL + "/v2/tech/customer/tech/view";
    public static final String URL_GET_MANAGER_INFO_DETAIL = BASE_URL + "/v2/tech/customer/manager/view";
    public static final String URL_GET_RECENTLY_VISITOR_LIST = BASE_URL + "/v2/tech/customer/recent/list";
    public static final String URL_DELETE_CONTACT = BASE_URL + "/v2/tech/customer/delete";
    public static final String URL_GET_USER_IS_BIND_WX = BASE_URL + "/v2/tech/is_bind_wx";
    public static final String URL_GAME_DICE_SUBMIT = BASE_URL + "/v2/credit/game/dice/submit";
    public static final String URL_GAME_DICE_ACCEPT_OR_REJECT = BASE_URL + "/v2/credit/game/dice/accept";
    public static final String URL_GET_CREDIT_GIFT_LIST = BASE_URL + "/v2/credit/gift/list";
    public static final String URL_CUSTOMER_SAY_HI = BASE_URL + "/v2/tech/customer/sayhi";
    public static final String URL_CUSTOMER_VIEW_VISIT = BASE_URL + "/v2/tech/customer/user/view/visit";


    public static final String URL_GET_CREDIT_SWITCH_STATUS = BASE_URL + "/v2/credit/switch/status";
    public static final String URL_GET_CREDIT_EXCHANGE_APPLY = BASE_URL + "/v2/credit/exchange/apply";
    public static final String URL_GET_CREDIT_USER_RECORDS = BASE_URL + "/v2/credit/user/records";
    public static final String URL_GET_CREDIT_USER_ACCOUNT = BASE_URL + "/v2/credit/user/account";
    public static final String URL_GET_CREDIT_USER_EXCHANGE_APPLICATIONS = BASE_URL + "/v2/credit/user/exchange/applications";
    public static final String URL_GET_CONTACT_MARK = BASE_URL + "/v2/club/impression/list";
    public static final String URL_INTRODUCE_BIND = "/spa-manager/follow9358/index.html";

    public static final String URL_SAVE_CONTACT_MARK_CHATTO_USER = BASE_URL + "/v1/emchat/markchattouser";


    public static final String URL_GET_USER_SWITCHES = BASE_URL + "/v2/user/switches";
    public static final String URL_COUPON_SHARE_EVENT_COUNT = BASE_URL + "/v1/profile/redpacket/share";
    public static final String URL_RANKING = "/spa-manager/manager/index.html?v=%s&userType=%s&sessionType=%s&token=%s";

    public static final String URL_GET_TECH_INFO = BASE_URL + "/v2/tech/current";
    public static final String URL_GET_TECH_ORDER_LIST = BASE_URL + "/v2/tech/order/list";
    public static final String URL_GET_TECH_STATISTICS_DATA = BASE_URL + "/v2/tech/statisticsData";
    public static final String URL_UPDATE_WORK_STATUS = BASE_URL + "/v1/profile/tech/status/update";
    public static final String URL_MANAGE_ORDER = BASE_URL + "/v2/tech/profile/order/manage";
    public static final String URL_GET_TECH_RANK_INDEX = BASE_URL + "/v2/tech/tech_rank_index";
    public static final String URL_GET_RECENT_DYNAMICS_LIST = BASE_URL + "/v2/tech/recent/dynamics/list";
    public static final String URL_ORDER_INNER_READ = BASE_URL + "/v2/tech/order/inner/read";
    public static final String URL_GET_PROFILE_TECH_ACCOUNT_LIST = BASE_URL + "/v1/profile/tech/account/list";


    //    public static final String URL_APP_UPDATE_CONFIG = "mockjs/17/app-upgrade-system/appUpgrade";
    public static final String URL_APP_UPDATE_CONFIG = "/app-upgrade-system/appUpgrade";

    public static final String URL_GET_UNUSED_TECH_NO = BASE_URL + "/v1/get/tech_no";
    /**
     * 自动领取优惠券
     */
    public static final String URL_GET_CLUB_USER_GET_COUPON = BASE_URL + "/v2/club/user/get/coupon";

    //买单记录查询
    public static final String URL_GET_PAY_NOTIFY_LIST = BASE_URL + "/v2/tech/fastpay/order/list";
    public static final String URL_CHECK_PAY_NOTIFY = BASE_URL + "/v2/club/order/notify/check";

    //技师功能权限
    public static final String URL_GET_SWITCH_STATUS = BASE_URL + "/v1/club/switch/status/get";
    /**
     * 营销列表
     */
    public static final String URL_GET_CARD_SHARE_LIST_INFO = BASE_URL + "/v1/techshare/cardListInfo";        //卡券列表
    public static final String URL_GET_ACTIVITY_LIST_INFO = BASE_URL + "/v1/techshare/activityListInfo";     //热门活动列表
    public static final String URL_GET_PROPAGANDA_LIST_INFO = BASE_URL + "/v1/techshare/propagandaListInfo";//会所宣传列表
    public static final String URL_GET_CARD_LIST_DETAIL = BASE_URL + "/v1/techshare/cardListDetail";       //点钟券、优惠券列表
    public static final String URL_GET_ONCE_CARD_LIST_DETAIL = BASE_URL + "/v2/club/item_card/activity/list";//次卡列表
    public static final String URL_GET_SERVICE_ITEM_LIST = BASE_URL + "/v1/techshare/serviceItemListDetail";    //限时抢列表
    public static final String URL_GET_PAY_FOR_ME_LIST = BASE_URL + "/v1/techshare/oneYuanListDetail";//谁替我买单列表
    public static final String URL_GET_CLUB_JOURNAL_LIST_DETAIL = BASE_URL + "/v1/techshare/journalListDetail";//会所期刊列表
    public static final String URL_GET_REWARD_ACTIVITY_LIST = BASE_URL + "/v1/techshare/darwActListDetail";//抽奖活动列表
    public static final String URL_DO_USER_JOURNAL_SHARE_COUNT = BASE_URL + "/v2/user/journal/share/count";//期刊统计
    /**
     * PK排行榜
     */
    public static final String URL_GET_TECH_PK_ACTIVITY_RANKING = BASE_URL + "/v1/tech/pk/activity/ranking/index";
    public static final String URL_GET_TECH_PK_ACTIVITY_LIST = BASE_URL + "/v1/tech/pk/activity/list";
    public static final String URL_GET_PK_TEAM_RANKING_LIST = BASE_URL + "/v1/tech/pk/activity/team/ranking/list";
    public static final String URL_GET_PK_PERSONAL_RANKING_LIST = BASE_URL + "/v1/tech/pk/activity/personal/ranking/list";
    public static final String URL_GET_PK_ACTIVITY_CATEGORY_LIST = "/spa-manager/pk/activity/category/list";
    public static final String URL_GET_PERSONAL_RANKING_LIST = BASE_URL + "/v2/manager/ranking_list";

    public static final String URL_ROLE_PERMISSION = BASE_URL + "/v2/tech/menu/list"; //权限
    public static final String URL_ROLE_LIST = BASE_URL + "/v2/tech/role/list"; //角色列表

    /**
     * --------------------------------------> 附近的人:url <----------------------------------------
     */
    // 查看会所位置(未使用)
    public static final String URL_GET_CLUB_POSITION = BASE_URL + "/v1/position/club";
    // 获取所属会所附近客户数量
    public static final String URL_GET_NEARBY_CUS_COUNT = BASE_URL + "/v2/tech/club/nearby/user/count";
    // 获取会所附近客户列表
    public static final String URL_GET_NEARBY_CUS_LIST = BASE_URL + "/v2/tech/club/nearby/user/list";

    // 技师打招呼
    public static final String URL_TECH_SAY_HELLO = BASE_URL + "/v2/tech/hello/{customerId}";
    // 获取技师打招呼剩余数量
    public static final String URL_GET_HELLO_LEFT_COUNT = BASE_URL + "/v2/tech/hello/number/left";
    // 获取技师打招呼列表
    public static final String URL_GET_HELLO_RECORD_LIST = BASE_URL + "/v2/tech/hello/list";
    // 近期是否打过招呼
    public static final String URL_CHECK_HELLO_RECENTLY = BASE_URL + "/v2/tech/hello/{customerId}/status";
    // 查询最新回复列表
    public static final String URL_CHECK_HELLO_REPLY = BASE_URL + "/v2/tech/hello/list/reply/new";

    // 查询同客户的联系限制
    public static final String URL_GET_CONTACT_PERMISSION = BASE_URL + "/v2/tech/contact/permission/{id}";

    // 保存打招呼内容 post
    public static final String URL_SAVE_HELLO_TEMPLATE = BASE_URL + "/v2/tech/hello/template";
    // 获取打招呼内容 get
    public static final String URL_GET_HELLO_TEMPLATE = BASE_URL + "/v2/tech/hello/template";
    // 查询系统模版列表
    public static final String URL_GET_HELLO_TEMPLATE_LIST = BASE_URL + "/v2/tech/hello/template/list";
    // 上传打招呼图片
    public static final String URL_UPLOAD_HELLO_TEMPLATE_IMAGE = BASE_URL + "/v2/tech/hello/template/image";

    /**
     * --------------------------------------> 聊天黑名单 <----------------------------------------
     */
    // 将联系人加入黑名单
    public static final String URL_ADD_TO_BLACKLIST = BASE_URL + "/v2/tech/customer/add/emchat/blacklist";
    // 将联系人移出黑名单
    public static final String URL_REMOVE_FROM_BLACKLIST = BASE_URL + "/v2/tech/customer/remove/emchat/blacklist";
    // 联系人是否在黑名单中
    public static final String URL_IN_BLACKLIST = BASE_URL + "/v2/tech/customer/in/emchat/blacklist";
    // 获取技师黑名单列表
    public static final String URL_GET_TECH_BLACKLIST = BASE_URL + "/v2/tech/customer/emchat/blacklist";
    // 技师是否在联系人黑名单中
    public static final String URL_IN_USER_BLACKLIST = BASE_URL + "/v2/tech/customer/in/user/blacklist";

    //获取会所位置
    public static final String URL_GET_MARK_CHAT_TO_USER = BASE_URL + "/v2/club/location/staticmap";
    public static final String URL_GET_TECH_CHAT_CATEGORY_LIST = BASE_URL + "/v2/tech/chat/category/list";
    public static final String URL_GET_TECH_MARKETING_ITEM_LIST = BASE_URL + "/v2/tech/marketing_item/list";
    /*********************************************************************************************/
    /*                                           keys                                            */
    /*********************************************************************************************/

    public static final String USER_TYPE_TECH = "tech";
    public static final String USER_TYPE_USER = "user";

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
    public static final String KEY_TECH_NAME = "techName";
    public static final String KEY_TECH_No = "techNo";

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
    public static final String KEY_ROLE_CODE = "roleCode";
    public static final String KEY_CHANEL = "chanel";
    public static final String KEY_LOGIN_CHANEL = "loginChanel";
    public static final String KEY_SPARE_TECH_ID = "spareTechId";

    public static final String KEY_LOGIN_CHANNEL = "loginChannel";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USER_AVATAR = "userAvatar";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER = "user";
    public static final String KEY_STATUS = "status";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PAGE_NUMBER = "pageNumber";
    public static final String KEY_PAGE_SIZE = "pageSize";
    public static final String KEY_OLD_PASSWORD = "oldPassword";
    public static final String KEY_NEW_PASSWORD = "newPassword";
    public static final String KEY_IS_MAIN_PAGE = "isMainPage";

    public static final String KEY_PROCESS_TYPE = "processType";
    public static final String KEY_SORT_TYPE = "sortType";
    public static final String KEY_ID = "id";
    public static final String KEY_IDS = "ids";
    public static final String KEY_REASON = "reason";
    public static final String KEY_ORDER_ID = "orderId";
    public static final String KEY_FRIEND_ID = "friendId";

    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_DAY_RANGE = "dayRange";
    public static final String KEY_BEGIN_TIME = "beginTime";
    public static final String KEY_END_TIME = "endTime";
    public static final String KEY_END_DAY = "endDay";

    public static final String KEY_CONSUME_TYPE = "consumeType";

    public static final String KEY_IMG_FILE = "imgFile";
    public static final String KEY_TIME_STAMP = "timestamp";

    public static final String SESSION_TYPE = "app";
    public static final String KEY_DRAW_MONEY_TYPE = "type";

    public static final String KEY_APP_VERSION = "appVersion";
    public static final String KEY_STAT_CATEGORY = "statCate";
    public static final String KEY_STAT_EVENT = "statEvent";
    public static final String KEY_APP_MODEL = "appModel";
    public static final String KEY_APP_BRAND = "appBrand";
    public static final String KEY_APP_IMEI = "appImei";
    public static final String KEY_NOTE_NAME = "noteName";
    public static final String KEY_REMARK = "remark";
    public static final String KEY_CUSTOMER_ID = "contactId";
    public static final String KEY_CONTACT_TYPE = "contactType";
    public static final String KEY_MANAGER_URL = "managerHeadUrl";
    public static final String KEY_TAG_TYPE = "tagType";
    public static final String TECH_CUSTOMER = "tech_customer";
    public static final String KEY_MARK_IMPRESSION = "impression";
    public static final String KEY_CUSTOMER_TYPE = "customerType";
    public static final String TYPE_CUSTOMER = "customer";
    public static final String TECH_ADD = "tech_add";
    public static final String FANS_USER = "fans_user";
    public static final String TEMP_USER = "temp_user";
    public static final String CONTACT_TYPE = "type";
    public static final String WX_USER = "wx_user";
    public static final String FANS_WX_USER = "fans_wx_user";
    public static final String COUPON_USER = "coupon_user";
    public static final String KEY_APPROVE = "approve";
    public static final String KEY_TIMEOUT = "timeout";
    public static final String KEY_REJECT = "reject";
    public static final String KEY_SUBMIT = "submit";
    public static final String KEY_SWITCH_OFF = "off";
    public static final String KEY_SWITCH_ON = "on";
    public static final String KEY_LAST_TIME = "lastTime";


    public static final String KEY_FILTER_ORDER = "filterOrder";
    public static final String KEY_COUPON_STATUS = "couponStatus";
    public static final String KEY_IS_EMPTY = "isEmpty";

    public static final String KEY_UPDATE_APP_ID = "appId";
    public static final String KEY_UPDATE_VERSION = "version";
    public static final String KEY_UPDATE_USER_ID = "userId";

    public static final String KEY_BIND_WX_SUCCESS = "bindSuccess";

    public static final String KEY_USER_WX_OPEN_ID = "openId";
    public static final String KEY_USER_WX_UNION_ID = "userWXUnionId";
    public static final String KEY_TRADE_AMOUNT = "tradeAmount";

    public static final String KEY_USER_WX_CODE = "code";
    public static final String KEY_USER_WX_SCOPE = "scope";
    public static final String KEY_USER_WX_STATE = "state";
    public static final String KEY_USER_WX_WXMP = "wxmp";
    public static final String KEY_USER_WX_PAGE_URL = "pageUrl";

    public static final String KEY_UER_CREDIT_AMOUNT = "amount";
    public static final String KEY_USER_CLUB_ID = "clubId";

    public static final String KEY_GAME_USER_EMCHAT_ID = "emchatId";
    public static final String KEY_DICE_GAME_STATUS = "status";
    public static final String KEY_DICE_GAME_ID = "gameId";
    public static final String KEY_DICE_GAME_TIME = "timestamp";


    public static final String KEY_CAN_SAY_HELLO = "canSayHello";
    public static final String KEY_IS_MY_CUSTOMER = "isMyCustomer";
    public static final String KEY_CURRENT_CHAT_ID = "currentChatId";
    public static final String KEY_CURRENT_USER_TYPE = "currentUserType";
    public static final String KEY_FRIEND_CHAT_ID = "friendChatId";
    public static final String KEY_FRIEND_USER_TYPE = "friendUserType";
    public static final String KEY_FRIEND_MESSAGE_TYPE = "msgType";
    public static final String KEY_CHAT_MSG_ID = "msgId";
    public static final String KEY_SEND_POST = "sentPost";//"0"不发送"1"发送
    public static final String KEY_MSG_TYPE_TEXT = "text";
    public static final String KEY_ORDER_STATUS = "orderStatus";
    public static final String KEY_ORDER_STATUS_COMPLETE = "complete";
    public static final String KEY_ORDER_STATUS_ACCEPT = "accept";
    public static final String KEY_ORDER_STATUS_SUBMIT = "submit";
    public static final String KEY_ORDER_STATUS_REJECT = "reject";
    public static final String KEY_ORDER_STATUS_FAILURE = "failure";
    public static final String KEY_ORDER_STATUS_SUBMIT_AND_ACCEPT = "accept,submit";
    public static final String KEY_TECH_STATUS_FREE = "free";
    public static final String KEY_TECH_STATUS_BUSY = "busy";
    public static final String KEY_TECH_STATUS_REST = "rest";
    public static final String KEY_TECH_DYNAMIC_TYPE = "bizType";
    public static final String KEY_IS_INDEX_PAGE = "isIndexPage";
    public static final String ORDER_TYPE_APPOINT = "appoint";
    public static final String ORDER_TYPE_PAID = "paid";
    public static final String KEY_COUPON_TYPE = "couponType";
    public static final String KEY_JOURNAL_ID = "journalId";
    public static final String KEY_WHICH = "which";
    public static final String KEY_WHICH_VALUE = "androidTechRecoverPassword";
    public static final String KEY_SIGN = "sign";
    /**
     * 自动领券
     */
    public static final String KEY_COUPON_CONTENT = "content";
    public static final String KEY_USER_COUPON_ACT_ID = "actId";
    public static final String KEY_USER_COUPON_CHANEL = "chanel"; //tech,manager,qrCode,link,index
    public static final String KEY_USER_COUPON_EMCHAT_ID = "emchatId";
    public static final String KEY_USER_COUPON_EMCHAT_MESSAGE = "messageContent";
    public static final String KEY_USER_TECH_CODE = "techCode";
    public static final String KEY_USER_CODE = "userCode";

    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";

    public static final String KEY_TYPE = "type";
    public static final String KEY_IS_SHARE = "isShare";
    /**
     * PK排行榜
     */
    public static final String KEY_SORT_BY_COMMENT = "commentStat";
    public static final String KEY_SORT_BY_CUSTOMER = "customerStat";
    public static final String KEY_SORT_BY_SALE = "saleStat";
    public static final String KEY_SORT_BY_COUPON = "couponStat";
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

    // -----------------------------------------> 附近的人 <-----------------------------------------
    public static final String KEY_NEW_CUSTOMER_ID = "customerId";
    public static final String KEY_HELLO_TEMPLATE_ID = "templateId";
    public static final String KEY_HELLO_SINCE = "since";           // 保留
    public static final String KEY_HELLO_TYPE_REPLY = "onlyReply";  // 保留
    public static final String KEY_TEMPLATE_IMAGE_ID = "imageId";
    public static final String KEY_RECENTLY_VISITOR_BEAN = "recentlyBean";
    public static final String KEY_CHAT_CONVERSATION_BEAN = "conversationBean";
    public static final String KEY_REQUEST_CONTACT_PERMISSION_TAG = "requestContactPermission";
    public static final String KEY_REQUEST_SAY_HI_TYPE = "requestSayHiType";
    public static final String KEY_CONTACT_ID_TYPE = "idType";
    public static final String KEY_CONTACT_PERMISSION_INFO = "permissionInfo";

    public static final String KEY_PLATFORM = "platform";
    public static final String VALUE_PLATFORM_TECH = "TECH_APP";
    public static final String KEY_MAP_SIZE = "size";

    /*********************************************************************************************/
    /*                                        configs                                            */
    /*********************************************************************************************/

    public static final int REQUEST_TIMEOUT = 30 * 1000;
    public static final int RESP_TOKEN_EXPIRED = 401;
    public static final int RESP_ERROR = 400;

    public static final int RESP_ERROR_CODE_FOR_LOCAL = 9999;

    public static final String DEFAULT_LOGIN_CHANNEL = "android";

}

