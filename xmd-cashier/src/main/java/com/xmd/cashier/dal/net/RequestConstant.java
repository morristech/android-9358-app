package com.xmd.cashier.dal.net;

/**
 * Created by sdcm on 15-10-23.
 */
public class RequestConstant {
    /*********************************************************************************************/
    /*                                           urls                                            */
    /*********************************************************************************************/
    public static final String SPA_SERVICE_BASE = "/spa-manager/api";

    public static final String URL_LOGIN = SPA_SERVICE_BASE + "/v1/manager/login";      // 登录
    public static final String URL_LOGOUT = SPA_SERVICE_BASE + "/v1/manager/logout";    // 登出

    public static final String URL_CLUB_INFO = SPA_SERVICE_BASE + "/v2/manager/club/info";          // 会所信息
    public static final String URL_CLUB_WX_QRCODE = SPA_SERVICE_BASE + "/v1/wx/club/param_qrcode";  // 会所活动二维码参数

    public static final String URL_MEMBER_INFO = SPA_SERVICE_BASE + "/v2/financial/member/get";     // 获取会员账户信息

    public static final String URL_TRADE_QR_CODE = SPA_SERVICE_BASE + "/v1/wx/pos/order_qrcode";        // 送积分微信二维码
    public static final String URL_GET_BILL_LIST = SPA_SERVICE_BASE + "/v2/manager/pos/deals";      // 交易流水

    public static final String URL_APP_UPDATE_CONFIG = "/app-upgrade-system/appUpgrade";        // 升级
    public static final String URL_APP_UPLOAD_LOG = "/file-upload";         // 上传日志

    public static final String URL_USER_CHECK_INFO_LIST = SPA_SERVICE_BASE + "/v2/manager/checkinfo/info/{number}";     // 获取核销列表:优惠券 + 付费预约

    public static final String URL_GET_VERIFY_TYPE = SPA_SERVICE_BASE + "/v2/manager/checkinfo/type/get";           // 查询核销码类型

    public static final String URL_INFO_COUPON = SPA_SERVICE_BASE + "/v2/manager/checkinfo/coupon/detail";          // 详情-优惠券
    public static final String URL_INFO_PAID_ORDER = SPA_SERVICE_BASE + "/v2/manager/checkinfo/paid_order/detail";  // 详情-付费预约
    public static final String URL_INFO_LUCKY_WHEEL = SPA_SERVICE_BASE + "/v2/manager/checkinfo/lucky_wheel/detail";    // 详情-转盘奖品
    public static final String URL_INFO_SERVICE_ITEM = SPA_SERVICE_BASE + "/v2/manager/checkinfo/service_item_coupon/detail";   // 详情-项目券
    public static final String URL_INFO_COMMON = SPA_SERVICE_BASE + "/v2/manager/checkinfo/common/detail";          // 详情-默认

    public static final String URL_VERIFY_COUPON = SPA_SERVICE_BASE + "/v2/manager/checkinfo/coupon/save";          // 核销-优惠券
    public static final String URL_VERIFY_PAID_ORDER = SPA_SERVICE_BASE + "/v2/manager/checkinfo/paid_order/save";  // 核销-付费预约
    public static final String URL_VERIFY_LUCKY_WHEEL = SPA_SERVICE_BASE + "/v2/manager/checkinfo/lucky_wheel/save";    // 核销-转盘奖品
    public static final String URL_VERIFY_SERVICE_ITEM = SPA_SERVICE_BASE + "/v2/manager/checkinfo/service_item_coupon/save";   // 核销-项目券
    public static final String URL_VERIFY_COMMON = SPA_SERVICE_BASE + "/v2/manager/checkinfo/common/save";          // 核销-默认

    //******************************************* 接单提醒 ****************************************
    public static final String URL_GET_ONLINE_PAY_LIST = SPA_SERVICE_BASE + "/v2/manager/fastpay/order/list";   // 在线买单列表:全部|状态|搜索
    public static final String URL_ONLINE_PAY_STATUS_UPDATE = SPA_SERVICE_BASE + "/v2/manager/fastpay/order/status/update"; // 在线买单状态修改:异常 通过 撤销
    public static final String URL_GET_ORDER_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/club/order/list";   // 预约订单列表:全部|状态|搜索
    public static final String URL_ORDER_RECORD_STATUS_UPDATE = SPA_SERVICE_BASE + "/v2/manager/club/order";    // 预约订单列表状态修改:

    // ****************************************** 交接班结算 *****************************************
    public static final String URL_NEW_SETTLE_SAVE = SPA_SERVICE_BASE + "/v2/manager/pos/settle/new/save";  //保存结算记录
    public static final String URL_NEW_SETTLE_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/pos/settle/new/record/list";    //查询已结算记录列表
    public static final String URL_NEW_SETTLE_RECORD_DETAIL = SPA_SERVICE_BASE + "/v2/manager/pos/settle/new/record/detail";    //查询已或者未计算记录详细

    // ****************************************** 核销记录 ****************************************
    public static final String URL_GET_VERIFY_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/checkinfo/record/list";   //获取核销记录列表
    public static final String URL_GET_VERIFY_RECORD_DETAIL = SPA_SERVICE_BASE + "/v2/manager/checkinfo/record/detail"; //查询核销记录明细
    public static final String URL_GET_VERIFY_TYPE_LIST = SPA_SERVICE_BASE + "/v2/manager/checkinfo/type/list";     //获取核销码类型选择列表

    // ****************************************** POS在线买单 **************************************
    public static final String URL_GET_DISCOUNT_COUPON_DETAIL = SPA_SERVICE_BASE + "/v2/user/fastpay/coupon/detail";    // 买单抵扣券详情

    // ****************************************** POS会员 **************************************
    public static final String URL_GET_MEMBER_SETTING_CONFIG = SPA_SERVICE_BASE + "/v2/manager/member/settings/get";   //会员设置信息获取
    public static final String URL_GET_MEMBER_ACT_PLAN = SPA_SERVICE_BASE + "/v2/manager/member/act/current/online/detail"; //获取会员充值套餐
    public static final String URL_GET_CLUB_TECH_LIST = SPA_SERVICE_BASE + "/v2/manager/member/tech/select/list";    //获取会所技师列表
    public static final String URL_GET_MEMBER_INFO = SPA_SERVICE_BASE + "/v2/manager/member/select/list";         //根据手机或者卡号获取会员详情
    public static final String URL_GET_MEMBER_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/member/order/list";      //获取会员账户记录列表

    public static final String URL_CHECK_MEMBER_CARD_PHONE = SPA_SERVICE_BASE + "/v2/manager/member/telephone/check";   //会员开卡:手机号校验
    public static final String URL_REQUEST_MEMBER_CARD = SPA_SERVICE_BASE + "/v2/manager/member/save";      //会员开卡

    public static final String URL_REQUEST_MEMBER_RECHARGE = SPA_SERVICE_BASE + "/v2/manager/member/recharge/save";     //会员充值:提交充值请求
    public static final String URL_GET_MEMBER_RECHARGE_DETAIL = SPA_SERVICE_BASE + "/v2/manager/member/order/detail";              //会员充值:查询充值请求详情
    public static final String URL_REPORT_MEMBER_RECHARGE_TRADE = SPA_SERVICE_BASE + "/v2/manager/member/order/recharge/success";  //会员充值:汇报POS支付情况 需要签名
    public static final String URL_UPDATE_MEMBER_INFO = SPA_SERVICE_BASE + "/v2/manager/member/update"; //更新会员信息
    public static final String URL_AUTH_CODE_RECHARGE = SPA_SERVICE_BASE + "/v2/manager/member/order/recharge/auth_pay/save";   // 威付通主扫充值

    // ****************************************** 买单有礼 **************************************
    public static final String URL_GET_ONLINE_GIFT_ACTIVITY = SPA_SERVICE_BASE + "/v2/user/fastpay/package/online/act";

    // ****************************************** Pos对账单 **************************************
    public static final String URL_GET_ACCOUNT_STATISTICS = SPA_SERVICE_BASE + "/v2/club/financial/settle/summary";
    public static final String URL_PULL_WANG_POS = SPA_SERVICE_BASE + "/v2/manager/wangpos/pull";

    // ****************************************** 经营项目 **************************************
    public static final String URL_GET_ITEM_STATISTICS = SPA_SERVICE_BASE + "/v2/manager/native/statistic/pos/item/summary";
    public static final String URL_GET_CLUB_WORK_TIME = SPA_SERVICE_BASE + "/v2/manager/club/work-time";

    // ****************************************** 内网收银 **************************************
    public static final String URL_GET_INNER_SWITCH = SPA_SERVICE_BASE + "/v2/manager/settings/switch/get";// 获取内网开关

    public static final String URL_GET_ROOM_LIST = SPA_SERVICE_BASE + "/v2/manager/native/order/room/simple/select/list";   //获取使用中的房间列表
    public static final String URL_GET_HAND_LIST = SPA_SERVICE_BASE + "/v2/manager/native/order/select/list";               //获取手牌列表
    public static final String URL_GET_TECHNICIAN_LIST = SPA_SERVICE_BASE + "/v1/manager/club/employee/group/tech/list";     //获取技师列表                                                                                       //获取会所技师列表
    public static final String URL_GET_INNER_ORDER_LIST = SPA_SERVICE_BASE + "/v2/manager/native/order/normal/list";    //根据房间,技师,手牌查询订单列表

    public static final String URL_GET_INNER_RECORD_LIST = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/list";   //获取内网支付列表
    public static final String URL_GET_INNER_UNPAID_COUNT = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/unpaid/count";      //查询当前内网订单未支付的数量

    // 收银重构
    public static final String URL_GET_PAY_CHANNEL_LIST = SPA_SERVICE_BASE + "/v2/manager/pay/channel/list/valid";      // 获取会所设置的有效支付方式
    public static final String URL_GENERATE_BATCH_ORDER = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/save";   //生成订单(内网+补收款)
    public static final String URL_CALLBACK_BATCH_ORDER = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/success/save";    //支付标记(内网+补收款)
    public static final String URL_GET_BATCH_HOLE_DETAIL = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/detail";  //查询订单详情(内网+补收款)
    public static final String URL_CHECK_PAY_STATUS = SPA_SERVICE_BASE + "/v2/manager/native/order/pay_record/status/get";    // 查询订单支付状态(内网+补收款) 扫码支付
    public static final String URL_CHECK_SCAN_STATUS = SPA_SERVICE_BASE + "/v2/manager/order/id/status";       // 查询订单扫码状态(内网+补收款) 扫码支付

    public static final String URL_AUTH_CODE_ACTIVE = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/auth_pay/save";   //威付通主扫支付

    // 旺POS回调URL
    public static final String WANG_POS_NOTIFY_URL = SPA_SERVICE_BASE + "/v2/wangpos/pay/paid_success";

    // 订单标记详情接口整合
    public static final String URL_CALLBACK_ORDER = SPA_SERVICE_BASE + "/v2/manager/native/order/fast_pay/success/save/detail";
    // 订单详情接口整合
    public static final String URL_CHECK_ORDER = SPA_SERVICE_BASE + "/v2/manager/order/id/info";

    // 心跳上报存活接口
    public static final String URL_ALIVE_REPORT = SPA_SERVICE_BASE + "/v2/app/posHeartBeat";

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

    public static final String KEY_MEMBER_TOKEN = "memberToken";

    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_ORI_AMOUNT = "oriAmount";

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

    public static final String KEY_SETTLE_TIME = "settleTime";
    public static final String KEY_SETTLE_YM = "settleYm";
    public static final String KEY_RECORD_ID = "recordId";

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
    public static final String KEY_PAY_CHANNEL = "payChannel";
    public static final String KEY_ORDER_AMOUNT = "orderAmount";
    public static final String KEY_DISCOUNT_AMOUNT = "discountAmount";

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

    public static final String KEY_EN = "en";
    public static final String KEY_DATE = "date";

    public static final String KEY_RECHARGE = "recharge";
    public static final String KEY_FASTPAY = "fastPay";

    public static final String KEY_AUTH_CODE = "authCode";

    /*********************************************************************************************/
    /*                                        configs                                            */
    /*********************************************************************************************/
    public static final int RESP_TOKEN_EXPIRED = 401;
    public static final int RESP_ERROR = 400;
    public static final int RESP_HTTP_BAD_GATEWAY = 502;

    public static final String DEFAULT_SIGN_VALUE = "need_sign";
    public static final String CLIENT_SECRET = "SPA$@MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6";
}

