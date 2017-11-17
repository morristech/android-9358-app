package com.xmd.technician;


import com.xmd.technician.bean.Entry;
import com.xmd.technician.common.ResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sdcm on 15-10-23.
 */
public class Constant {

    public final static String EMCHAT_APP_KEY_DEBUG = "xiaomodo#spatest";
    public final static String EMCHAT_APP_KEY_RELEASE = "xiaomodo#spa";

    public static final String DEFAULT_ENCODE = "utf-8";
    public static final String MIME_TYPE_HTML = "text/html";
    public static final int WEBVIEW_TEXT_ZOOM = 80;

    public static final int APP_ID = 1;

    public static final String ORDER_STATUS_ALL = "";
    public static final String SHARE_CONTEXT = "context";

    public static final int AVATAR_MAX_SIZE = 512;
    public static final int ALBUM_MAX_SIZE = 1024;
    public static final int POSTER_MAX_SIZE = 1024;

    public static final String ROLE_TECH = "tech";
    public static final String ROLE_CUSTOMER_SERVICE = "customer_service";

    /**
     * 优惠券
     */
    public static final String COUPON_TYPE = "redpack,ordinary";
    public static final String NORMAL_COUPON_TYPE = "coupon";

    /**
     * 点钟券
     */
    public static final String PAID_TYPE = "paid";
    /**
     * 次卡
     */
    public static final String ONCE_TYPE = "once";
    /**
     * 夺宝
     */
    public static final String PAID_ITEM_TYPE = "paidItem";
    /**
     * 限时抢
     */
    public static final String ONE_YUAN_TYPE = "oneYuan";
    /**
     * 抽奖活动
     */
    public static final String DRAW_TYPE = "draw";
    /**
     * 期刊
     */
    public static final String JOURNAL_TYPE = "journal";


    /**
     * 已接受
     */
    public static final String ORDER_STATUS_ACCEPT = "accept";
    /**
     * 已拒绝
     */
    public static final String ORDER_STATUS_REJECTED = "reject";
    /**
     * 未接受
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
     * 失效
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
     * 普通预约
     */
    public static final String ORDER_TYPE_APPOINT = "appoint";

    /**
     * 付费预约
     */
    public static final String ORDER_TYPE_PAID = "paid";

    public static final String COUPON_TYPE_PAID = "paid";

    public static final String USE_TYPE_MONEY = "money";
    public static final String USE_TYPE_COUPON = "coupon";

    public static final int COUPON_DISPLAY_TYPE_CLUB = 1;
    public static final int COUPON_DISPLAY_TYPE_USER = 2;

    /**
     * 技师状态
     */
    public static final String TECH_STATUS_VALID = "valid"; //未加入会所
    public static final String TECH_STATUS_REJECT = "reject";//加入会所后被会所拒绝
    public static final String TECH_STATUS_UNCERT = "uncert";//等待会所审核
    public static final String TECH_STATUS_BUSY = "busy";//已成功加入会所，忙状态
    public static final String TECH_STATUS_FREE = "free";//已成功加入会所，闲状态
    public static final String TECH_STATUS_REST = "rest";//已成功加入会所，休状态
    public static final String TECH_STATUS = "status";

    /**
     * 订单界面的待接，已接受，已完成三种状态
     */
    public static final String FILTER_ORDER_SUBMIT = "submit";
    /**
     * 订单界面的待接，已接受，已完成三种状态
     */
    public static final String FILTER_ORDER_ACCEPT = "accept";
    /**
     * 订单界面的待接，已接受，已完成三种状态
     */
    public static final String FILTER_ORDER_COMPLETE = "complete,failure,reject,overtime,refund,refunded";
    /**
     *
     */
    public static final String TECH_SHARE_URL = "shareUrl";
    public static final String TECH_CAN_SHARE = "canShare";
    public static final String TECH_USER_HEAD_URL = "userHead";
    public static final String TECH_USER_NAME = "userName";
    public static final String TECH_USER_TECH_NUM = "userNum";
    public static final String TECH_USER_CLUB_NAME = "userClubName";
    public static final String TECH_ShARE_CODE_IMG = "userShareCodeImg";
    public static final String TECH_SHARE_CODE_IMG = "userShareCodeImg";
    /**
     * status:submit 未接受，accept 同意，reject 拒绝，cancel 取消，complete 完成，failure 失效，sysReject 超时。 不传默认查全部订单
     */
    public static final Map<String, String> LABELS = new HashMap<String, String>() {{
        put(ORDER_STATUS_SUBMIT, "未接受");
        put(ORDER_STATUS_ACCEPT, "已接受");
        put(ORDER_STATUS_REJECTED, "已拒绝");
        put(ORDER_STATUS_COMPLETE, "已完成");
        put(ORDER_STATUS_FAILURE, "失效");
        put(ORDER_STATUS_OVERTIME, "超时");
        put(ORDER_STATUS_CANCEL, "取消");
    }};
    /**
     * timeFilter 全部all,每天everyDay;
     */
    public static final String TIME_FILTER_EVERY_DAY = "everyDay";
    public static final String TIME_FILTER_ALL = "all";

    public static final String TIME_FILTER_BY_DAY = "day";
    public static final String TIME_FILTER_BY_WEEK = "week";
    public static final String TIME_FILTER_BY_MONTH = "month";
    public static final String TIME_FILTER_BY_QUARTER = "quarter";
    public static final String TIME_FILTER_BY_YEAR = "year";
    public static final String TIME_FILTER_BY_ALL = "all";

    public static final String COUPON_STATUS_ALL = "0";
    public static final String COUPON_STATUS_ACCEPT = "1";
    public static final String COUPON_STATUS_COMPLETE = "2";
    public static final String COUPON_STATUS_EXPIRE = "3";

    public static final List<Entry> COUPON_STATUS_DATA = new ArrayList<Entry>() {{
        add(new Entry(Constant.COUPON_STATUS_ALL, ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_header_status)));
        add(new Entry(Constant.COUPON_STATUS_ACCEPT, ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_accept)));
        add(new Entry(Constant.COUPON_STATUS_COMPLETE, ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_complete)));
        add(new Entry(Constant.COUPON_STATUS_EXPIRE, ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_expire)));
    }};

    public static final Map<String, String> TIME_FILTER_LABELS = new LinkedHashMap<String, String>() {{
        put("每天", TIME_FILTER_EVERY_DAY);
        put("累计", TIME_FILTER_ALL);

    }};

    public static final Map<String, String> RANKING_TIME_FILTER_LABELS = new LinkedHashMap<String, String>() {{
        put("天", TIME_FILTER_BY_DAY);
        put("周", TIME_FILTER_BY_WEEK);
        put("月", TIME_FILTER_BY_MONTH);
        put("季", TIME_FILTER_BY_QUARTER);
        put("年", TIME_FILTER_BY_YEAR);
        put("累计", TIME_FILTER_BY_ALL);
    }};

    /**
     * interval to refresh the new order account, meanwhile act as the HearBeat
     */
    public static final int NEW_ORDER_ACCOUNT_INTERVAL_SECOND = 60 * 30;

    public static final int PUSH_CLIENT_ID_BIND_RETRY = 3;

    public static final long ONE_DAY = 1000 * 60 * 60 * 24;

    public static final String DEFAULT_SERVER_HOST = "http://spa.93wifi.com";
    public static final String DEFAULT_UPDATE_SERVER = "http://192.168.1.100:9883";

    public static final String PARAM_COUPON_DISPLAY_TYPE = "p_coupon_display_type";

    public static final String PARAM_ACT_ID = "p_act_id";
    public static final String PARAM_SUA_ID = "p_sua_id";
    public static final String PARAM_COUPON_NUMBER = "p_coupon_number";
    public static final String PARAM_PHONE_NUMBER = "p_phone_number";

    public static final String PARAM_SHARE_THUMBNAIL = "p_share_thumbnail";
    public static final String PARAM_SHARE_TITLE = "p_share_title";
    public static final String PARAM_SHARE_URL = "p_share_url";
    public static final String PARAM_SHARE_DESCRIPTION = "p_share_desc";
    public static final String PARAM_SHARE_TYPE = "p_share_type";
    public static final String PARAM_SHARE_DIALOG_TITLE = "dialog_title";
    public static final String PARAM_SHARE_LOCAL_IMAGE = "local_image_url";
    public static final String APP_BROWSER_USER_AGENT = "9358.tech.android.browser";

    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    /*******************************
     * 自定义事件统计
     *****************************/
    public static final String SHARE_COUPON = "couponShare";
    public static final String SHARE_BUSINESS_CARD = "businessCardShare";
    public static final String REGISTER_EVENT = "register";
    public static final String SHARE_JOURNAL = "journalShare";
    public static final String SHARE_OTHER = "otherShare";
    public static final String SHARE_TYPE_LIMIT_GRAB = "";
    public static final String SHARE_TYPE_ONCE_CARD = "onceCard";
    public static final String SHARE_TYPE_PAY_FOR_ME = "";
    public static final String SHARE_TYPE_REWARD_ACTIVITY = "";
    public static final String SHARE_TYPE_TECH_POSTER = "";
    public static final String share_type_ = "";

    /**
     * ******************************* Request Code for activities ***************
     */
    public static final int REQUEST_CODE_FOR_USER_COUPON_LIST_ACTIVITY = 0x0001;
    public static final int REQUEST_CODE_FOR_CONSUME_ACTIVITY = 0x0002;
    public static final int REQUEST_CODE_FOR_ORDER_DETAIL_ACTIVITY = 0x0003;


    /*******************
     * 买单通知*************************
     */
    public static final int PAY_NOTIFY_MAIN_PAGE_SHOW_LIMIT = 10;
    public static final int PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT = 12 * 3600 * 1000; //显示最近12小时的数据

    public static final String REQUEST_SAY_HI_TYPE_VISITOR = "visitor";
    public static final String REQUEST_SAY_HI_TYPE_NEARBY = "nearby";
    public static final String REQUEST_SAY_HI_TYPE_DETAIL = "detail";

    public static final String REQUEST_CONTACT_PERMISSION_VISITOR = "visitor";
    public static final String REQUEST_CONTACT_PERMISSION_EMCHAT = "emchat";
    public static final String REQUEST_CONTACT_PERMISSION_DETAIL = "detail";

    public static final String REQUEST_CONTACT_ID_TYPE_CUSTOMER = "customer";
    public static final String REQUEST_CONTACT_ID_TYPE_EMCHAT = "emchat";

    public static final String CONTACT_INFO_DETAIL_TYPE_CUSTOMER = "customer";
    public static final String CONTACT_INFO_DETAIL_TYPE_MANAGER = "manager";
    public static final String CONTACT_INFO_DETAIL_TYPE_TECH = "tech";

    public static final String EXTRA_FRAGMENT_SWITCH = "switch";
    public static final String EXTRA_HELLO_REPLY_INFO = "hello_reply_info";
    public static final String ACTION_HELLO_REPLY_RECEIVER = "com.xmd.technician.HELLO_REPLY";
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
     * 推送
     */
    public static final String PUSH_MESSAGE_BUSINESS_PAY_NOTIFY = "fast_pay";
    /**
     * 混合套餐
     */
    public static final String ITEM_CARD_TYPE = "item_card"; //单项次卡
    public static final String ITEM_PACKAGE_TYPE = "item_package"; //混合套餐\
    public static final String CREDIT_GIFT_TYPE = "credit_gift"; //积分礼物
    /**
     * 联系人优化
     */
    public static final int FILTER_ALL_CONTACT = 0; //所有联系人
    public static final int FILTER_WX_CONTACT = 1;//wx联系人
    public static final int FILTER_FANS_CONTACT = 2;//粉丝用户/手机用户
    public static final int FILTER_FANS_AND_WX_CONTACT = 3;//粉丝用户+wx用户
    public static final int FILTER_PHONE_CONTACT = 4;//手机联系人
    public static final String USER_WX = "wx_user";
    public static final String USER_FANS = "fans_user";
    public static final String USER_FANS_WX = "fans_wx_user";
    public static final String USER_TECH_ADD = "tech_add";
    public static final String USER_ALL = "";
    public static final String USER_MARK_TECH_ADD = "通讯录";//通讯录
    public static final String USER_MARK_NEW_ADD = "新客";//新客
    public static final String USER_MARK_ACTIVATION = "待激活";//待激活
    public static final String USER_MARK_BIG = "大客";//大客
    public static final String USER_MARK_NORMAL = "普客";//普客
    public static final int CONTACT_ALL_INDEX = 0;
    public static final int CONTACT_REGISTER_INDEX = 1;
    public static final int CONTACT_VISITOR_INDEX = 2;
    public static final int CONTACT_CLUB_INDEX = 3;
    public static final String ROLE_TYPE_MANAGER = "club_manager"; //店长
    public static final String ROLE_TYPE_OP_MANAGER = "op_manager";//运营专员
    public static final String ROLE_TYPE_TECH = "tech";//技师
    public static final String ROLE_TYPE_CASHIER = "cashier";//收银员
    public static final String ROLE_TYPE_FLOOR_STAFF = "floor_staff";//楼面
    public static final String CLUB_EMPLOYEE_TYPE_TECH = "tech";
    public static final String CLUB_EMPLOYEE_TYPE_MANAGER = "manager";
    public static final String CLUB_EMPLOYEE_HAS_NONE_GROUP = "has_none_group";//会所用户未进行分组
    public static final String CLUB_EMPLOYEE_DEFAULT_GROUP = "未分组";//会所用户未进行分组名称
    public static final int CLUB_EMPLOYEE_DEFAULT_GROUP_ID = 999;//会所用户未进行分组ID
    public static final int CONTACT_RECENT_TYPE_NORMAL = 0;
    public static final int CONTACT_RECENT_TYPE_COMMENT = 1;
    public static final int CONTACT_RECENT_TYPE_COLLECTION = 2;
    public static final int CONTACT_RECENT_TYPE_COUPON = 3;
    public static final int CONTACT_RECENT_TYPE_PAID_COUPON = 4;
    public static final int CONTACT_RECENT_TYPE_REWARD = 5;
    public static final String SWITCH_FRAGMENT_INDEX = "selectType";
    public static final String SWITCH_FRAGMENT_ITEM_INDEX = "selectItem";


    public static final String CHAT_MENU_APPOINTMENT = "11";
    public static final String CHAT_MENU_APPOINTMENT_REQUEST = "12";

    public static final String CUSTOMER_STATUS_WORKING = "working";
    public static final String CUSTOMER_STATUS_REST = "rest";

    public static final String TECH_POSTER_CATEGORY_TYPE = "08";
    public static final String TECH_POSTER_TYPE_SQUARE = "01";
    public static final String TECH_POSTER_TYPE_CIRCULAR = "02";
    public static final String TECH_POSTER_TYPE_FLOWER = "03";

    public static final int TECH_POSTER_SQUARE_MODEL = 1;
    public static final int TECH_POSTER_CIRCULAR_MODEL = 2;
    public static final int TECH_POSTER_FLOWER_MODEL = 3;
    public static final String KEY_CURRENT_POSTER = "currentPoster";
    public static final String KEY_QR_CODE_URL = "qrCodeUrl";

    public static final String ORDER_PENDING_TREATMENT = "submit,accept";//只查询待接收，已接受状态下的订单数量
    //小红点
    public static final String RED_POINT_CHAT_ALL_UNREAD = "chat_all_unread";

}

