package com.xmd.technician;


import com.xmd.technician.bean.Entry;
import com.xmd.technician.common.ResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sdcm on 15-10-23.
 */
public class Constant {

    public static final String DEFAULT_ENCODE = "utf-8";
    public static final String MIME_TYPE_HTML = "text/html";
    public static final int WEBVIEW_TEXT_ZOOM = 80;

    public static final int APP_ID = 1;

    public static final String ORDER_STATUS_ALL = "";
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
    public static final String TECH_STATUS_VALID = "valid";
    public static final String TECH_STATUS_REJECT = "reject";
    public static final String TECH_STATUS_UNCERT = "uncert";
    public static final String TECH_STATUS_BUSY = "busy";

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
        public static final String TECH_USER_HEAD_URL= "userHead";
        public static final String TECH_USER_NAME ="userName";
        public static final String TECH_USER_TECH_NUM = "userNum";
        public static final String TECH_USER_CLUB_NAME = "userClubName";
        public static final String TECH_ShARE_CODE_IMG ="userShareCodeImg";
        public static final String TECH_SHARE_CODE_IMG ="userShareCodeImg";
    /**
     * status:submit 未接受，accept 同意，reject 拒绝，cancel 取消，complete 完成，failure 失效，sysReject 超时。 不传默认查全部订单
     */
    public static final Map<String, String> LABELS = new HashMap<String, String>(){{
        put(ORDER_STATUS_SUBMIT, "未接受");
        put(ORDER_STATUS_ACCEPT, "已接受");
        put(ORDER_STATUS_REJECTED, "已拒绝");
        put(ORDER_STATUS_COMPLETE, "已完成");
        put(ORDER_STATUS_FAILURE, "失效");
        put(ORDER_STATUS_OVERTIME, "超时");
        put(ORDER_STATUS_CANCEL, "取消");
    }};

    public static final String COUPON_STATUS_ALL = "0";
    public static final String COUPON_STATUS_ACCEPT = "1";
    public static final String COUPON_STATUS_COMPLETE = "2";
    public static final String COUPON_STATUS_EXPIRE = "3";

    public static final List<Entry> COUPON_STATUS_DATA = new ArrayList<Entry>(){{
        add(new Entry(Constant.COUPON_STATUS_ALL, ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_header_status)));
        add(new Entry(Constant.COUPON_STATUS_ACCEPT, ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_accept)));
        add(new Entry(Constant.COUPON_STATUS_COMPLETE, ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_complete)));
        add(new Entry(Constant.COUPON_STATUS_EXPIRE, ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_expire)));
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

    public static final String APP_BROWSER_USER_AGENT = "9358.tech.android.browser";

    public static final String FORMAT_DATE_TIME = "yyyyMMddHHmmss";

    /*******************************自定义事件统计*****************************/
    public static final String SHARE_COUPON = "couponShare";
    public static final String SHARE_BUSINESS_CARD = "businessCardShare";
    public static final String REGISTER_EVENT = "register";

    /**
     * ******************************* Request Code for activities ***************
     */
    public static final int REQUEST_CODE_FOR_USER_COUPON_LIST_ACTIVITY = 0x0001;
    public static final int REQUEST_CODE_FOR_CONSUME_ACTIVITY = 0x0002;
    public static final int REQUEST_CODE_FOR_ORDER_DETAIL_ACTIVITY = 0x0003;
}

