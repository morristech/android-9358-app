package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.user.User;
import com.xmd.appointment.AppointmentData;
import com.xmd.appointment.AppointmentEvent;
import com.xmd.appointment.beans.AppointmentSetting;
import com.xmd.appointment.beans.Technician;
import com.xmd.chat.AccountManager;
import com.xmd.chat.MessageManager;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowAppointmentBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.OrderChatMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Created by mo on 17-7-1.
 * 文本消息
 */

public class ChatRowViewModelAppointment extends ChatRowViewModel {
    private final static String TAG = "ChatRowViewModelAppointment";
    private OrderChatMessage orderChatMessage;
    private ChatRowAppointmentBinding binding;

    public ChatRowViewModelAppointment(ChatMessage chatMessage) {
        super(chatMessage);
        orderChatMessage = (OrderChatMessage) chatMessage;
        mAppointmentData = orderChatMessage.parseOrderData(); //FIXME
        setupOperationButton();
        setupShowItem();
    }

    public boolean operateRefuseAndAccept;
    public boolean operateChangeAndConfirm;
    public boolean operateCancel;

    public boolean techVisible;
    public boolean serviceVisible;

    private String status;
    public boolean showOperateSplitLine;

    public AppointmentData mAppointmentData;

    public ObservableBoolean inProgress = new ObservableBoolean();

    public static View createView(ViewGroup parent) {
        ChatRowAppointmentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_appointment, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    protected boolean contentViewMatchParent() {
        return true;
    }

    @Override
    public void onUnbindView() {

    }

    //点击完善信息
    public void onClickCreateOrder() {
        EventBusSafeRegister.register(this);
        User currentUser = AccountManager.getInstance().getUser();
        if (currentUser == null) {
            return;
        }
        boolean fixTech = currentUser.getRoles() != null && !currentUser.getRoles().contains(User.ROLE_FLOOR);
        if (fixTech) {
            Technician tech = new Technician();
            tech.setId(currentUser.getId());
            tech.setAvatarUrl(currentUser.getAvatar());
            tech.setName(currentUser.getName());
            mAppointmentData.setTechnician(tech);
            mAppointmentData.setFixTechnician(true);
        }
        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SHOW, TAG, mAppointmentData));
    }

    //点击拒绝
    public void onClickRefuseOrder() {
        //发送拒绝消息
        operateRefuseAndAccept = false;
        status = "已拒绝";
        binding.setData(this);
        binding.executePendingBindings();
        orderChatMessage.setInnerProcessed(status);
        sendMessage(ChatMessage.MSG_TYPE_ORDER_REFUSE);
    }

    public String status() {
        return status;
    }

    //点击取消
    public void onClickCancelOrder() {
        if (inProgress.get()) {
            return;
        }
        operateChangeAndConfirm = false;
        operateCancel = false;
        status = "已取消";
        binding.setData(this);
        binding.executePendingBindings();
        orderChatMessage.setInnerProcessed(status);
        sendMessage(ChatMessage.MSG_TYPE_ORDER_CANCEL);
    }

    //点击修改
    public void onClickChangeOrder() {
        if (inProgress.get()) {
            return;
        }
        EventBusSafeRegister.register(this);
        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SHOW, TAG, mAppointmentData));
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
        MessageManager.getInstance().sendMessage(OrderChatMessage.create(chatMessage.getRemoteChatId(), msgType, mAppointmentData));
    }

    //显示或者隐藏按钮
    private void setupOperationButton() {
        showOperateSplitLine = false;
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
            showOperateSplitLine = operateRefuseAndAccept || operateChangeAndConfirm || operateCancel;
        } else {
            status = orderChatMessage.getInnerProcessed();
            showOperateSplitLine = true;
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
        if (mAppointmentData != null) {
            return mAppointmentData.getFontMoney() == null || mAppointmentData.getFontMoney() == 0;
        } else {
            return orderChatMessage.getOrderPayMoney() == null || orderChatMessage.getOrderPayMoney() == 0;
        }
    }

    public OrderChatMessage getOrderChatMessage() {
        return orderChatMessage;
    }

    public void setOrderChatMessage(OrderChatMessage orderChatMessage) {
        this.orderChatMessage = orderChatMessage;
    }

    @Subscribe
    public void onAppointmentEvent(AppointmentEvent event) {
        if (!TAG.equals(event.getTag())) {
            return;
        }
        if (event.getCmd() == AppointmentEvent.CMD_HIDE) {
            EventBusSafeRegister.unregister(this);
            AppointmentData data = event.getData();
            if (data != null) {
                mAppointmentData = data;
                if (isFreeAppointment()) {
                    //免费预约时，先发送预约确定，客服点击之后才生成订单
                    status = "已处理";
                    operateRefuseAndAccept = false;
                    operateChangeAndConfirm = false;
                    binding.setData(this);
                    binding.executePendingBindings();
                    orderChatMessage.setInnerProcessed("已处理");
                    sendMessage(ChatMessage.MSG_TYPE_ORDER_CONFIRM);
                } else {
                    //付费预约，直接生成订单
                    orderChatMessage.setOrderData(mAppointmentData);
                    onClickSubmitOrder();
                }
            }
        } else if (event.getCmd() == AppointmentEvent.CMD_SUBMIT_RESULT) {
            inProgress.set(false);
            EventBusSafeRegister.unregister(this);
            if (event.getData().isSubmitSuccess()) {
                mAppointmentData = event.getData();
                XLogger.i("submit ok, order: " + mAppointmentData.getSubmitOrderId());
                if (isFreeAppointment()) {
                    //免费预约，生成订单后，直接提示成功
                    status = "已生成订单";
                    operateCancel = false;
                    operateChangeAndConfirm = false;
                    binding.setData(this);
                    binding.executePendingBindings();
                    orderChatMessage.setInnerProcessed(status);
                    sendMessage(ChatMessage.MSG_TYPE_ORDER_SUCCESS);
                } else {
                    //付费预约，生成订单后，发送预约确定信息给对方支付
                    status = "已处理";
                    operateCancel = false;
                    operateChangeAndConfirm = false;
                    binding.setData(this);
                    binding.executePendingBindings();
                    orderChatMessage.setInnerProcessed(status);
                    sendMessage(ChatMessage.MSG_TYPE_ORDER_CONFIRM);
                }
            } else {
                XToast.show("生成订单失败：" + event.getData().getSubmitErrorString());
            }
        }
    }
}
