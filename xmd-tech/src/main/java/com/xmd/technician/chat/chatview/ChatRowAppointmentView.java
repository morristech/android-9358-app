package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.appointment.AppointmentData;
import com.xmd.appointment.AppointmentEvent;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.OrderChatMessage;
import com.xmd.chat.order.OrderChatManager;
import com.xmd.technician.Adapter.EaseMessageAdapter;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.databinding.ChatRowViewSubOrderBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Created by heyangya on 17-6-7.
 * 预约聊天视图
 */

public class ChatRowAppointmentView extends BaseEaseChatView {
    private static final String TAG = "ChatRowAppointmentView";
    private OrderChatMessage mChatMessage;
    private ChatRowViewSubOrderBinding mBinding;
    public boolean operateRefuseAndAccept;
    public boolean operateChangeAndConfirm;
    public boolean operateCancel;

    public boolean techVisible;
    public boolean serviceVisible;

    public boolean operateVisible;

    public AppointmentData mAppointmentData;

    public ObservableBoolean inProgress = new ObservableBoolean();

    private String remoteChatId;

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

    //显示或者隐藏按钮
    private void setupOperationButton() {
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
        switch (mChatMessage.getMsgType()) {
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

    @Override
    protected void onSetUpView(EMMessage message) {
        mBubbleLayout.setOnClickListener(null);
        mChatMessage = (OrderChatMessage) ChatMessageFactory.get(message);
        mAppointmentData = OrderChatManager.parseMessage(mChatMessage);
        setupOperationButton();
        setupShowItem();
        mBinding.setHandler(this);
        mBinding.setData(mChatMessage);
        mBinding.executePendingBindings();

        remoteChatId = message.direct() == EMMessage.Direct.RECEIVE ? message.getFrom() : message.getTo();

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
            view.setText(OrderChatManager.getMsgTypeText(data.getMsgType()));
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
                OrderChatManager.fillMessage(mChatMessage, mAppointmentData);
                mChatMessage.setInnerProcessed("已处理");
                if (isFreeAppointment()) {
                    //免费预约时，先发送预约确定，客服点击之后才生成订单
                    sendMessage(ChatMessage.MSG_TYPE_ORDER_CONFIRM);
                } else {
                    //付费预约，直接生成订单
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
                    mChatMessage.setInnerProcessed("已生成订单");
                    sendMessage(ChatMessage.MSG_TYPE_ORDER_SUCCESS);
                } else {
                    //付费预约，生成订单后，发送预约确定信息给对方支付
                    sendMessage(ChatMessage.MSG_TYPE_ORDER_CONFIRM);
                }
            } else {
                XToast.show("生成订单失败：" + event.getData().getSubmitErrorString());
            }
        }
    }


    //点击完善信息
    public void onClickCreateOrder() {
        EventBusSafeRegister.register(this);
        EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SHOW, TAG, mAppointmentData));
    }

    //点击拒绝
    public void onClickRefuseOrder() {
        //发送拒绝消息
        mChatMessage.setInnerProcessed("已拒绝");
        sendMessage(ChatMessage.MSG_TYPE_ORDER_REFUSE);
    }

    //点击取消
    public void onClickCancelOrder() {
        if (inProgress.get()) {
            return;
        }
        mChatMessage.setInnerProcessed("已取消");
        sendMessage(ChatMessage.MSG_TYPE_ORDER_CANCEL);
    }

    //点击修改
    public void onClickChangeOrder() {
        if (inProgress.get()) {
            return;
        }
        AppointmentData data = OrderChatManager.parseMessage(mChatMessage);
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
        ((EaseMessageAdapter) mAdapter)
                .getSentMessageHelper()
                .sendMessage(OrderChatManager.createMessage(remoteChatId, msgType, mAppointmentData));
    }

    private boolean isFreeAppointment() {
        return OrderChatManager.isFreeAppointment(mAppointmentData, mChatMessage);
    }
}
