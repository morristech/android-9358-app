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
    public static final String ACTION_CUSTOM_NOTIFY_RECEIVER = "com.xmd.cashier.CUSTOM_NOTIFY";

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

    public static final int VIEW_TYPE_UNKNOW = -1;
    public static final int VIEW_TYPE_COUPON = 1;
    public static final int VIEW_TYPE_ORDER = 2;
    public static final int VIEW_TYPE_TREAT = 3;

    public static final int MAX_MONEY_INT_BIT = 6;
    public static final int MAX_COUPON_NUMBER_LENGTH = 19;

    public static final int CHECK_INFO_TYPE_COUPON = 1;
    public static final int CHECK_INFO_TYPE_ORDER = 2;

    public static final int CASHIER_TYPE_ERROR = -1;
    public static final int CASHIER_TYPE_MEMBER = 0;// 会员支付
    public static final int CASHIER_TYPE_POS = 1;// 普通支付
    public static final int CASHIER_TYPE_XMD_ONLINE = 2;// 小摩豆在线买单

    public static final int PAY_TYPE_UNKNOWN = 0;
    public static final int PAY_TYPE_CASH = 1;
    public static final int PAY_TYPE_ALIPAY = 2;
    public static final int PAY_TYPE_WECHART = 3;
    public static final int PAY_TYPE_CARD = 4;
    public static final int PAY_TYPE_MEMBER = 5;

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
    public static final int TRADE_STATUS_CANCEL = 2;

    public static final String APP_CODE_WEI_POS = "100";
    public static final String APP_CODE_HUI_POS = "101";

    public static final int APP_LIST_DEFAULT_PAGE = 1;
    public static final int APP_LIST_PAGE_SIZE = 20;

    public static final Map<String, Integer> PAY_TIME_FILTERS = new LinkedHashMap<String, Integer>() {{
        put("全部(近三月)", PAY_TIME_ALL_THREE_MONTH);
        put("今天", PAY_TIME_TODAY);
        put("本月除今天", PAY_TIME_MONTH);
    }};

    public static final Map<String, Integer> PAY_TYPE_FILTERS = new LinkedHashMap<String, Integer>() {{
        put("全部", PAY_TYPE_UNKNOWN);
        put("现金", PAY_TYPE_CASH);
        put("支付宝", PAY_TYPE_ALIPAY);
        put("微信", PAY_TYPE_WECHART);
        put("银行卡", PAY_TYPE_CARD);
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

    public static final String EXTRA_BILL_INFO = "extra_bill_info";
    public static final String EXTRA_PHONE_VERIFY = "extra_phone_verify";
    public static final String EXTRA_COUPON_VERIFY_INFO = "extra_coupon_info";
    public static final String EXTRA_ORDER_VERIFY_INFO = "extra_order_verify_info";
    public static final String EXTRA_PRIZE_VERIFY_INFO = "extra_prize_verify_info";
    public static final String EXTRA_COMMON_VERIFY_INFO = "extra_common_verify_info";
    public static final String EXTRA_ONLINE_PAY_INFO = "extra_online_pay_info";
    public static final String EXTRA_NOTIFY_TYPE = "notify_type";
    public static final String EXTRA_NOTIFY_DATA = "notify_data";
    public static final String EXTRA_NOTIFY_TYPE_ORDER_RECORD = "order_record";
    public static final String EXTRA_NOTIFY_TYPE_ONLINE_PAY = "online_pay";
    public static final String EXTRA_RECORD_ID = "record_id";
    public static final String EXTRA_IS_SHOW = "is_show";
    public static final String EXTRA_COUPON_CODE = "coupon_code";

    public static final String APP_REQUEST_YES = "Y";
    public static final String APP_REQUEST_NO = "N";

    public static final String CASHIER_TYPE_POS_TEXT = "银行卡或现金";
    public static final String CASHIER_TYPE_MEMBER_TEXT = "会员支付";
    public static final String CASHIER_TYPE_XMD_ONLINE_TEXT = "微信或支付宝";

    public static final String TYPE_PAID_AMOUNT = "amount";
    public static final String TYPE_PAID_CREDITS = "credits";
    public static final String TYPE_PAID_FREE = "free";

    public static final String PUSH_TAG_FASTPAY = "fast_pay";
    public static final String PUSH_TAG_ORDER = "order";
    public static final String PUSH_TAG_MEMBER_PRINT = "member_print";

    public static final String FAST_PAY_CHANNEL_WX = "wx";
    public static final String FAST_PAY_CHANNEL_ALI = "ali";
    public static final String FAST_PAY_CHANNEL_MEMBER = "account";

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

    public static final int MEMBER_PLAN_ITEM_TYPE_CREDIT = 0;
    public static final int MEMBER_PLAN_ITEM_TYPE_GIF = 1;
    public static final int MEMBER_PLAN_ITEM_TYPE_COUPON = 2;
    public static final int MEMBER_PLAN_ITEM_TYPE_SERVICE = 3;
    public static final int MEMBER_PLAN_ITEM_TYPE_MONEY = 4;

    public static final String ONLINE_PAY_DISCOUNT_COUPON = "coupon";
    public static final String ONLINE_PAY_DISCOUNT_ORDER = "paid_order";
    public static final String ONLINE_PAY_DISCOUNT_MEMBER = "member";

    public static final String INPUT_DIGITS = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
}
