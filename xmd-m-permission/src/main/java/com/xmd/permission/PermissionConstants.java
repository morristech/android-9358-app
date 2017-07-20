package com.xmd.permission;

/**
 * *所有权限定义!
 */

public class PermissionConstants {
    public final static String HOME = "home";//首页
    public final static String MESSAGE = "message";//信息
    public final static String CONTACTS = "contacts";//联系人
    public final static String MARKETING = "marketing";//营销
    public final static String WORK_TIME = "work_time";//工作时间
    public final static String WORK_PROJECT = "work_project";//工作项目
    public final static String WORK_STATUS = "work_status";//工作状态（休/忙）
    public final static String JOIN_OR_QUIT_CLUB = "join_or_quit_club";//加入或退出会所
    public final static String PERSONAL_EDIT = "personal_edit";//个人资料编辑
    public final static String QR_CODE = "qr_code";//二维码
    public final static String CREDIT = "credit";//积分中心
    public final static String STATISTIC = "statistic";//统计
    public final static String STATISTIC_INVITE_CUSTOMER = "statistic_invite_customer";//拓客统计
    public final static String STATISTIC_DISTRIBUTE_COUPON = "statistic_distribute_coupon";//发券统计
    public final static String STATISTIC_PRAISE = "statistic_praise";//好评统计
    public final static String STATISTIC_INCOME = "statistic_income";//收入统计
    public final static String ONLINE_PAY = "online_pay";//在线买单
    public final static String ORDER = "order";//订单
    public final static String VISITOR = "visitor";//最近访客
    public final static String MOMENT = "moment";//动态
    public final static String NEARBY_USER = "nearby_user";//附近的人
    public final static String RANKING_TECHNICIAN = "ranking_technician";//技师排行榜

    public final static String MESSAGE_SEND_PICTURE = "message_send_picture";//发送图片
    public final static String MESSAGE_SEND_EMOJI = "message_send_emoji";//发送表情
    public final static String MESSAGE_FAST_REPLY = "message_fast_reply";//快速回复
    public final static String MESSAGE_SEND_ORDER = "message_send_order"; //发送预约
    public final static String MESSAGE_SEND_COUPON = "message_send_coupon";//发送优惠券
    public final static String MESSAGE_SEND_ORDER_REQUEST = "message_send_order_request";//求预约
    public final static String MESSAGE_SEND_REWARD = "message_send_reward";//求打赏
    public final static String MESSAGE_SEND_ACTIVITY = "message_send_activity";//发送营销活动
    public final static String MESSAGE_SEND_JOURNAL = "message_send_journal";//发送电子期刊
    public final static String MESSAGE_SEND_MALL_INFO = "message_send_mall_info";//发送商城信息
    public final static String MESSAGE_SEND_LOCATION = "message_send_location"; //发送位置
    public final static String MESSAGE_PLAY_CREDIT_GAME = "message_play_credit_game";//积分游戏

    public final static String CONTACTS_VISITOR = "contacts_visitor";//最近访客
    public final static String CONTACTS_CUSTOMER = "contacts_customer";//全部客户
    public final static String CONTACTS_ADD_CUSTOMER = "contacts_add_customer";//添加客户
    public final static String CONTACTS_MY_CLUB = "contacts_my_club";//本店
    public final static String CONTACTS_EMP_PHONE = "my_emp_phone";//手机号


    /************管理者*********/
    public final static String MG_TAB_INDEX = "index";//首页
    public final static String MG_TAB_CHAT = "chat";//消息
    public final static String MG_TAB_CUSTOMER = "custMgr";//客户
    public final static String MG_TAB_ORDER = "order";//订单
    public final static String MG_TAB_COUPON = "coupon";//营销

    /**********************         Level 2  start      ******************************/

    /**
     * 核销
     */
    public static final String MG_INDEX_VERIFY = "index.verify";

    /**
     * 今天数据
     */
    public static final String MG_INDEX_STAT = "index.statistics";

    /**
     * wifi统计
     */
    public static final String MG_INDEX_WIFI = "index.wifi";
    /**
     * 网店访客
     */
    public static final String MG_INDEX_ONLINE = "index.online";
    /**
     * 拓客锁客
     */
    public static final String MG_EXPAND_CUSTOMERS = "expandCustomers";
    /**
     * 客户统计
     */
    public static final String MG_INDEX_REGISTER = "index.register";

    /**
     * 优惠券统计
     */
    public static final String MG_INDEX_COUPON = "index.coupon";

    /**
     * 订单统计
     */
    public static final String MG_INDEX_ORDER = "index.order";

    /**
     * 差评管理
     */
    public static final String MG_INDEX_BAD_COMMENT = "index.badcomment";


    /**
     * 排行榜统计
     */
    public static final String MG_INDEX_RANKING = "index.ranking";

    /**
     * 订单操作
     */
    public static final String MG_ORDER_OPERATE = "order.orderOperate";

    /**
     * 优惠券的使用数据
     */
    public static final String MG_COUPON_USE_DATA = "coupon.useData";

//    核销	index.verify
//    网店宣传	index.statistics
//    营销数据	expandCustomers
//    订单	index.order
//    差评管理	index.badcomment
//    明星技师	index.ranking
//    订单操作	order.orderOperate
//    使用数据	coupon.useData
//    图片	message_send_picture
//    表情	message_send_emoji
//    快捷回复	message_fast_reply
//    发券	message_send_coupon
//    预约	message_send_order
//    更多	message_more
//    WiFi宣传	index.wifi
//    网店访客	index.online
//    新增客户	index.register
//    领券数量	index.coupon
//    营销活动	message_send_activity
//    电子期刊	message_send_journal
//    特惠商城	message_send_mall_info
//    会所位置	message_send_location

}
