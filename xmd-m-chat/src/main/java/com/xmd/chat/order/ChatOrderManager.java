package com.xmd.chat.order;

import com.hyphenate.chat.EMMessage;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.appointment.AppointmentData;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.Technician;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.OrderChatMessage;

import java.util.Date;

/**
 * Created by heyangya on 17-6-8.
 * 聊天创建或者管理订单
 */

public class ChatOrderManager {
    private static final ChatOrderManager ourInstance = new ChatOrderManager();

    public static ChatOrderManager getInstance() {
        return ourInstance;
    }

    private ChatOrderManager() {
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
            data.setCustomerName(user.getName());
        }

        //设置到店时间
        if (chatMessage.getOrderServiceTime() != null) {
            data.setTime(new Date(chatMessage.getOrderServiceTime()));
        }

        //设置技师信息
        if (chatMessage.getOrderTechId() != null) {
            Technician technician = new Technician();
            technician.setId(chatMessage.getOrderTechId());
            technician.setName(chatMessage.getOrderTechName());
            technician.setAvatarUrl(chatMessage.getOrderTechAvatar());
            data.setTechnician(technician);
        }

        //设置项目信息
        if (chatMessage.getOrderServiceId() != null) {
            ServiceItem serviceItem = new ServiceItem();
            serviceItem.setId(chatMessage.getOrderServiceId());
            serviceItem.setName(chatMessage.getOrderServiceName());
            data.setServiceItem(serviceItem);
        }

        //设置服务时长
        if (chatMessage.getOrderServiceDuration() != null) {
            data.setDuration(chatMessage.getOrderServiceDuration());
        }

        //设置支付信息
        if (chatMessage.getOrderPayMoney() != null) {
            data.setFontMoney(chatMessage.getOrderPayMoney());
        }
        return data;
    }

    public static void fillMessageWithAppointmentData(OrderChatMessage chatMessage, AppointmentData data) {
        if (data.getTime() != null) {
            chatMessage.setOrderServiceTime(data.getTime().getTime());
        }
        if (data.getTechnician() != null) {
            chatMessage.setOrderTechId(data.getTechnician().getId());
            chatMessage.setOrderTechName(data.getTechnician().getName());
            chatMessage.setOrderTechAvatar(data.getTechnician().getAvatarUrl());
        }
        if (data.getServiceItem() != null) {
            chatMessage.setOrderServiceId(data.getServiceItem().getId());
            chatMessage.setOrderServiceName(data.getServiceItem().getName());
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

    public static String getMsgTypeText(String msgType) {
        switch (msgType) {
            case ChatMessage.MSG_TYPE_ORDER_START:
                return "发起预约";
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
                return "拒绝预约";
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
                return "预约取消";
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
                return "预约确认";
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                return "预约成功";
        }
        return "预约消息";
    }

    public static OrderChatMessage createMessage(String toChatId, String msgType, AppointmentData data) {
        String content = getMsgTypeText(msgType);
        OrderChatMessage msg = new OrderChatMessage(EMMessage.createTxtSendMessage(content, toChatId), msgType);
        if (data != null) {
            fillMessageWithAppointmentData(msg, data);
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
