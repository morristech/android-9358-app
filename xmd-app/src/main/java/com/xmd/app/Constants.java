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
}
