package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.appointment.AppointmentData;
import com.xmd.appointment.AppointmentEvent;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.Technician;
import com.xmd.chat.ChatMessage;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.OrderChatMessage;
import com.xmd.technician.Adapter.EaseMessageAdapter;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.databinding.ChatRowViewSubOrderBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;


/**
 * Created by heyangya on 17-6-7.
 * 预约聊天视图
 */

public class ChatRowAppointmentView extends BaseEaseChatView {
    private OrderChatMessage mChatMessage;
    private ChatRowViewSubOrderBinding mBinding;
    public boolean operateRefuseAndAccept;
    public boolean operateChangeAndConfirm;
    public boolean operateCancel;

    public boolean operateVisible;

    public ChatRowAppointmentView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);

    }

    @Override
    protected void onInflateView() {
        ViewGroup rootView = (ViewGroup) mInflater.inflate(mEMMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.chat_row_view_received : R.layout.chat_row_view_sent, this);
        mBinding = DataBindingUtil.inflate(mInflater, R.layout.chat_row_view_sub_order, (ViewGroup) rootView.findViewById(R.id.bubble), true);
    }

    @Override
    protected void onFindViewById() {

    }

    @Override
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
        mBinding.setData(mChatMessage);
        mBinding.executePendingBindings();
    }

    private void setUpOperationButton() {
        operateVisible = false;
        operateRefuseAndAccept = false;
        operateChangeAndConfirm = false;
        operateCancel = false;
        if (TextUtils.isEmpty(mChatMessage.getInnerProcessed())) {
            switch (mChatMessage.getMsgType()) {
                case ChatMessage.MSG_TYPE_ORDER_START:
                    operateRefuseAndAccept = true;
                    break;
                case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
                    operateChangeAndConfirm = true;
                    operateCancel = true;
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

    @Override
    protected void onSetUpView(EMMessage message) {
        mBubbleLayout.setOnClickListener(null);
        mChatMessage = (OrderChatMessage) ChatMessageFactory.get(message);
        setUpOperationButton();
        mBinding.setHandler(this);
        mBinding.setData(mChatMessage);
        mBinding.executePendingBindings();

        if (mEMMessage.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (mEMMessage.status()) {
                case CREATE:
                    mProgressBar.setVisibility(View.GONE);
                    mStatusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    mProgressBar.setVisibility(View.GONE);
                    mStatusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    String errorCode = mEMMessage.getStringAttribute(ChatConstant.KEY_ERROR_CODE, ChatConstant.ERROR_SERVER_NOT_REACHABLE);
                    if (ChatConstant.ERROR_IN_BLACKLIST.equals(errorCode)) {
                        mProgressBar.setVisibility(View.GONE);
                        mStatusView.setVisibility(View.GONE);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        mStatusView.setVisibility(View.VISIBLE);
                    }
                    break;
                case INPROGRESS:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mStatusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            if (!mEMMessage.isAcked() && mEMMessage.getChatType() == EMMessage.ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(mEMMessage.getFrom(), mEMMessage.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onBubbleClick() {

    }


    @BindingAdapter("order_title")
    public static void bindOrderTitle(TextView view, OrderChatMessage data) {
        if (data != null) {
            view.setText(getMsgTypeText(data.getMsgType()));
        }
    }

    @BindingAdapter("order_service_time")
    public static void bindServiceTime(TextView view, OrderChatMessage data) {
        if (data != null && data.getOrderServiceTime() != null) {
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


    public void onClickCreateOrder() {
        AppointmentData data = parseMessage(mChatMessage);
        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SHOW, data));
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onAppointmentEvent(AppointmentEvent event) {
        if (event.getCmd() == AppointmentEvent.CMD_HIDE) {
            AppointmentData data = event.getData();
            if (data != null) {
                //发送预约确定
                mChatMessage.setInnerProcessed("已处理");
                String remoteChatId = mChatMessage.getToChatId();//FIXME
                String msgType = ChatMessage.MSG_TYPE_ORDER_CONFIRM;
                String content = getMsgTypeText(msgType);
                OrderChatMessage newMessage = new OrderChatMessage(EMMessage.createTxtSendMessage(content, remoteChatId), msgType);
                fillMessageWithAppointmentData(newMessage, data);
                ((EaseMessageAdapter) mAdapter).getSentMessageHelper().sendMessage(newMessage);
            }
            EventBus.getDefault().unregister(this);
        }
    }

    public void onClickRefuseOrder() {
        //发送拒绝消息
        mChatMessage.setInnerProcessed("已拒绝");
        sendEmptyMessage(ChatMessage.MSG_TYPE_ORDER_REFUSE);
    }

    public void onClickCancelOrder() {
        mChatMessage.setInnerProcessed("已取消");
        sendEmptyMessage(ChatMessage.MSG_TYPE_ORDER_CANCEL);
    }

    public void onClickChangeOrder() {
        AppointmentData data = parseMessage(mChatMessage);
        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SHOW, data));
        EventBus.getDefault().register(this);
    }


    public void onClickSubmitOrder() {
        //TODO
        mChatMessage.setInnerProcessed("已下单");
        sendEmptyMessage(ChatMessage.MSG_TYPE_ORDER_SUCCESS);
    }

    private AppointmentData parseMessage(OrderChatMessage chatMessage) {
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
//        technician.setId(LoginTechnician.getInstance().getUserId());
//        technician.setName(LoginTechnician.getInstance().getNickName());
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
        return data;
    }

    private void fillMessageWithAppointmentData(OrderChatMessage chatMessage, AppointmentData data) {
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
    }

    private static String getMsgTypeText(String msgType) {
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

    private void sendEmptyMessage(String msgType) {
        String remoteChatId = mChatMessage.getToChatId();//FIXME
        String content = getMsgTypeText(msgType);
        OrderChatMessage refuseMessage = new OrderChatMessage(EMMessage.createTxtSendMessage(content, remoteChatId), msgType);
        ((EaseMessageAdapter) mAdapter).getSentMessageHelper().sendMessage(refuseMessage);
    }
}
