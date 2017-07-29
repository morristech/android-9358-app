package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;
import com.xmd.appointment.AppointmentData;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.Technician;

import java.util.Date;

/**
 * Created by heyangya on 17-6-7.
 * 预约消息
 */

public class OrderChatMessage extends ChatMessage {
    private final static String ATTR_ORDER_TYPE = "orderAppointType"; //预约类型,电话，免费，订金，全额
    private final static String ATTR_ORDER_CUSTOMER_ID = "orderCustomerId";
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


    public OrderChatMessage(EMMessage emMessage) {
        super(emMessage);
    }

    public static OrderChatMessage create(String remoteChatId, String msgType) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(getMsgTypeText(msgType), remoteChatId);
        OrderChatMessage orderChatMessage = new OrderChatMessage(emMessage);
        orderChatMessage.setMsgType(msgType);
        return orderChatMessage;
    }

    public static OrderChatMessage createRequestOrderMessage(String remoteChatId) {
        EMMessage emMessage = EMMessage.createTxtSendMessage("选项目、约技师，\n线上预约，方便快捷～", remoteChatId);
        OrderChatMessage orderChatMessage = new OrderChatMessage(emMessage);
        orderChatMessage.setMsgType(ChatMessage.MSG_TYPE_ORDER_REQUEST);
        return orderChatMessage;
    }

    public String getCustomerName() {
        return getSafeStringAttribute(ATTR_ORDER_CUSTOMER_NAME);
    }

    public void setCustomerName(String customerName) {
        setAttr(ATTR_ORDER_CUSTOMER_NAME, customerName);
    }

    public String getCustomerId() {
        return getSafeStringAttribute(ATTR_ORDER_CUSTOMER_ID);
    }

    public void setCustomerId(String customerId) {
        setAttr(ATTR_ORDER_CUSTOMER_NAME, customerId);
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
        return getSafeIntegerAttribute(ATTR_ORDER_SERVICE_PRICE);
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
        return getSafeIntegerAttribute(ATTR_ORDER_SERVICE_DURATION);
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
        return getSafeIntegerAttribute(ATTR_ORDER_PAY_MONEY);
    }

    public void setOrderPayMoney(Integer orderPayMoney) {
        setAttr(ATTR_ORDER_PAY_MONEY, orderPayMoney);
    }


    public String getOrderType() {
        return getSafeStringAttribute(ATTR_ORDER_TYPE);
    }

    public void setOrderType(String type) {
        setAttr(ATTR_ORDER_TYPE, type);
    }


    //设置订单数据
    public void setOrderData(AppointmentData data) {
        if (data.getCustomerId() != null) {
            setCustomerId(data.getCustomerId());
        }
        if (data.getCustomerName() != null) {
            setCustomerName(data.getCustomerName());
        }
        if (data.getCustomerPhone() != null) {
            setCustomerPhone(data.getCustomerPhone());
        }
        if (data.getAppointmentTime() != null) {
            setOrderServiceTime(data.getAppointmentTime().getTime());
        }
        if (data.getTechnician() != null) {
            setOrderTechId(data.getTechnician().getId());
            setOrderTechName(data.getTechnician().getName());
            setOrderTechAvatar(data.getTechnician().getAvatarUrl());
        }
        if (data.getServiceItem() != null) {
            setOrderServiceId(data.getServiceItem().getId());
            setOrderServiceName(data.getServiceItem().getName());
            if (data.getServiceItem().getPrice() != null) {
                setOrderServicePrice(Integer.parseInt(data.getServiceItem().getPrice()));
            }
        }
        if (data.getDuration() > 0) {
            setOrderServiceDuration(data.getDuration());
        }
        if (data.getFontMoney() != null && data.getFontMoney() > 0) {
            setOrderPayMoney(data.getFontMoney());
        }

        //设置生成的订单ID
        if (data.getSubmitOrderId() != null) {
            setOrderId(data.getSubmitOrderId());
        }
    }

    public AppointmentData parseOrderData() {
        AppointmentData data = new AppointmentData();
        data.setCustomerId(getCustomerId());
        if (getCustomerName() != null) {
            data.setCustomerName(getCustomerName());
        }
        if (getCustomerPhone() != null) {
            data.setCustomerPhone(getCustomerPhone());
        }
        if (getOrderServiceTime() != null) {
            data.setAppointmentTime(new Date(getOrderServiceTime()));
        }
        if (getOrderTechId() != null) {
            Technician technician = new Technician();
            technician.setId(getOrderTechId());
            technician.setName(getOrderTechName());
            technician.setAvatarUrl(getOrderTechAvatar());
            data.setTechnician(technician);
        }
        if (getOrderServiceId() != null) {
            ServiceItem serviceItem = new ServiceItem();
            serviceItem.setId(getOrderServiceId());
            serviceItem.setName(getOrderServiceName());
            if (getOrderServicePrice() != null) {
                serviceItem.setPrice(String.valueOf(getOrderServicePrice()));
            }
            if (getOrderServiceDuration() != null) {
                serviceItem.setDuration(String.valueOf(getOrderServiceDuration()));
                data.setDuration(getOrderServiceDuration());
            }
            data.setServiceItem(serviceItem);
        }
        if (getOrderPayMoney() != null) {
            data.setFontMoney(getOrderPayMoney());
        }
        if (getOrderId() != null) {
            data.setSubmitOrderId(getOrderId());
        }
        return data;
    }

    public static OrderChatMessage create(String toChatId, String msgType, AppointmentData data) {
        OrderChatMessage msg = OrderChatMessage.create(toChatId, msgType);
        if (data != null) {
            msg.setOrderData(data);
        }
        return msg;
    }

    public static boolean isFreeAppointment(AppointmentData data, OrderChatMessage msg) {
        if (data != null) {
            return data.getFontMoney() == null || data.getFontMoney() == 0;
        } else {
            return msg.getOrderPayMoney() == null || msg.getOrderPayMoney() == 0;
        }
    }
}
