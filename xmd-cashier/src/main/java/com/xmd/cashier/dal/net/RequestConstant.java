package com.xmd.cashier.dal.net;

/**
 * Created by sdcm on 15-10-23.
 */
public class RequestConstant {

    /*********************************************************************************************/
    /*                                           urls                                            */
    /*********************************************************************************************/
    public static final String SPA_SERVICE_BASE = "/spa-manager/api";

    // 登录登出
    public static final String URL_LOGIN = SPA_SERVICE_BASE + "/v1/manager/login";
    public static final String URL_LOGOUT = SPA_SERVICE_BASE + "/v1/manager/logout";

    // 会所
    public static final String URL_CLUB_INFO = SPA_SERVICE_BASE + "/v2/manager/club/info";
    // 会所活动二维码参数
    public static final String URL_CLUB_WX_QRCODE = SPA_SERVICE_BASE + "/v1/wx/club/param_qrcode";

    // 获取会员账户优惠信息
    public static final String URL_MEMBER_INFO = SPA_SERVICE_BASE + "/v2/financial/member/get";
    // 会员付款二维码扫码支付
    public static final String URL_MEMBER_PAY = SPA_SERVICE_BASE + "/v2/financial/member/pay/save";
    // 保存支付流水
    public static final String URL_REPORT_TRADE_DATA = SPA_SERVICE_BASE + "/v2/manager/payreport/save";
    // 订单编号生成
    public static final String URL_GET_TRADE_NO = SPA_SERVICE_BASE + "/v2/manager/pospaydeal/tradeno";
    // 给用户赠送积分
    public static final String URL_GAIN_POINTS = SPA_SERVICE_BASE + "/v2/manager/pospaydeal/delivery/credit";
    // POS机送积分微信二维码
    public static final String URL_TRADE_QR_CODE = SPA_SERVICE_BASE + "/v1/wx/pos/order_qrcode";

    // 升级
    public static final String URL_APP_UPDATE_CONFIG = "/app-upgrade-system/appUpgrade";
    // 交易流水
    public static final String URL_GET_BILL_LIST = SPA_SERVICE_BASE + "/v2/manager/pos/deals";

    // 获取核销列表:优惠券 + 付费预约
    public static final String URL_USER_CHECK_INFO_LIST = SPA_SERVICE_BASE + "/v2/manager/checkinfo/info/{number}";

    // 获取优惠列表   -- 核销 old
    public static final String URL_USER_COUPON_LIST = SPA_SERVICE_BASE + "/v2/manager/user/coupons";
    // 根据预约码查询优惠券或者订单  -- 收银
    public static final String URL_GET_ORDER_COUPON_VIEW = SPA_SERVICE_BASE + "/v2/manager/user/paid_order_or_coupon/view";
    // 根据授权码查询未使用的授权详情--针对请客    -- 收银
    public static final String URL_GET_TREAT_INFO = SPA_SERVICE_BASE + "/v2/financial/account/payforother/not_used_detail";

    // 查询核销码类型
    public static final String URL_GET_VERIFY_TYPE = SPA_SERVICE_BASE + "/v2/manager/checkinfo/type/get";

    // 详情-优惠券
    public static final String URL_INFO_COUPON = SPA_SERVICE_BASE + "/v2/manager/checkinfo/coupon/detail";
    // 详情-付费预约
    public static final String URL_INFO_PAID_ORDER = SPA_SERVICE_BASE + "/v2/manager/checkinfo/paid_order/detail";
    // 详情-转盘奖品
    public static final String URL_INFO_LUCKY_WHEEL = SPA_SERVICE_BASE + "/v2/manager/checkinfo/lucky_wheel/detail";
    // 详情-项目券
    public static final String URL_INFO_SERVICE_ITEM = SPA_SERVICE_BASE + "/v2/manager/checkinfo/service_item_coupon/detail";
    // 详情-默认
    public static final String URL_INFO_COMMON = SPA_SERVICE_BASE + "/v2/manager/checkinfo/common/detail";
    // 详情-请客 = 根据授权码查询未使用的授权详情

    // 核销-优惠券
    public static final String URL_VERIFY_COUPON = SPA_SERVICE_BASE + "/v2/manager/checkinfo/coupon/save";
    // 核销-付费预约
    public static final String URL_VERIFY_PAID_ORDER = SPA_SERVICE_BASE + "/v2/manager/checkinfo/paid_order/save";
    // 核销-转盘奖品
    public static final String URL_VERIFY_LUCKY_WHEEL = SPA_SERVICE_BASE + "/v2/manager/checkinfo/lucky_wheel/save";
    // 核销-项目券
    public static final String URL_VERIFY_SERVICE_ITEM = SPA_SERVICE_BASE + "/v2/manager/checkinfo/service_item_coupon/save";
    // 核销-默认
    public static final String URL_VERIFY_COMMON = SPA_SERVICE_BASE + "/v2/manager/checkinfo/common/save";
    // 核销-请客 = 核销-默认

    //******************************************* 接单提醒 ****************************************
    public static final String URL_GET_ONLINE_PAY_LIST = SPA_SERVICE_BASE + "/v2/manager/fastpay/order/list";   // 在线买单列表:全部|状态|搜索
    public static final String URL_ONLINE_PAY_STATUS_UPDATE = SPA_SERVICE_BASE + "/v2/manager/fastpay/order/status/update"; // 在线买单状态修改:异常 通过 撤销
    public static final String URL_GET_ORDER_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/club/order/list";   // 预约订单列表:全部|状态|搜索
    public static final String URL_ORDER_RECORD_STATUS_UPDATE = SPA_SERVICE_BASE + "/v2/manager/club/order";    // 预约订单列表状态修改:

    // ****************************************** Pos结算 *****************************************
    public static final String URL_SETTLE_SAVE = SPA_SERVICE_BASE + "/v2/manager/pos/settle/save";      // 保存结算结果
    public static final String URL_SETTLE_GET_CURRENT_SUMMARY = SPA_SERVICE_BASE + "/v2/manager/pos/settle/current";    //获取当前未结算汇总
    public static final String URL_SETTLE_GET_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/pos/settle/record/list";    //获取结算记录:全部|按月
    public static final String URL_SETTLE_GET_RECORD_DETAIL = SPA_SERVICE_BASE + "/v2/manager/pos/settle/record/detail";    //获取结算记录详情

    // ****************************************** 核销记录 ****************************************
    public static final String URL_GET_VERIFY_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/checkinfo/record/list";   //获取核销记录列表
    public static final String URL_GET_VERIFY_RECORD_DETAIL = SPA_SERVICE_BASE + "/v2/manager/checkinfo/record/detail"; //查询核销记录明细
    public static final String URL_GET_VERIFY_TYPE_LIST = SPA_SERVICE_BASE + "/v2/manager/checkinfo/type/list";     //获取核销码类型选择列表

    // ****************************************** POS在线买单 **************************************
    public static final String URL_GET_XMD_ONLINE_ORDER_ID = SPA_SERVICE_BASE + "/v2/manager/order/id/get";
    public static final String URL_GET_XMD_ONLINE_ORDER_DETAIL = SPA_SERVICE_BASE + "/v2/manager/fastpay/order/detail";
    public static final String URL_GET_XMD_ONLINE_SCAN_STATUS = SPA_SERVICE_BASE + "/v2/manager/order/id/status";
    public static final String URL_GET_XMD_ONLINE_QRCODE_URL = SPA_SERVICE_BASE + "/v2/manager/fast_pay/url/get";

    /*********************************************************************************************/
    /*                                           keys                                            */
    /*********************************************************************************************/
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CLUB_ID = "clubId";

    public static final String KEY_SESSION_TYPE = "sessionType";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_SUA_ID = "suaId";
    public static final String KEY_COUPON_NO = "couponNo";
    public static final String KEY_ORDER_NO = "orderNo";
    public static final String KEY_CODE = "code";

    public static final String KEY_PHONE_NUMBER = "phoneNum";
    public static final String KEY_NUMBER = "number";

    public static final String KEY_COUPON_TYPE = "couponType";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_PROCESS_TYPE = "processType";
    public static final String KEY_ID = "id";
    public static final String KEY_APP_VERSION = "appVersion";

    public static final String KEY_USED_AMOUNT = "usedAmount";

    public static final String KEY_STATUS = "status";

    public static final String KEY_REQUEST_TIME = "requestTime";
    public static final String KEY_SIGN = "sign";

    public static final String KEY_TRADE_NO = "tradeNo";
    public static final String KEY_TRADE_STATUS = "tradeStatus";

    public static final String KEY_ORIGIN_MONEY = "originMoney";

    public static final String KEY_COUPON_LIST = "couponList";
    public static final String KEY_COUPON_RESULT = "couponResult";
    public static final String KEY_COUPON_MONEY = "couponMoney";

    public static final String KEY_DISCOUNT_TYPE = "discountType";
    public static final String KEY_COUPON_DISCOUNT_MONEY = "couponDiscountMoney";
    public static final String KEY_USER_DISCOUNT_MONEY = "userDiscountMoney";

    public static final String KEY_MEMBER_CARD_NO = "memberCardNo";
    public static final String KEY_MEMBER_PAY_MONEY = "memberPayMoney";
    public static final String KEY_MEMBER_PAY_DISCOUNT_MONEY = "memberPayDiscountMoney";
    public static final String KEY_MEMBER_PAY_RESULT = "memberPayResult";
    public static final String KEY_MEMBER_PAY_CERTIFICATE = "memberPayCertificate";

    public static final String KEY_POS_PAY_OUT_TRADE_NO = "posPayOutTradeNo";
    public static final String KEY_POS_PAY_INNER_TRADE_NO = "posPayInnerTradeNo";
    public static final String KEY_POS_PAY_MONEY = "posPayMoney";
    public static final String KEY_POS_PAY_TYPE = "posPayType";
    public static final String KEY_POS_PAY_RESULT = "posPayResult";
    public static final String KEY_POS_PAY_EXTRAINFO = "posPayExtraInfo";
    public static final String KEY_POS_PAY_CERTIFICATE = "posPayCertificate";


    public static final String KEY_MEMBER_TOKEN = "memberToken";
    public static final String KEY_MEMBER_CAN_DISCOUNT = "canDiscount";


    public static final String KEY_CASHIER_MONEY = "cashierMoney";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_AMOUNT = "amount";


    public static final String KEY_EXTRA_INFO = "extraInfo";
    public static final String KEY_PAY_DATE = "payDate";
    public static final String KEY_PAY_CERTIFICATE = "payCertificate";
    public static final String KEY_CASHIER_TRADE_NO = "cashierTradeNo";
    public static final String KEY_PAY_TRADE_NO = "payTradeNo";
    public static final String KEY_PAY_TYPE = "payType";
    public static final String KEY_PAY_RESULT = "payResult";

    public static final String KEY_APP_ID = "appId";
    public static final String KEY_VERSION = "version";

    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";

    public static final String KEY_PAGE_START = "page";
    public static final String KEY_PAGE_SIZE = "pageSize";

    public static final String KEY_VERIFY_CODE = "verifyCode";
    public static final String KEY_TYPE = "type";

    public static final String KEY_TECH_NAME = "techName";
    public static final String KEY_TELEPHONE = "telephone";
    public static final String KEY_ORDER_ID = "orderId";

    public static final String KEY_IS_POS = "isPos";
    public static final String KEY_IS_TIME = "isTime";

    public static final String KEY_SETTLE_RECORD = "settleRecord";
    public static final String KEY_SETTLE_YEAR_MONTH = "settleYm";
    public static final String KEY_RECORD_ID = "recordId";

    public static final String KEY_TOTAL = "total";
    public static final String KEY_DISCOUNT = "discount";

    /*********************************************************************************************/
    /*                                        configs                                            */
    /*********************************************************************************************/

    public static final int REQUEST_TIMEOUT = 30 * 1000;

    public static final int RESP_OK = 200;
    public static final int RESP_OK_NO_CONTENT = 204;
    public static final int RESP_TOKEN_EXPIRED = 401;
    public static final int RESP_ERROR = 400;
    public static final int RESP_EXCEPTION = 404;

    public static final int RESP_ERROR_CODE_FOR_LOCAL = 9999;
    public static final int PAY_RESULT_ERROR_CODE = 500;

    public static final String RESP_STATUS_FAIL = "fail";

    public static final String RESULT_TYPE_ORDER = "paid_order";
    public static final String RESULT_TYPE_COUPON = "paid_coupon";

    public static final String DEFAULT_SIGN_VALUE = "need_sign";
    public static final String CLIENT_SECRET = "SPA$@MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6";
}

