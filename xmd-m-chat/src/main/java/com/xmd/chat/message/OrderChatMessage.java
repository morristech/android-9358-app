package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;

/**
 * Created by heyangya on 17-6-7.
 * 预约消息
 */

public class OrderChatMessage extends ChatMessage {
    private final static String ATTR_ORDER_CUSTOMER_PHONE = "orderCustomerPhone"; //客户手机
    private final static String ATTR_ORDER_CUSTOMER_NAME = "orderCustomerName"; //客户名称
    private final static String ATTR_ORDER_TECH_ID = "orderTechId"; //技师ID
    private final static String ATTR_ORDER_TECH_NAME = "orderTechName";//技师名字
    private final static String ATTR_ORDER_TECH_AVATAR = "orderTechAvatar"; //技师头像
    private final static String ATTR_ORDER_SERVICE_ID = "orderServiceId";//项目ID
    private final static String ATTR_ORDER_SERVICE_NAME = "orderServiceName";//项目名字
    private final static String ATTR_ORDER_SERVICE_PRICE = "orderServicePrice"; //项目价格
    private final static String ATTR_ORDER_SERVICE_TIME = "orderServiceTime";// 到店时间
    private final static String ATTR_ORDER_SERVICE_DURATION = "orderServiceDuration";//服务时长，单位分钟
    private final static String ATTR_ORDER_ID = "orderId";//订单ID
    private final static String ATTR_ORDER_PAY_MONEY = "orderPayMoney";////支付金额，单位为分 Integer


    //内部是否已处理此消息
    private static final String ATTR_INNER_PROCESSED = "inner_processed";

    public OrderChatMessage(EMMessage emMessage, String msgType) {
        super(emMessage, msgType);
    }

    public String getCustomerName() {
        return getSafeStringAttribute(ATTR_ORDER_CUSTOMER_NAME);
    }

    public void setCustomerName(String customerName) {
        setAttr(ATTR_ORDER_CUSTOMER_NAME, customerName);
    }

    public String getCustomerPhone() {
        return getSafeStringAttribute(ATTR_ORDER_CUSTOMER_PHONE);
    }

    public void setCustomerPhone(String customerPhone) {
        setAttr(ATTR_ORDER_CUSTOMER_PHONE, customerPhone);
    }

    public String getOrderTechId() {
        return getSafeStringAttribute(ATTR_ORDER_TECH_ID);
    }

    public void setOrderTechId(String orderTechId) {
        setAttr(ATTR_ORDER_TECH_ID, orderTechId);
    }

    public String getOrderTechName() {
        return getSafeStringAttribute(ATTR_ORDER_TECH_NAME);
    }

    public void setOrderTechName(String orderTechName) {
        setAttr(ATTR_ORDER_TECH_NAME, orderTechName);
    }

    public String getOrderTechAvatar() {
        return getSafeStringAttribute(ATTR_ORDER_TECH_AVATAR);
    }

    public void setOrderTechAvatar(String orderTechAvatar) {
        setAttr(ATTR_ORDER_TECH_AVATAR, orderTechAvatar);
    }

    public String getOrderServiceId() {
        return getSafeStringAttribute(ATTR_ORDER_SERVICE_ID);
    }

    public void setOrderServiceId(String orderServiceId) {
        setAttr(ATTR_ORDER_SERVICE_ID, orderServiceId);
    }

    public String getOrderServiceName() {
        return getSafeStringAttribute(ATTR_ORDER_SERVICE_NAME);
    }

    public void setOrderServiceName(String orderServiceName) {
        setAttr(ATTR_ORDER_SERVICE_NAME, orderServiceName);
    }

    public Integer getOrderServicePrice() {
        return getSafeIntergeAttribute(ATTR_ORDER_SERVICE_PRICE);
    }

    public void setOrderServicePrice(Integer orderServicePrice) {
        setAttr(ATTR_ORDER_SERVICE_PRICE, orderServicePrice);
    }

    public Long getOrderServiceTime() {
        return getSafeLongAttribute(ATTR_ORDER_SERVICE_TIME);
    }

    public void setOrderServiceTime(Long orderServiceTime) {
        setAttr(ATTR_ORDER_SERVICE_TIME, orderServiceTime);
    }

    public Integer getOrderServiceDuration() {
        return getSafeIntergeAttribute(ATTR_ORDER_SERVICE_DURATION);
    }

    public void setOrderServiceDuration(Integer orderServiceDuration) {
        setAttr(ATTR_ORDER_SERVICE_DURATION, orderServiceDuration);
    }

    public String getOrderId() {
        return getSafeStringAttribute(ATTR_ORDER_ID);
    }

    public void setOrderId(String orderId) {
        setAttr(ATTR_ORDER_ID, orderId);
    }

    public Integer getOrderPayMoney() {
        return getSafeIntergeAttribute(ATTR_ORDER_PAY_MONEY);
    }

    public void setOrderPayMoney(Integer orderPayMoney) {
        setAttr(ATTR_ORDER_PAY_MONEY, orderPayMoney);
    }

    public String getInnerProcessed() {
        return getSafeStringAttribute(ATTR_INNER_PROCESSED);
    }

    public void setInnerProcessed(String processedDesc) {
        setAttr(ATTR_INNER_PROCESSED, processedDesc);
    }
}
