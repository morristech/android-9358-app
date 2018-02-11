package com.xmd.cashier.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heyangya on 16-8-23.
 */

public class AppConstants {
    public static String SESSION_TYPE = "app";

    public static final String TIME_XMD_REGISTER = "2014-07-23 00:00:00";
    public static final long DEFAULT_INTERVAL = 5 * 1000;
    public static final long TINNY_INTERVAL = 3 * 1000;

    // 核销类型
    public static final String TYPE_COUPON = "coupon";      // 优惠券-->体验券
    public static final String TYPE_CASH_COUPON = "cash_coupon";    //现金券
    public static final String TYPE_GIFT_COUPON = "gift_coupon";    //礼品券
    public static final String TYPE_PAID_COUPON = "paid_coupon";    //点钟券
    public static final String TYPE_DISCOUNT_COUPON = "discount_coupon";    //折扣券
    public static final String TYPE_SERVICE_ITEM_COUPON = "service_item_coupon";    //项目券
    public static final String TYPE_ORDER = "order";        // 付费预约
    public static final String TYPE_PAY_FOR_OTHER = "pay_for_other";    //请客
    public static final String TYPE_LUCKY_WHEEL = "lucky_wheel";    // 大转盘
    public static final String TYPE_PHONE = "phone";        // 手机号

    // 券类型
    public static final String COUPON_TYPE_COUPON = "coupon";//体验券
    public static final String COUPON_TYPE_PAID = "paid";   //点钟券
    public static final String COUPON_TYPE_GIFT = "gift";   //礼品券
    public static final String COUPON_TYPE_CASH = "cash";   //现金券
    public static final String COUPON_TYPE_DISCOUNT = "discount";   //折扣券
    public static final String COUPON_TYPE_SERVICE_ITEM = "service_item";    //项目券

    public static final int VIEW_TYPE_UNKNOWN = -1;
    public static final int VIEW_TYPE_COUPON = 1;
    public static final int VIEW_TYPE_ORDER = 2;
    public static final int VIEW_TYPE_TREAT = 3;

    public static final int MAX_MONEY_INT_BIT = 6;
    public static final int MAX_COUPON_NUMBER_LENGTH = 19;

    public static final int CHECK_INFO_TYPE_COUPON = 1;
    public static final int CHECK_INFO_TYPE_ORDER = 2;

    public static final int CASHIER_TYPE_ERROR = -1;
    public static final int CASHIER_TYPE_MEMBER = 0;// 会员支付
    public static final int CASHIER_TYPE_POS = 1;   // POS支付
    public static final int CASHIER_TYPE_QRCODE = 2;// 扫码支付
    public static final int CASHIER_TYPE_CASH = 3;  // 现金支付
    public static final int CASHIER_TYPE_MARK = 4;  // 记账支付

    public static final int PAY_TYPE_UNKNOWN = 0;
    public static final int PAY_TYPE_CASH = 1;
    public static final int PAY_TYPE_ALIPAY = 2;
    public static final int PAY_TYPE_WECHAT = 3;
    public static final int PAY_TYPE_UNION = 4;

    public static final int DISCOUNT_TYPE_NONE = 0;
    public static final int DISCOUNT_TYPE_COUPON = 2;
    public static final int DISCOUNT_TYPE_USER = 1;

    public static final int PAY_RESULT_SUCCESS = 1;
    public static final int PAY_RESULT_CANCEL = 2;
    public static final int PAY_RESULT_ERROR = 3;

    public static final int PAY_TIME_ALL_THREE_MONTH = 0;
    public static final int PAY_TIME_TODAY = 1;
    public static final int PAY_TIME_MONTH = 2;

    public static final int PAY_STATUS_ALL = 0;
    public static final int PAY_STATUS_SUCCESS = 1;
    public static final int PAY_STATUS_CANCEL = 2;
    public static final int PAY_STATUS_REFUND = 3;

    //已支付订单操作
    public static final String PAID_ORDER_OP_VERIFIED = "verified";
    public static final String PAID_ORDER_OP_EXPIRE = "expire";

    public static final int TRADE_STATUS_SUCCESS = 1;
    public static final int TRADE_STATUS_ERROR = 2;

    // POS类型
    public static final String APP_CODE_WEI_POS = "100";
    public static final String APP_CODE_HUI_POS = "101";
    public static final String APP_CODE_PHONE_POS = "102";

    public static final int APP_LIST_DEFAULT_PAGE = 1;
    public static final int APP_LIST_PAGE_SIZE = 20;
    public static final int APP_LIST_INNRE_PAGE_SIZE = 5;

    public static final Map<String, Integer> PAY_TIME_FILTERS = new LinkedHashMap<String, Integer>() {{
        put("全部(近三月)", PAY_TIME_ALL_THREE_MONTH);
        put("今天", PAY_TIME_TODAY);
        put("本月除今天", PAY_TIME_MONTH);
    }};

    public static final Map<String, Integer> PAY_TYPE_FILTERS = new LinkedHashMap<String, Integer>() {{
        put("全部", PAY_TYPE_UNKNOWN);
        put("现金", PAY_TYPE_CASH);
        put("银行卡", PAY_TYPE_UNION);
    }};

    public static final Map<String, Integer> PAY_STATUS_FILTERS = new LinkedHashMap<String, Integer>() {{
        put("全部", PAY_STATUS_ALL);
        put("成功", PAY_STATUS_SUCCESS);
        put("已退款", PAY_STATUS_REFUND);
    }};

    public static String strSuccess = "点击或上拉，加载更多";
    public static String strError = "加载失败，点击重试...";
    public static String strNoNetwork = "网络异常，请稍后重试...";
    public static String strNone = "---数据加载完成---";
    public static String strLoading = "正在加载...";

    public static final int FOOTER_STATUS_SUCCESS = 0;
    public static final int FOOTER_STATUS_ERROR = 1;
    public static final int FOOTER_STATUS_NO_NETWORK = 2;
    public static final int FOOTER_STATUS_NONE = 3;
    public static final int FOOTER_STATUS_LOADING = 4;

    // 预约订单状态
    public static final String ORDER_RECORD_STATUS_SUBMIT = "submit";
    public static final String ORDER_RECORD_STATUS_SUBMIT_TEXT = "待接新单";
    public static final String ORDER_RECORD_STATUS_ACCEPT = "accept";
    public static final String ORDER_RECORD_STATUS_ACCEPT_TEXT = "即将到店";
    public static final String ORDER_RECORD_STATUS_REJECT = "reject";
    public static final String ORDER_RECORD_STATUS_REJECT_TEXT = "已拒绝";
    public static final String ORDER_RECORD_STATUS_CANCEL = "cancel";
    public static final String ORDER_RECORD_STATUS_CANCEL_TEXT = "已取消";
    public static final String ORDER_RECORD_STATUS_COMPLETE = "complete";
    public static final String ORDER_RECORD_STATUS_COMPLETE_TEXT = "已核销";
    public static final String ORDER_RECORD_STATUS_DONE_TEXT = "已完成";
    public static final String ORDER_RECORD_STATUS_FAILURE = "failure";
    public static final String ORDER_RECORD_STATUS_FAILURE_TEXT = "爽约";
    public static final String ORDER_RECORD_STATUS_OVERTIME = "overtime";
    public static final String ORDER_RECORD_STATUS_OVERTIME_TEXT = "超时";

    // 在线买单状态
    public static final String ONLINE_PAY_STATUS_PAID = "paid";
    public static final String ONLINE_PAY_STATUS_PAID_TEXT = "待确认";
    public static final String ONLINE_PAY_STATUS_PASS = "pass";
    public static final String ONLINE_PAY_STATUS_PASS_TEXT = "已确认";
    public static final String ONLINE_PAY_STATUS_UNPASS = "unpass";
    public static final String ONLINE_PAY_STATUS_UNPASS_TEXT = "到前台";

    public static final String STATUS_ALL_TEXT = "全部";
    public static final String STATUS_LOADING = "loading";
    public static final String STATUS_NORMAL = "normal";
    public static final String STATUS_DETAIL = "detail";

    public static final Map<String, String> ONLINE_PAY_STATUS_FILTER = new LinkedHashMap<String, String>() {{
        put(STATUS_ALL_TEXT, null);
        put(ONLINE_PAY_STATUS_PAID_TEXT, ONLINE_PAY_STATUS_PAID);
        put(ONLINE_PAY_STATUS_PASS_TEXT, ONLINE_PAY_STATUS_PASS);
        put(ONLINE_PAY_STATUS_UNPASS_TEXT, ONLINE_PAY_STATUS_UNPASS);
    }};

    public static final Map<String, String> ORDER_RECORD_STATUS_FILTER = new LinkedHashMap<String, String>() {{
        put(STATUS_ALL_TEXT, null);
        put(ORDER_RECORD_STATUS_SUBMIT_TEXT, ORDER_RECORD_STATUS_SUBMIT);
        put(ORDER_RECORD_STATUS_ACCEPT_TEXT, ORDER_RECORD_STATUS_ACCEPT);
        put(ORDER_RECORD_STATUS_REJECT_TEXT, ORDER_RECORD_STATUS_REJECT);
        put(ORDER_RECORD_STATUS_COMPLETE_TEXT, ORDER_RECORD_STATUS_COMPLETE);
        put(ORDER_RECORD_STATUS_FAILURE_TEXT, ORDER_RECORD_STATUS_FAILURE);
        put(ORDER_RECORD_STATUS_OVERTIME_TEXT, ORDER_RECORD_STATUS_OVERTIME);
    }};

    public static final Map<String, String> PAY_CHANNEL_FILTER = new LinkedHashMap<String, String>() {{
        put(CASHIER_TYPE_WX_TEXT, PAY_CHANNEL_WX);
        put(CASHIER_TYPE_ALI_TEXT, PAY_CHANNEL_ALI);
        put(CASHIER_TYPE_UNION_TEXT, PAY_CHANNEL_UNION);
        put(CASHIER_TYPE_ACCOUNT_TEXT, PAY_CHANNEL_ACCOUNT);
        put(CASHIER_TYPE_CASH_TEXT, PAY_CHANNEL_CASH);
    }};

    public static final String EXTRA_CASH_AMOUNT = "cash_amount";
    public static final String EXTRA_BILL_INFO = "extra_bill_info";
    public static final String EXTRA_PHONE_VERIFY = "extra_phone_verify";
    public static final String EXTRA_COUPON_VERIFY_INFO = "extra_coupon_info";
    public static final String EXTRA_ORDER_VERIFY_INFO = "extra_order_verify_info";
    public static final String EXTRA_PRIZE_VERIFY_INFO = "extra_prize_verify_info";
    public static final String EXTRA_COMMON_VERIFY_INFO = "extra_common_verify_info";
    public static final String EXTRA_ONLINE_PAY_INFO = "extra_online_pay_info";
    public static final String EXTRA_RECORD_ID = "record_id";
    public static final String EXTRA_RECORD_INFO = "record_info";
    public static final String EXTRA_IS_SHOW = "is_show";
    public static final String EXTRA_COUPON_CODE = "coupon_code";
    public static final String EXTRA_GIFT_ACTIVITY_INFO = "gift_act";
    public static final String EXTRA_BIZ_TYPE = "biz_type";
    public static final String EXTRA_TRADE_TYPE = "trade_type";

    public static final String APP_REQUEST_YES = "Y";
    public static final String APP_REQUEST_NO = "N";

    public static final String CASHIER_TYPE_XMD_ONLINE_TEXT = "微信或支付宝";
    public static final String CASHIER_TYPE_ACCOUNT_TEXT = "会员支付";
    public static final String CASHIER_TYPE_CASH_TEXT = "现金支付";
    public static final String CASHIER_TYPE_WX_TEXT = "微信支付";
    public static final String CASHIER_TYPE_ALI_TEXT = "支付宝支付";
    public static final String CASHIER_TYPE_UNION_TEXT = "银行卡支付";
    public static final String CASHIER_TYPE_OTHER_TEXT = "其他支付";

    public static final String TYPE_PAID_AMOUNT = "amount";
    public static final String TYPE_PAID_CREDITS = "credits";
    public static final String TYPE_PAID_FREE = "free";

    public static final String PUSH_TAG_FASTPAY = "fast_pay";
    public static final String PUSH_TAG_ORDER = "order";
    public static final String PUSH_TAG_MEMBER_PRINT = "member_print";  //会员记录打印
    public static final String PUSH_TAG_ORDER_PRINT = "order_print";    //订单记录打印
    public static final String PUSH_TAG_FASTPAY_PRINT = "fast_pay_print";   //买单记录打印
    public static final String PUSH_TAG_CLUB_ORDER_TO_PAY = "club_order_to_pay";
    public static final String PUSH_TAG_FAST_PAY_SUCCESS = "fast_pay_success";
    public static final String PUSH_TAG_UPLOAD_LOG = "upload_log";  //上传日志

    public static final int TRADE_TYPE_NORMAL = 1;  //普通补收款
    public static final int TRADE_TYPE_INNER = 2;   //内网支付
    public static final int TRADE_TYPE_RECHARGE = 3;    //会员充值

    public static final String PAY_CHANNEL_WX = "wx";
    public static final String PAY_CHANNEL_ALI = "ali";
    public static final String PAY_CHANNEL_UNION = "union";
    public static final String PAY_CHANNEL_CASH = "cash";
    public static final String PAY_CHANNEL_ACCOUNT = "account";
    public static final String PAY_CHANNEL_OTHER = "other";

    public static final String PLATFORM_OFFLINE = "offline";
    public static final String PLATFORM_ONLINE = "online";
    public static final String PLATFORM_CASHIER = "cashier";

    public static final String EXTRA_MEMBER_CASHIER_METHOD = "member_cashier_method";
    public static final String MEMBER_CASHIER_METHOD_SCAN = "scan";
    public static final String MEMBER_CASHIER_METHOD_CASH = "cash";

    public static final String EXTRA_MEMBER_BUSINESS_TYPE = "member_read_type";
    public static final String MEMBER_BUSINESS_TYPE_RECHARGE = "recharge";
    public static final String MEMBER_BUSINESS_TYPE_PAYMENT = "payment";
    public static final String MEMBER_BUSINESS_TYPE_CARD = "card";

    public static final int MEMBER_CARD_MODEL_NORMAL = 1;   //电子卡实体卡
    public static final int MEMBER_CARD_MODEL_WITHOUT = 2;  //仅电子卡

    public static final String MEMBER_RECHARGE_MODEL_POS = "cashier";
    public static final String MEMBER_TRADE_TYPE_PAY = "pay";
    public static final String MEMBER_TRADE_TYPE_INCOME = "income";

    public static final String MEMBER_GENDER_FEMALE = "female";
    public static final String MEMBER_GENDER_MALE = "male";

    public static final String MEMBER_RECORD_TYPE_OTHER = "member_other";
    public static final String MEMBER_RECORD_TYPE_SUBTRACT = "member_subtract"; //错充扣回
    public static final String MEMBER_RECORD_TYPE_REFUND = "member_refund";     //错扣退款
    public static final String MEMBER_RECORD_TYPE_CONSUME = "consume";
    public static final String MEMBER_RECORD_TYPE_RECHARGE = "recharge";
    public static final String MEMBER_RECORD_TYPE_CONSUME_TEXT = "会员消费";
    public static final String MEMBER_RECORD_TYPE_RECHARGE_TEXT = "会员充值";

    public static final Map<String, String> MEMBER_RECORD_STATUS_FILTER = new LinkedHashMap<String, String>() {{
        put(STATUS_ALL_TEXT, null);
        put(MEMBER_RECORD_TYPE_CONSUME_TEXT, MEMBER_RECORD_TYPE_CONSUME);
        put(MEMBER_RECORD_TYPE_RECHARGE_TEXT, MEMBER_RECORD_TYPE_RECHARGE);
    }};

    public static final List<String> MEMBER_CARD_STEPS_NORMAL = Arrays.asList(new String[]{"手机验证", "会员资料", "绑定卡片", "开卡成功"});
    public static final List<String> MEMBER_CARD_STEPS_WITHOUT = Arrays.asList(new String[]{"手机验证", "会员资料", "开卡成功"});

    // 会员支付媒介：二维码 || 接口
    public static final String MEMBER_PAY_METHOD_CODE = "code";
    public static final String MEMBER_PAY_METHOD_SCAN = "scan";

    public static final String MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE = "package";
    public static final String MEMBER_RECHARGE_AMOUNT_TYPE_MONEY = "money";
    public static final String MEMBER_RECHARGE_AMOUNT_TYPE_NONE = "none";

    public static final String REPORT_DATA_BIZ_TRADE = "trade";
    public static final String REPORT_DATA_BIZ_MEMBER = "member";
    public static final String REPORT_DATA_BIZ_INNER = "inner";

    public static final int ITEM_TYPE_CREDIT = 0;   //积分
    public static final int ITEM_TYPE_GIF = 1;      //礼品
    public static final int ITEM_TYPE_COUPON = 2;   //优惠券
    public static final int ITEM_TYPE_SERVICE = 3;  //项目
    public static final int ITEM_TYPE_MONEY = 4;    //现金

    public static final String PAY_DISCOUNT_COUPON = "coupon";
    public static final String PAY_DISCOUNT_ORDER = "paid_order";
    public static final String PAY_DISCOUNT_MEMBER = "member";
    public static final String PAY_DISCOUNT_REDUCTION = "reduction";

    public static final String INPUT_DIGITS = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String QR_TYPE_POS = "pos";
    public static final String QR_TYPE_CLUB = "club";
    public static final String QR_TYPE_TECH = "tech";

    public static final String STATISTICS_DEFAULT_TIME = "00:00:00";

    public static final String INNER_SEARCH_TYPE_ROOM = "room";
    public static final String INNER_SEARCH_TYPE_ORDER = "order";
    public static final String INNER_SEARCH_TYPE_TECH = "tech";

    public static final String INNER_ORDER_ITEM_TYPE_SPA = "spa";
    public static final String INNER_ORDER_ITEM_TYPE_GOODS = "goods";

    public static final String INNER_BATCH_STATUS_UNPAID = "unpaid";
    public static final String INNER_BATCH_STATUS_PAID = "paid";
    public static final String INNER_BATCH_STATUS_PASS = "pass";
    public static final String INNER_BATCH_STATUS_UNPASS = "unpass";

    public static final String INNER_SWITCH_CODE = "native_system";

    public static final String EXTRA_INNER_EMP_ID = "empId";
    public static final String EXTRA_INNER_RECORD_DETAIL = "order_detail";

    public static final String EXTRA_INNER_METHOD_SOURCE = "pay_source";
    public static final String INNER_METHOD_SOURCE_NORMAL = "normal";
    public static final String INNER_METHOD_SOURCE_RECORD = "record";
    public static final String INNER_METHOD_SOURCE_PUSH = "push";
    public static final String INNER_METHOD_SOURCE_CONTINUE = "continue";

    public static final String EXTRA_INNER_DETAIL_SOURCE = "detail_source";
    public static final String INNER_DETAIL_SOURCE_OTHER = "other";
    public static final String INNER_DETAIL_SOURCE_RECORD = "record";

    public static final List<String> INNER_PAY_STEPS = Arrays.asList(new String[]{"结账方式", "支付方式", "支付结果"});

    public static final String INNER_DISCOUNT_CHECK_CASH_COUPON = "cash_coupon";
    public static final String INNER_DISCOUNT_CHECK_DISCOUNT_COUPON = "discount_coupon";
    public static final String INNER_DISCOUNT_CHECK_COUPON = "coupon";
    public static final String INNER_DISCOUNT_GIFT_COUPON = "gift_coupon";
    public static final String INNER_DISCOUNT_SERVICE_ITEM_COUPON = "service_item_coupon";
    public static final String INNER_DISCOUNT_CONSUME = "consume";
    public static final String INNER_DISCOUNT_PAID_ORDER = "paid_order";

    public static final String TECH_STATUS_FREE = "free";
    public static final String TECH_STATUS_BUSY = "busy";
    public static final String TECH_STATUS_REST = "rest";

    public static final String EXTRA_INNER_PAY_RECORD = "pay_record";

    public final static String EXTRA_CMD = "cmd";
    public final static String EXTRA_ALL = "all";

    public final static String LOG_BIZ_NATIVE_CASHIER = "[内网收银]";
    public final static String LOG_BIZ_NORMAL_CASHIER = "[补收款收银]";
    public final static String LOG_BIZ_MEMBER_MANAGER = "[会员管理]";
    public final static String LOG_BIZ_ACCOUNT_MANAGER = "[账号系统]";
    public final static String LOG_BIZ_LOCAL_CONFIG = "[本地配置]";
    public final static String LOG_BIZ_MAIN_VERIFY = "[首页核销]";
}
