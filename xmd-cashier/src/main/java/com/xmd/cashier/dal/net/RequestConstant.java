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
    // POS机送积分微信二维码
    public static final String URL_TRADE_QR_CODE = SPA_SERVICE_BASE + "/v1/wx/pos/order_qrcode";

    // 升级
    public static final String URL_APP_UPDATE_CONFIG = "/app-upgrade-system/appUpgrade";
    // 上传日志
    public static final String URL_APP_UPLOAD_LOG = "/file-upload";

    // 交易流水
    public static final String URL_GET_BILL_LIST = SPA_SERVICE_BASE + "/v2/manager/pos/deals";

    // 获取核销列表:优惠券 + 付费预约
    public static final String URL_USER_CHECK_INFO_LIST = SPA_SERVICE_BASE + "/v2/manager/checkinfo/info/{number}";

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
    public static final String URL_GET_XMD_ONLINE_ORDER_DETAIL = SPA_SERVICE_BASE + "/v2/manager/fastpay/order/detail";
    public static final String URL_GET_XMD_ONLINE_SCAN_STATUS = SPA_SERVICE_BASE + "/v2/manager/order/id/status";
    public static final String URL_GET_XMD_ONLINE_QRCODE_URL = SPA_SERVICE_BASE + "/v2/manager/fast_pay/url/get";
    public static final String URL_DELETE_XMD_ONLINE_ORDER_ID = SPA_SERVICE_BASE + "/v2/manager/order/id/delete";
    public static final String URL_GET_DISCOUNT_COUPON_DETAIL = SPA_SERVICE_BASE + "/v2/user/fastpay/coupon/detail";    // 买单抵扣券详情

    // ****************************************** POS会员 **************************************
    public static final String URL_GET_MEMBER_SETTING_CONFIG = SPA_SERVICE_BASE + "/v2/manager/member/settings/get";   //会员设置信息获取
    public static final String URL_GET_MEMBER_ACT_PLAN = SPA_SERVICE_BASE + "/v2/manager/member/act/current/online/detail"; //获取会员充值套餐
    public static final String URL_GET_CLUB_TECH_LIST = SPA_SERVICE_BASE + "/v2/manager/member/tech/select/list";    //获取会所技师列表
    public static final String URL_GET_MEMBER_INFO = SPA_SERVICE_BASE + "/v2/manager/member/select/list";         //根据手机或者卡号获取会员详情
    public static final String URL_GET_MEMBER_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/member/order/list";      //获取会员账户记录列表
    public static final String URL_REQUEST_MEMBER_PAYMENT = SPA_SERVICE_BASE + "/v2/manager/member/order/trade/save";      //会员支付

    public static final String URL_CHECK_MEMBER_CARD_PHONE = SPA_SERVICE_BASE + "/v2/manager/member/telephone/check";   //会员开卡:手机号校验
    public static final String URL_REQUEST_MEMBER_CARD = SPA_SERVICE_BASE + "/v2/manager/member/save";      //会员开卡

    public static final String URL_REQUEST_MEMBER_RECHARGE = SPA_SERVICE_BASE + "/v2/manager/member/recharge/save";     //会员充值:提交充值请求
    public static final String URL_GET_MEMBER_RECHARGE_DETAIL = SPA_SERVICE_BASE + "/v2/manager/member/order/detail";              //会员充值:查询充值请求详情
    public static final String URL_REPORT_MEMBER_RECHARGE_TRADE = SPA_SERVICE_BASE + "/v2/manager/member/order/recharge/success";  //会员充值:汇报POS支付情况 需要签名
    public static final String URL_UPDATE_MEMBER_INFO = SPA_SERVICE_BASE + "/v2/manager/member/update"; //更新会员信息

    // ****************************************** 买单有礼 **************************************
    public static final String URL_GET_ONLINE_GIFT_ACTIVITY = SPA_SERVICE_BASE + "/v2/user/fastpay/package/online/act";

    // ****************************************** Pos对账单 **************************************
    public static final String URL_GET_ACCOUNT_STATISTICS = SPA_SERVICE_BASE + "/v2/club/financial/settle/summary";

    // ****************************************** 经营项目 **************************************
    public static final String URL_GET_ITEM_STATISTICS = SPA_SERVICE_BASE + "/v2/manager/native/statistic/pos/item/summary";
    public static final String URL_GET_CLUB_WORK_TIME = SPA_SERVICE_BASE + "/v2/manager/club/work-time";

    // ****************************************** 内网收银 **************************************
    public static final String URL_GET_INNER_SWITCH = SPA_SERVICE_BASE + "/v2/manager/settings/switch/get";// 获取内网开关
    public static final String URL_GET_PAY_CHANNEL_LIST = SPA_SERVICE_BASE + "/v2/manager/pay/channel/list/valid";      // 获取会所设置的有效支付方式

    public static final String URL_GET_ROOM_LIST = SPA_SERVICE_BASE + "/v2/manager/native/order/room/simple/select/list";   //获取使用中的房间列表
    public static final String URL_GET_HAND_LIST = SPA_SERVICE_BASE + "/v2/manager/native/order/select/list";               //获取手牌列表
    public static final String URL_GET_TECHNICIAN_LIST = SPA_SERVICE_BASE + "/v1/manager/club/employee/group/tech/list";     //获取技师列表                                                                                       //获取会所技师列表
    public static final String URL_GET_INNER_ORDER_LIST = SPA_SERVICE_BASE + "/v2/manager/native/order/normal/list";    //根据房间,技师,手牌查询订单列表

    // 收银结账部分
    public static final String URL_GET_INNER_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/list";   //获取合并支付列表

    public static final String URL_GENERATE_INNER_BATCH_ORDER = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/save";   //保存结账单
    public static final String URL_CALLBACK_INNER_BATCH_ORDER = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/success/save";    //支付回调
    public static final String URL_GET_THIRD_PAY_STATUS = URL_GET_XMD_ONLINE_ORDER_DETAIL;      //微信支付宝 查询支付详情
    public static final String URL_GET_INNER_BATCH_HOLE = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/detail";  //根据订单PayOrderId查询详情
    public static final String URL_GET_INNER_UNPAID_COUNT = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/unpaid/count";      //查询当前内网订单未支付的数量
    public static final String URL_CHECK_INNER_SUB_PAY_STATUS = SPA_SERVICE_BASE + "/v2/manager/native/order/pay_record/status/get";

    /*********************************************************************************************/
    /*                                           keys                                            */
    /*********************************************************************************************/
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CLUB_ID = "clubId";

    public static final String KEY_SESSION_TYPE = "sessionType";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_COUPON_NO = "couponNo";
    public static final String KEY_ORDER_NO = "orderNo";
    public static final String KEY_CODE = "code";

    public static final String KEY_NUMBER = "number";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_PROCESS_TYPE = "processType";
    public static final String KEY_ID = "id";
    public static final String KEY_APP_VERSION = "appVersion";

    public static final String KEY_STATUS = "status";

    public static final String KEY_REQUEST_TIME = "requestTime";
    public static final String KEY_SIGN = "sign";

    public static final String KEY_TRADE_NO = "tradeNo";

    public static final String KEY_ORIGIN_MONEY = "originMoney";

    public static final String KEY_COUPON_LIST = "couponList";
    public static final String KEY_COUPON_RESULT = "couponResult";
    public static final String KEY_COUPON_MONEY = "couponMoney";

    public static final String KEY_DISCOUNT_TYPE = "discountType";
    public static final String KEY_COUPON_DISCOUNT_MONEY = "couponDiscountMoney";
    public static final String KEY_USER_DISCOUNT_MONEY = "userDiscountMoney";

    public static final String KEY_POS_PAY_OUT_TRADE_NO = "posPayOutTradeNo";
    public static final String KEY_POS_PAY_INNER_TRADE_NO = "posPayInnerTradeNo";
    public static final String KEY_POS_PAY_MONEY = "posPayMoney";
    public static final String KEY_POS_PAY_TYPE = "posPayType";
    public static final String KEY_POS_PAY_RESULT = "posPayResult";
    public static final String KEY_POS_PAY_EXTRAINFO = "posPayExtraInfo";
    public static final String KEY_POS_PAY_CERTIFICATE = "posPayCertificate";

    public static final String KEY_MEMBER_TOKEN = "memberToken";
    public static final String KEY_MEMBER_CAN_DISCOUNT = "canDiscount";

    public static final String KEY_AMOUNT = "amount";

    public static final String KEY_PAY_DATE = "payDate";
    public static final String KEY_PAY_TYPE = "payType";

    public static final String KEY_APP_ID = "appId";
    public static final String KEY_VERSION = "version";

    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";

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
    public static final String KEY_COUNT = "count";
    public static final String KEY_BUSINESS_TYPE = "businessType";

    public static final String KEY_BIRTH = "birth"; //yyyy-MM-dd
    public static final String KEY_GENDER = "gender";   // male||female
    public static final String KEY_CARD_NO = "cardNo";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_MEMBER_PACKAGE_ID = "packageId";
    public static final String KEY_TECH_ID = "techId";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_MEMBER_ID = "memberId";
    public static final String KEY_BIZ_CATEGORY = "businessCategory";
    public static final String KEY_TRADE_TYPE = "tradeType";
    public static final String KEY_PAY_CHANNEL = "payChannel";
    public static final String KEY_ORDER_AMOUNT = "orderAmount";
    public static final String KEY_DISCOUNT_AMOUNT = "discountAmount";

    public static final String KEY_REPORT_TAG = "reportTag";

    public static final String KEY_TECH_NO_SEARCH = "noSearch";
    public static final String KEY_ROLE = "role";
    public static final String KEY_ROOM_ID = "roomId";
    public static final String KEY_ROOM_NAME = "roomName";
    public static final String KEY_EMP_ID = "empId";
    public static final String KEY_USER_IDENTIFY = "userIdentify";

    public static final String KEY_BATCH_NO = "batchNo";
    public static final String KEY_ORDER_IDS = "orderIds";
    public static final String KEY_VERIFY_CODES = "verifyCodes";
    public static final String KEY_PAY_ORDER_ID = "payOrderId";
    public static final String KEY_PUSH_DATA = "data";
    public static final String KEY_REDUCTION_AMOUNT = "reductionAmount";
    public static final String KEY_PAY_NO = "payNo";

    /*********************************************************************************************/
    /*                                        configs                                            */
    /*********************************************************************************************/
    public static final int RESP_TOKEN_EXPIRED = 401;
    public static final int RESP_ERROR = 400;

    public static final String DEFAULT_SIGN_VALUE = "need_sign";
    public static final String CLIENT_SECRET = "SPA$@MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6";
}

