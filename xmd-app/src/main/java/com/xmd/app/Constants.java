package com.xmd.app;

/**
 * Created by heyangya on 17-5-24.
 */

public class Constants {
    public static final String EXTRA_EVENT_TAG = "extra_event_tag";
    public static final String EXTRA_DATA = "extra_data";
    public static final String EXTRA_TYPE = "extra_type";

    public static final String EXTRA_CHAT_ID = "extra_chat_id";

    //技师状态
    public static final String TECH_STATUS_VALID = "valid"; //未加入会所
    public static final String TECH_STATUS_REJECT = "reject";//加入会所后被会所拒绝
    public static final String TECH_STATUS_UNCERT = "uncert";//等待会所审核
    public static final String TECH_STATUS_BUSY = "busy";//已成功加入会所，忙状态
    public static final String TECH_STATUS_FREE = "free";//已成功加入会所，休状态
    public static final String TECH_STATUS_REST = "rest";//已成功加入会所，休状态
    public static final String USER_MARK_TECH_ADD = "通讯录";//通讯录
    public static final String USER_MARK_NEW_ADD = "新客";//新客
    public static final String USER_MARK_ACTIVATION = "待激活";//待激活
    public static final String USER_MARK_BIG = "大客";//大客
    public static final String USER_MARK_NORMAL = "普客";//普客

    /**
     * 订单状态
     */
    public static final String ORDER_STATUS_UNPAID = "unpaid";  //待支付
    public static final String ORDER_STATUS_SUBMIT = "submit"; //待接受
    public static final String ORDER_STATUS_ACCEPT = "accept"; //接受
    public static final String ORDER_STATUS_REJECT = "reject"; //拒绝
    public static final String ORDER_STATUS_CANCEL = "cancel"; //取消
    public static final String ORDER_STATUS_CLOSED = "complete"; //完成|已核销
    public static final String ORDER_STATUS_EXPIRE = "expire"; //过期
    public static final String ORDER_STATUS_FAILURE = "failure"; //失效,过期
    public static final String ORDER_STATUS_OVERTIME = "overtime"; //超时
    public static final String ORDER_STATUS_DELETE = "delete"; //删除
    public static final String ORDER_STATUS_ERROR = "error"; //下单支付成功，下单出错
    public static final String ORDER_STATUS_PROCESS = "process"; //付款处理中，用户版查看用的

    /**
     * Umeng统计事件
     */
    public static final String KEY_HOME_BROWSE = "key_home_browse";//首页页面浏览数
    public static final String KEY_NEARBY_CLICK = "key_nearby_click";//附近的人点击数
    public static final String KEY_HELLO_CLICK = "key_hello_click";//点击打招呼的次数
//  public static final String KEY_NEARBY_USER = "key_nearby_user";//点击附近的人独立用户数
//  public static final String KEY_HOME_USER = "key_home_user";//首页浏览独立用户数
//  public static final String KEY_NEARBY_TIME = "key_nearby_time";//停留在附近的人页面的总时长

    public static final String KEY_ALL_BROWSE = "key_all_browse";//全部客户
    public static final String KEY_MINE_BROWSE = "key_mine_browse";//我的拓客
    public static final String KEY_RECENTLY_BROWSE = "key_recently_browse";//最近访客
    public static final String KEY_COLLEAGUE_BROWSE = "key_colleague_browse";//本店同事
    public static final String KEY_NEW_CUSTOMER_CLICK = "key_newcustomer_click";//点击添加客户按钮次数
    public static final String KEY_NEW_CUSTOMER_SAVE_CLICK = "key_newcustomersave_click";//点击保存按钮的次数
    public static final String KEY_FILTER_CLICK = "key_filter_click";//筛选按钮点击次数
    public static final String KEY_FILTER_CUSTOMER_CHOOSE = "key_filtercustomer_choose";//客户标签使用次数
    public static final String KEY_FILTER_CUSTOMER_GROUP = "key_filtergroup_choose";//客户群组过滤
    public static final String KEY_FILTER_VIP_CHOOSE = "key_filtervip_choose";//会员等级使用次数
    public static final String KEY_FILTER_TYPE_CHOOSE = "key_filtertype_choose";//帐号类型使用次数
    public static final String KEY_FILTER_BELONG_CHOOSE = "key_filterbelong_choose";//拓客者使用次数
    public static final String KEY_FILTER_BTN_CLICK = "key_filter_btn_click";//进行筛选


    public static final String KEY_PICTURE_CLICK = "key_picture_click";//图片
    public static final String KEY_PICTURE_SEND = "key_picture_send";//图片发送次数
    public static final String KEY_EMOJI_CLICK = "key_emoji_click";//表情
    public static final String KEY_EMOJI_SEND = "key_emoji_send";//表情发送次数
    public static final String KEY_QUICK_CLICK = "key_quick_click";//快捷回复
    public static final String KEY_QUICK_SEND = "key_quick_send";//快捷回复发送次数
    public static final String KEY_COUPON_CLICK = "key_coupon_click";//发券
    public static final String KEY_COUPON_SEND = "key_coupon_send";//发券分享次数
    public static final String KEY_BOOK_CLICK = "key_book_click";//预约
    public static final String KEY_BOOK_SEND = "key_book_send";//预约发送次数
    public static final String KEY_BOOK_COMPLETE = "key_book_complete";//成功预约次数
    public static final String KEY_BOOK_CANCEL = "key_book_cancel";//取消预约次数
    public static final String KEY_BOOKED_CLICK = "key_booked_click";//求预约
    public static final String KEY_BOOKED_SEND = "key_booked_send";//求预约发送次数
    public static final String KEY_REWARDED_CLICK = "key_rewarded_click";//求打赏
    public static final String KEY_REWARDED_SEND = "key_rewarded_send";//求打赏发送次数
    public static final String KEY_ACTIVITY_CLICK = "key_activity_click";//营销活动
    public static final String KEY_ACTIVITY_SEND = "key_activity_send";//营销活动分享次数
    public static final String KEY_JOURNAL_CLICK = "key_journal_click";//电子期刊
    public static final String KEY_JOURNAL_SEND = "key_journal_send";//电子期刊分享次数
    public static final String KEY_MALL_CLICK = "key_mall_click";//特惠商城
    public static final String KEY_MALL_SEND = "key_mall_send";//特惠商城分享次数
    public static final String KEY_LOCATION_CLICK = "key_location_click";//会所位置
    public static final String KEY_INVITATION_CLICK = "key_invitation_click";//邀请有礼


    public static final int UMENG_STATISTICS_HOME_BROWSE = 0x001;
    public static final int UMENG_STATISTICS_NEARBY_CLICK = 0x002;
    public static final int UMENG_STATISTICS_HELLO_CLICK = 0x003;
    public static final int UMENG_STATISTICS_ALL_BROWSE = 0x004;
    public static final int UMENG_STATISTICS_MINE_BROWSE = 0x005;
    public static final int UMENG_STATISTICS_RECENTLY_BROWSE = 0x006;
    public static final int UMENG_STATISTICS_COLLEAGUE_BROWSE = 0x0007;
    public static final int UMENG_STATISTICS_NEW_CUSTOMER_CLICK = 0x0008;
    public static final int UMENG_STATISTICS_CUSTOMER_SAVE_CLICK = 0x0009;
    public static final int UMENG_STATISTICS_FILTER_CLICK = 0x000A;
    public static final int UMENG_STATISTICS_FILTER_CUSTOMER_CHOOSE = 0x000B;
    public static final int UMENG_STATISTICS_FILTER_VIP_CHOOSE = 0x000C;
    public static final int UMENG_STATISTICS_FILTER_TYPE_CHOOSE = 0x000D;
    public static final int UMENG_STATISTICS_FILTER_BELONG_CHOOSE = 0x000E;
    public static final int UMENG_STATISTICS_FILTER_BTN_CLICK = 0x000F;
    public static final int UMENG_STATISTICS_CUSTOMER_GROUP = 0x0011;
    public static final int UMENG_STATISTICS_PICTURE_CLICK = 0x0012;
    public static final int UMENG_STATISTICS_PICTURE_SEND = 0x0013;
    public static final int UMENG_STATISTICS_EMOJI_CLICK = 0x0014;
    public static final int UMENG_STATISTICS_EMOJI_SEND = 0x0015;
    public static final int UMENG_STATISTICS_QUICK_CLICK = 0x0016;
    public static final int UMENG_STATISTICS_QUICK_SEND = 0x0017;
    public static final int UMENG_STATISTICS_COUPON_CLICK = 0x0018;
    public static final int UMENG_STATISTICS_COUPON_SEND = 0x0019;
    public static final int UMENG_STATISTICS_BOOK_CLICK = 0x001A;
    public static final int UMENG_STATISTICS_BOOK_SEND = 0x001B;
    public static final int UMENG_STATISTICS_BOOKED_CLICK = 0x001C;
    public static final int UMENG_STATISTICS_BOOKED_SEND = 0x001D;
    public static final int UMENG_STATISTICS_REWARDED_CLICK = 0x001E;
    public static final int UMENG_STATISTICS_REWARDED_SEND = 0x001F;
    public static final int UMENG_STATISTICS_ACTIVITY_CLICK = 0x0021;
    public static final int UMENG_STATISTICS_ACTIVITY_SEND = 0x0022;
    public static final int UMENG_STATISTICS_JOURNAL_CLICK = 0x0023;
    public static final int UMENG_STATISTICS_JOURNAL_SEND = 0x0024;
    public static final int UMENG_STATISTICS_MALL_CLICK = 0x0025;
    public static final int UMENG_STATISTICS_MALL_SEND = 0x0026;
    public static final int UMENG_STATISTICS_LOCATION_CLICK = 0x0027;
    public static final int UMENG_STATISTICS_BOOK_COMPLETE = 0x0028;
    public static final int UMENG_STATISTICS_BOOK_CANCEL = 0x0029;
    public static final int UMENG_STATISTICS_INVITATION = 0x0030;


}
