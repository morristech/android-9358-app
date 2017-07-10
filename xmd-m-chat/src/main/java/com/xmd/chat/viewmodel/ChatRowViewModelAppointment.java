package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.appointment.AppointmentData;
import com.xmd.appointment.AppointmentEvent;
import com.xmd.appointment.beans.AppointmentSetting;
import com.xmd.chat.MessageManager;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowAppointmentBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.OrderChatMessage;
import com.xmd.chat.order.OrderChatManager;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by mo on 17-7-1.
 * 文本消息
 */

public class ChatRowViewModelAppointment extends ChatRowViewModel {
    private final static String TAG = "ChatRowViewModelAppointment";
    private OrderChatMessage orderChatMessage;

    public ChatRowViewModelAppointment(ChatMessage chatMessage) {
        super(chatMessage);
        orderChatMessage = (OrderChatMessage) chatMessage;
        mAppointmentData = OrderChatManager.parseMessage(orderChatMessage);
        setupOperationButton();
        setupShowItem();
    }

    public boolean operateRefuseAndAccept;
    public boolean operateChangeAndConfirm;
    public boolean operateCancel;

    public boolean techVisible;
    public boolean serviceVisible;

    public boolean operateVisible;

    public AppointmentData mAppointmentData;

    public ObservableBoolean inProgress = new ObservableBoolean();

    public static View createView(ViewGroup parent) {
        ChatRowAppointmentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_appointment, parent, false);
        return binding.getRoot();
    }

    @Override
    public void onBindView(View view) {
        ChatRowAppointmentBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
    }

    @Override
    public void onUnbindView() {

    }

    //点击完善信息
    public void onClickCreateOrder() {
        EventBusSafeRegister.register(this);
//        LoginTechnician technician = LoginTechnician.getInstance();
//        boolean fixTech = technician.getRoles() != null && !technician.getRoles().contains(User.ROLE_FLOOR);
//        if (fixTech) {
//            Technician tech = new Technician();
//            tech.setId(technician.getUserId());
//            tech.setAvatarUrl(technician.getAvatarUrl());
//            tech.setName(technician.getNickName());
//            mAppointmentData.setTechnician(tech);
//            mAppointmentData.setFixTechnician(true);
//        }
        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SHOW, TAG, mAppointmentData));
    }

    //点击拒绝
    public void onClickRefuseOrder() {
        //发送拒绝消息
        orderChatMessage.setInnerProcessed("已拒绝");
        sendMessage(ChatMessage.MSG_TYPE_ORDER_REFUSE);
    }

    //点击取消
    public void onClickCancelOrder() {
        if (inProgress.get()) {
            return;
        }
        orderChatMessage.setInnerProcessed("已取消");
        sendMessage(ChatMessage.MSG_TYPE_ORDER_CANCEL);
    }

    //点击修改
    public void onClickChangeOrder() {
        if (inProgress.get()) {
            return;
        }
        AppointmentData data = OrderChatManager.parseMessage(orderChatMessage);
        EventBusSafeRegister.register(this);
        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SHOW, TAG, data));
    }

    //点击下单
    public void onClickSubmitOrder() {
        if (inProgress.get()) {
            return;
        }
        inProgress.set(true);
        EventBusSafeRegister.register(this);
        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SUBMIT, TAG, mAppointmentData));
    }

    private void sendMessage(String msgType) {
        MessageManager.getInstance().sendMessage(OrderChatManager.createMessage(chatMessage.getRemoteChatId(), msgType, mAppointmentData));
    }

    //显示或者隐藏按钮
    private void setupOperationButton() {
        operateVisible = false;
        operateRefuseAndAccept = false;
        operateChangeAndConfirm = false;
        operateCancel = false;
        if (AppointmentSetting.APPOINT_TYPE_CALL.equals(orderChatMessage.getOrderType())) {
            return;
        }
        if (TextUtils.isEmpty(orderChatMessage.getInnerProcessed())) {
            switch (orderChatMessage.getMsgType()) {
                case ChatMessage.MSG_TYPE_ORDER_START:
                    operateRefuseAndAccept = true;
                    break;
                case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
                    if (isFreeAppointment()) {
                        //免费预约才显示操作按钮
                        operateChangeAndConfirm = true;
                        operateCancel = true;
                    }
                    break;
                case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                case ChatMessage.MSG_TYPE_ORDER_CANCEL:
                case ChatMessage.MSG_TYPE_ORDER_REFUSE:
                    break;
            }
            operateVisible = operateRefuseAndAccept || operateChangeAndConfirm || operateCancel;
        } else {
            operateVisible = true;
        }
    }

    private void setupShowItem() {
        switch (orderChatMessage.getMsgType()) {
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
                techVisible = true;
                serviceVisible = true;
                break;
            default:
                techVisible = false;
                serviceVisible = false;
        }
    }

    @BindingAdapter("order_title")
    public static void bindOrderTitle(TextView view, OrderChatMessage data) {
        if (data != null) {
            view.setText(OrderChatMessage.getMsgTypeText(data.getMsgType()));
        }
    }

    @BindingAdapter("order_service_time")
    public static void bindServiceTime(TextView view, OrderChatMessage data) {
        if (data != null && data.getOrderServiceTime() != null && data.getOrderServiceTime() > 0) {
            view.setText(DateUtils.getSdf("MM月dd日 EEEE HH:mm").format(data.getOrderServiceTime()));
        } else {
            view.setText("待定");
        }
    }

    @BindingAdapter("order_pay_money")
    public static void bindPayMoney(TextView view, OrderChatMessage data) {
        if (data != null && data.getOrderPayMoney() != null && data.getOrderPayMoney() > 0) {
            view.setText("￥ " + data.getOrderPayMoney() / 100.0 + "元");
        }
    }

    private boolean isFreeAppointment() {
        return OrderChatManager.isFreeAppointment(mAppointmentData, orderChatMessage);
    }

    public OrderChatMessage getOrderChatMessage() {
        return orderChatMessage;
    }

    public void setOrderChatMessage(OrderChatMessage orderChatMessage) {
        this.orderChatMessage = orderChatMessage;
    }
}
