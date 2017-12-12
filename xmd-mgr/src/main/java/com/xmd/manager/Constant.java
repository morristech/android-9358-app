package com.xmd.manager;

import android.util.SparseArray;

import com.xmd.manager.common.ResourceUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sdcm on 15-10-23.
 */
public class Constant {

    public static final int APP_ID = 11;

    public static final String DEFAULT_ENCODE = "utf-8";
    public static final String MIME_TYPE_HTML = "text/html";
    public static final int WEBVIEW_TEXT_ZOOM = 80;

    public static final SparseArray<String> ACTIVE_DEGREES = new SparseArray<String>() {{
        put(1, "高活跃度");
        put(0, "中活跃度");
        put(-1, "低活跃度");
    }};

    public static final Map<String, String> ORDER_LABELS = new HashMap<String, String>() {{
        put(ORDER_STATUS_SUBMIT, "待接新单");
        put(ORDER_STATUS_ACCEPT, "即将到店");
        put(ORDER_STATUS_REJECTED, "已拒绝");
        put(ORDER_STATUS_COMPLETE, "已到店");
        put(ORDER_STATUS_FAILURE, "爽约");
        put(ORDER_STATUS_OVERTIME, "超时");
        put(ORDER_STATUS_CANCEL, "取消");
        put(ORDER_STATUS_EXPIRE, "已过期");
        put(ORDER_STATUS_REFUND, "已退款");
        put(ORDER_STATUS_ERROR, "出错");
    }};
    public static final Map<String, String> PAID_ORDER_LABELS = new HashMap<String, String>() {{
        put(ORDER_STATUS_SUBMIT, "待接新单");
        put(ORDER_STATUS_ACCEPT, "即将到店");
        put(ORDER_STATUS_REJECTED, "已拒绝");
        put(ORDER_STATUS_COMPLETE, "已核销");
        put(ORDER_STATUS_FAILURE, "爽约");
        put(ORDER_STATUS_OVERTIME, "超时");
        put(ORDER_STATUS_CANCEL, "取消");
        put(ORDER_STATUS_EXPIRE, "已过期");
        put(ORDER_STATUS_REFUND, "已退款");
        put(ORDER_STATUS_ERROR, "出错");
    }};

    public static final String ON = "on";
    public static final String OFF = "off";

    public static final String TIME_FILTER_BY_DAY = "day";
    public static final String TIME_FILTER_BY_WEEK = "week";
    public static final String TIME_FILTER_BY_MONTH = "month";
    public static final String TIME_FILTER_BY_QUARTER = "quarter";
    public static final String TIME_FILTER_BY_YEAR = "year";
    public static final String TIME_FILTER_BY_ALL = "all";

    public static final Map<String, String> RANKING_TIME_FILTER_LABELS = new LinkedHashMap<String, String>() {{
        put("天", TIME_FILTER_BY_DAY);
        put("周", TIME_FILTER_BY_WEEK);
        put("月", TIME_FILTER_BY_MONTH);
        put("季", TIME_FILTER_BY_QUARTER);
        put("年", TIME_FILTER_BY_YEAR);
        put("累计", TIME_FILTER_BY_ALL);
    }};

    public static final String TIME_FILTER_EVERY_DAY = "everyDay";
    public static final String TIME_FILTER_ALL = "all";
    public static final Map<String, String> TIME_FILTER_LABELS = new LinkedHashMap<String, String>() {{
        put("每天", TIME_FILTER_EVERY_DAY);
        put("累计", TIME_FILTER_ALL);
    }};

    public static final Map<String, String> COUPON_TYPE_FILTER_LABELS = new LinkedHashMap<String, String>() {{
        put("全部", COUPON_ALL_TYPE);
        put("现金券", COUPON_CASH_TYPE);
     //   put("体验券", COUPON_COUPON_TYPE);
        put("礼品券", COUPON_GIFT_TYPE);
        put("折扣券", COUPON_DISCOUNT_TYPE);
    }};
    /**
     * 已接受
     */
    public static final String ORDER_STATUS_ACCEPT = "accept";
    /**
     * 已拒绝
     */
    public static final String ORDER_STATUS_REJECTED = "reject";
    /**
     * 待接新单
     */
    public static final String ORDER_STATUS_SUBMIT = "submit";
    /**
     * 取消
     */
    public static final String ORDER_STATUS_CANCEL = "cancel";
    /**
     * 完成
     */
    public static final String ORDER_STATUS_COMPLETE = "complete";
    /**
     * 爽约
     */
    public static final String ORDER_STATUS_FAILURE = "failure";
    /**
     * 超时
     */
    public static final String ORDER_STATUS_OVERTIME = "overtime";
    /**
     * 删除
     */
    public static final String ORDER_STATUS_DELETE = "delete";
    /**
     * 过期
     */
    public static final String ORDER_STATUS_EXPIRE = "expire";
    /**
     * 已退款
     */
    public static final String ORDER_STATUS_REFUND = "refund";
    /**
     * 订单状态
     */
    public static final String ORDER_STATUS_TYPE = "type";
    /**
     * 订单起始查询时间
     */
    public static final String ORDER_START_TIME = "startTime";
    /**
     * 订单结束查询时间
     */
    public static final String ORDER_END_TIME = "endTime";

    /**
     * 出错
     */
    public static final String ORDER_STATUS_ERROR = "error";

    /**
     * 核销
     */
    public static final String PAID_ORDER_OP_VERIFIED = "verified";
    /**
     * 过期
     */
    public static final String PAID_ORDER_OP_EXPIRE = "expire";
    /**
     * 全部
     */
    public static final String ORDER_STATUS_ALL = "";
    /**
     * 在线
     */
    public static final String COUPON_STATUS_ONLINE = "online";

    /**
     * 多店查看
     */
    public static final String MULTI_CLUB_ROLE = "view_clubs";

    /**
     * 全部用户
     */
    public static final String CUSTOMER_TYPE_ALL = "";
    /**
     * 微信用户
     */
    public static final String CUSTOMER_TYPE_WEIXIN = "weixin";
    /**
     * 领券用户，即临时用户
     */
    public static final String CUSTOMER_TYPE_TEMP = "temp";
    public static final String CUSTOMER_TYPE_TEMP_TECH = "temp-tech";
    /**
     * 粉丝用户，即手机号注册
     */
    public static final String CUSTOMER_TYPE_USER = "user";
    public static final String CUSTOMER_TYPE_TECH = "tech";
    /**
     * 全部券
     */
    public static final String COUPON_TYPE_ALL = "";
    /**
     * 优惠券
     */
    public static final String COUPON_TYPE_COMMON = "common";
    /**
     * 现金券
     */
    public static final String COUPON_TYPE_CASH = "cash";
    /**
     * 点钟券
     */
    public static final String COUPON_TYPE_USER_PAID = "user_paid";
    /**
     * 付费预约
     */
    public static final String COUPON_TYPE_PAID_APPOINTMENT = "appointment_paid";
    /**
     * 项目券
     */
    public static final String COUPON_TYPE_PROJECT = "project";
    /**
     * 奖品券
     */
    public static final String COUPON_TYPE_AWARD = "award";
    /**
     * 请客
     */
    public static final String COUPON_TYPE_GUEST = "guest";
    /**
     *
     */
    public static final String KEY_ALL_GROUPS = "all_groups";
    public static final String KEY_USER_GROUPS = "user_groups";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_GROUPS = "group_manamger";


    public static final Map<String, String> CUSTOMER_TYPE_LABELS = new LinkedHashMap<String, String>() {{
        put("全部客户", CUSTOMER_TYPE_ALL);
        put("微信客户", CUSTOMER_TYPE_WEIXIN);
        put("领券客户", CUSTOMER_TYPE_TEMP);
        put("粉丝客户", CUSTOMER_TYPE_USER);
    }};

    public static final Map<String, String> ORDER_TYPE_LABELS = new LinkedHashMap<String, String>() {{

        put(ResourceUtils.getString(R.string.all_orders), ORDER_STATUS_ALL);
        put(ResourceUtils.getString(R.string.untreated_orders), ORDER_STATUS_SUBMIT);
        put(ResourceUtils.getString(R.string.refused_orders), ORDER_STATUS_REJECTED);
        put(ResourceUtils.getString(R.string.overtime_orders), ORDER_STATUS_OVERTIME);
        put(ResourceUtils.getString(R.string.accept_orders), ORDER_STATUS_ACCEPT);
        put(ResourceUtils.getString(R.string.completed_orders), ORDER_STATUS_COMPLETE);
        put(ResourceUtils.getString(R.string.nullity_orders), ORDER_STATUS_FAILURE);

    }};

    public static final Map<String, String> COUPON_TYPE_LABELS = new LinkedHashMap<String, String>() {{
        put(ResourceUtils.getString(R.string.coupon_record_all), COUPON_TYPE_ALL);
        put(ResourceUtils.getString(R.string.coupon_record_common), COUPON_TYPE_COMMON);
        put(ResourceUtils.getString(R.string.coupon_record_cash), COUPON_TYPE_CASH);
        put(ResourceUtils.getString(R.string.coupon_record_user_paid), COUPON_TYPE_USER_PAID);
        put(ResourceUtils.getString(R.string.coupon_record_appointment_paid), COUPON_TYPE_PAID_APPOINTMENT);
        put(ResourceUtils.getString(R.string.coupon_record_project), COUPON_TYPE_PROJECT);
        put(ResourceUtils.getString(R.string.coupon_record_award), COUPON_TYPE_AWARD);
        put(ResourceUtils.getString(R.string.coupon_record_guest), COUPON_TYPE_GUEST);
    }};

    public static final String USE_TYPE_MONEY = "money";
    public static final String USE_TYPE_COUPON = "coupon";

    public static final String COUPON_TYPE_PAID = "paid";
    public static final String COUPON_TYPE_DISCOUNT = "discount";
    public static final String COUPON_TYPE_GIFT = "gift";

    public static final String ORDER_TYPE_APPOINT = "appoint";
    public static final String ORDER_TYPE_PAID = "paid";

    public static final int COUPON_DISPLAY_TYPE_CLUB = 1;
    public static final int COUPON_DISPLAY_TYPE_USER = 2;

    public static final String TYPE_ORDER = "paid_order";
    public static final String TYPE_COUPON = "paid_coupon";

    public static final String DEFAULT_SERVER_HOST = "http://spa.93wifi.com";

    public static final String PARAM_COUPON_DISPLAY_TYPE = "p_coupon_display_type";

    public static final String PARAM_CUSTOMER_ID = "p_customer_id";
    public static final String PARAM_ACT_ID = "p_act_id";
    public static final String PARAM_SUA_ID = "p_sua_id";
    public static final String PARAM_COUPON_NUMBER = "p_coupon_number";
    public static final String PARAM_PHONE_NUMBER = "p_phone_number";

    public static final String PARAM_SHARE_THUMBNAIL = "p_share_thumbnail";
    public static final String PARAM_SHARE_TITLE = "p_share_title";
    public static final String PARAM_SHARE_URL = "p_share_url";
    public static final String PARAM_SHARE_DESCRIPTION = "p_share_desc";

    public static final String APP_BROWSER_USER_AGENT = "9358.manager.android.browser";

    public static final String FORMAT_DATE_TIME = "yyyyMMddHHmmss";

    public static final int CUSTOMER_TYPE_HEADER_TECHNICIAN = 1;
    public static final int CUSTOMER_TYPE_HEADER_ACTIVE_DEGREE = 2;
    public static final int CUSTOMER_TYPE_ITEM = 3;

    public static final String KEY_ID = "id";
    public static final String KEY_CALLBACK = "callback";

    public static final String PARAM_RANGE = "range";
    public static final String SWITCH_INDEX = "index";
    public static final String ONLINE_PAY_STATUS = "pass,paid,unpass";

    /**
     * ******************************* Request Code for activities ***************
     */
    public static final int REQUEST_CODE_FOR_USER_COUPON_LIST_ACTIVITY = 0x0001;
    public static final int REQUEST_CODE_FOR_LOCAL_SELECT_PICTURE = 0x0002;


    /**
     * ******************************** getui business type *********************
     */
    public static final String BUSINESS_NEW_ORDER = "new_order";
    public static final String BUSINESS_CHAT_ORDER = "chat_order";
    public static final String BUSINESS_PROCESS_ORDER = "process_order";
    /**
     * 分组pK
     */
    public static final int HAS_NONE_PK_GROUP = -1;
    public static final int HAS_NONE_RUNNING_PK_GROUP = 0;
    public static final int HAS_RUNNING_PK_GROUP = 1;
    public static final String KEY_CATEGORY_LIST = "categoryList";
    public static final String KEY_CATEGORY_CUSTOMER_TYPE = "01";
    public static final String KEY_CATEGORY_SAIL_TYPE = "02";
    public static final String KEY_CATEGORY_COMMENT_TYPE = "03";
    public static final String KEY_CATEGORY_PAID_TYPE = "04";


    /**
     * 核销类型定义
     */
    public static final int VERIFICATION_VIEW_COMMON = 1;
    public static final int VERIFICATION_VIEW_COUPON = 2;
    public static final int VERIFICATION_VIEW_ORDER = 3;

        /*---------------------------------------- 群发信息　----------------------------------------*/
    /**
     * 会所群发消息选择的参数: 指定用户
     */
    public static final String USER_GROUP_TYPE_SPECIFIED = "specified";
    /**
     * 会所群发消息选择的参数: 按拓客技师分组
     */
    public static final String USER_GROUP_TYPE_TECH = "tech";
    /**
     * 会所群发消息选择的参数: 按客户行为分组
     */
    public static final String USER_GROUP_TYPE_BEHAVIOR = "behavior";
    /**
     * 会所群发消息选择的参数: 按会员卡等级分组
     */
    public static final String USER_GROUP_TYPE_MEMBER = "member";
    public static final Map<String, String> USE_GROUP_LABELS = new LinkedHashMap<String, String>() {{
        put(USER_GROUP_TYPE_BEHAVIOR, ResourceUtils.getString(R.string.group_by_behavior));
        put(USER_GROUP_TYPE_TECH, ResourceUtils.getString(R.string.group_by_tech));
        put(USER_GROUP_TYPE_SPECIFIED, ResourceUtils.getString(R.string.group_by_customize));
        put(USER_GROUP_TYPE_MEMBER, ResourceUtils.getString(R.string.group_by_member));
        put("", ResourceUtils.getString(R.string.all_customer));
    }};

    public static final String MSG_TYPE_COUPON = "ordinaryCoupon";
    public static final String MSG_TYPE_LUCKY_WHEEL = "luckyWheel";
    public static final String MSG_TYPE_TIME_LIMIT = "timeLimit";
    public static final String MSG_TYPE_ONE_YUAN = "oneYuan";
    public static final String MSG_TYPE_JOURNAL = "journal";
    public static final String MSG_TYPE_PLUM_FLOWER = "plumFlower";

    public static final Map<String, String> MESSAGE_ACTIVITY_LABELS = new LinkedHashMap<String, String>() {{
        put(MSG_TYPE_COUPON, ResourceUtils.getString(R.string.club_coupon));
        put(MSG_TYPE_TIME_LIMIT, ResourceUtils.getString(R.string.club_time_limit));
        put(MSG_TYPE_LUCKY_WHEEL, ResourceUtils.getString(R.string.club_lucky_wheel));
        put(MSG_TYPE_ONE_YUAN, ResourceUtils.getString(R.string.club_one_yuan));
        put(MSG_TYPE_JOURNAL, ResourceUtils.getString(R.string.club_journal));
        put(MSG_TYPE_PLUM_FLOWER, ResourceUtils.getString(R.string.club_one_yuan));
    }};

    public static final String RED_POINT_NEW_ORDER = "newOrder";

    public static final String OPERATE_START_TIME = "startTime";
    public static final String OPERATE_END_TIME = "endTime";

    public static final String ONLINE_ORDER_START_TIME = "startTime";
    public static final String ONLINE_ORDER_END_TIME = "endTime";
    public static final String ONLINE_ORDER_STATUS = "orderStatus";
    public static final String ONLINE_ORDER_PROJECT = "orderProject";
    public static final String ONLINE_ORDER_TECH_NO = "techNo";

    public static final int COUPON_FILTER_TIME_TYPE_ALL = 0;
    public static final int COUPON_FILTER_TIME_TYPE_USER = 1;
    public static final int COUPON_RECORD_TIME_TYPE_VERIFICATION = 2;

    public static final String COUPON_RECORD_START_TIME = "startTime";
    public static final String COUPON_RECORD_END_TIME = "endTime";
    public static final int COUPON_TYPE_RECORD_ALL = 0;
    public static final int COUPON_TYPE_RECORD_USABLE = 1; //领取
    public static final int COUPON_TYPE_RECORD_USED = 2; //核销
    public static final int COUPON_TYPE_RECORD_OVER_TIME = 3;//过期

    public static final String COUPON_STATUS_CAN_USE = "canUse";
    public static final String COUPON_STATUS_VERIFIED = "verified";
    public static final String COUPON_STATUS_EXPIRED = "expired";
    public static final String COUPON_STATUS_ALL = "";
    public static final String COUPON_TIME_TYPE_ALL = "";
    public static final String COUPON_TIME_TYPE_GET_TIME = "getTime";
    public static final String COUPON_TIME_TYPE_VERIFY_TIME = "verifyTime";
    public static final String COUPON_CASH_TYPE = "cash";
    public static final String COUPON_COUPON_TYPE = "coupon";
    public static final String COUPON_GIFT_TYPE = "gift";
    public static final String COUPON_DISCOUNT_TYPE = "discount";
    public static final String COUPON_PAID_TYPE = "paid";
    public static final String COUPON_ONLINE_TRUE = "Y";
    public static final String COUPON_ONLINE_FALSE = "N";
    public static final String COUPON_ALL_TYPE = "cash,coupon,gift,discount";

    public static final String COUPON_ONLINE_TYPE = "online";
    public static final String COUPON_OFFLINE_TYPE = "offline";
    //0:未被选中，1：被选中
    public static final int COUPON_IS_SELECTED_TRUE = 1;
    public static final int COUPON_IS_SELECTED_FALSE = 0;

    public static final String KEY_INTENT_COUPON_BEAN = "couponBean";
    public static final String FILTER_COUPON_TIME_START_TIME = "start_time";
    public static final String FILTER_COUPON_TIME_END_TIME = "end_time";
    public static final String FILTER_COUPON_STATUS_TYPE = "statusType";
    public static final String FILTER_COUPON_TIME_TYPE = "time_type";
}

