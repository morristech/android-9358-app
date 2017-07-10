package com.xmd.chat.order;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.appointment.AppointmentData;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.Technician;
import com.xmd.chat.message.OrderChatMessage;

import java.util.Date;

/**
 * Created by heyangya on 17-6-8.
 * 聊天创建或者管理订单
 */

public class OrderChatManager {
    private static final OrderChatManager ourInstance = new OrderChatManager();

    public static OrderChatManager getInstance() {
        return ourInstance;
    }

    private OrderChatManager() {
    }


    public static AppointmentData parseMessage(OrderChatMessage chatMessage) {
        AppointmentData data = new AppointmentData();

        //设置用户信息
        User user;
        if (chatMessage.getEmMessage().direct() == EMMessage.Direct.RECEIVE) {
            user = UserInfoServiceImpl.getInstance().getUserByChatId(chatMessage.getFromChatId());
        } else {
            user = UserInfoServiceImpl.getInstance().getUserByChatId(chatMessage.getToChatId());
        }
        if (user != null) {
            data.setCustomerId(user.getId());
        }
        data.setCustomerName(chatMessage.getCustomerName());
        data.setCustomerPhone(chatMessage.getCustomerPhone());

        //设置技师信息
        if (!TextUtils.isEmpty(chatMessage.getOrderTechId())) {
            Technician technician = new Technician();
            technician.setId(chatMessage.getOrderTechId());
            technician.setName(chatMessage.getOrderTechName());
            technician.setAvatarUrl(chatMessage.getOrderTechAvatar());
            data.setTechnician(technician);
        }

        //设置项目信息
        if (!TextUtils.isEmpty(chatMessage.getOrderServiceId())) {
            ServiceItem serviceItem = new ServiceItem();
            serviceItem.setId(chatMessage.getOrderServiceId());
            serviceItem.setName(chatMessage.getOrderServiceName());
            serviceItem.setDuration(String.valueOf(chatMessage.getOrderServiceDuration()));
            if (chatMessage.getOrderServicePrice() != null) {
                serviceItem.setPrice(String.valueOf(chatMessage.getOrderServicePrice()));
            }
            data.setServiceItem(serviceItem);
        }

        //设置到店时间
        if (chatMessage.getOrderServiceTime() != null && chatMessage.getOrderServiceTime() != 0) {
            data.setAppointmentTime(new Date(chatMessage.getOrderServiceTime()));
        }
        //设置服务时长
        if (chatMessage.getOrderServiceDuration() != null && chatMessage.getOrderServiceDuration() != 0) {
            data.setDuration(chatMessage.getOrderServiceDuration());
        }

        //设置支付信息
        if (chatMessage.getOrderPayMoney() != null && chatMessage.getOrderPayMoney() != 0) {
            data.setFontMoney(chatMessage.getOrderPayMoney());
        }

        //设置订单信息
        if (!TextUtils.isEmpty(chatMessage.getOrderId())) {
            data.setSubmitSuccess(true);
            data.setSubmitErrorString(null);
            data.setSubmitOrderId(chatMessage.getOrderId());
        }
        return data;
    }

    public static void fillMessage(OrderChatMessage chatMessage, AppointmentData data) {
        if (data.getCustomerName() != null) {
            chatMessage.setCustomerName(data.getCustomerName());
        }
        if (data.getCustomerPhone() != null) {
            chatMessage.setCustomerPhone(data.getCustomerPhone());
        }
        if (data.getAppointmentTime() != null) {
            chatMessage.setOrderServiceTime(data.getAppointmentTime().getTime());
        }
        if (data.getTechnician() != null) {
            chatMessage.setOrderTechId(data.getTechnician().getId());
            chatMessage.setOrderTechName(data.getTechnician().getName());
            chatMessage.setOrderTechAvatar(data.getTechnician().getAvatarUrl());
        }
        if (data.getServiceItem() != null) {
            chatMessage.setOrderServiceId(data.getServiceItem().getId());
            chatMessage.setOrderServiceName(data.getServiceItem().getName());
            if (data.getServiceItem().getPrice() != null) {
                chatMessage.setOrderServicePrice(Integer.parseInt(data.getServiceItem().getPrice()));
            }
        }
        if (data.getDuration() > 0) {
            chatMessage.setOrderServiceDuration(data.getDuration());
        }
        if (data.getFontMoney() != null && data.getFontMoney() > 0) {
            chatMessage.setOrderPayMoney(data.getFontMoney());
        }

        //设置生成的订单ID
        if (data.getSubmitOrderId() != null) {
            chatMessage.setOrderId(data.getSubmitOrderId());
        }
    }


    public static OrderChatMessage createMessage(String toChatId, String msgType, AppointmentData data) {
        OrderChatMessage msg = OrderChatMessage.create(toChatId, msgType);
        if (data != null) {
            fillMessage(msg, data);
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
