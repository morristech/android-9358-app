package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatHelper;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lhj on 17-3-30.
 */

public class ChatRowOrderView extends BaseEaseChatView {

    private TextView mTitle;
    private TextView mTimeHint;
    private TextView mTime;
    private TextView mServiceHint;
    private TextView mServiceItem;
    private Button mAcceptOrder;
    private Button mRefuseOrder;

    public ChatRowOrderView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(mEMMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.chat_row_received_order : R.layout.chat_row_sent_order, this);
    }

    @Override
    protected void onFindViewById() {
        mTitle = (TextView) findViewById(R.id.order_title);
        mTime = (TextView) findViewById(R.id.order_time);
        mTimeHint = (TextView) findViewById(R.id.order_time_hint);
        mServiceHint = (TextView) findViewById(R.id.order_service_hint);
        mServiceItem = (TextView) findViewById(R.id.order_service);
        mAcceptOrder = (Button) findViewById(R.id.order_accept);
        mRefuseOrder = (Button) findViewById(R.id.order_refuse);
    }

    protected void handleTextMessage() {
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
                        mProgressBar.setVisibility(View.VISIBLE);
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
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) mEMMessage.getBody();
        String content = body.getMessage();
        String orderId = mEMMessage.getStringAttribute(ChatConstant.KEY_ORDER_ID, "");
        if (Utils.isNotEmpty(orderId)) {
            if (Utils.isNotEmpty(ChatHelper.getOrderMap().get(orderId))) {
                mAcceptOrder.setEnabled(false);
                mRefuseOrder.setEnabled(false);
            } else {
                mAcceptOrder.setEnabled(true);
                mRefuseOrder.setEnabled(true);
            }
        } else if (content.contains("发起预约")) {
            mAcceptOrder.setEnabled(true);
            mRefuseOrder.setEnabled(true);
        } else {
            mAcceptOrder.setEnabled(false);
            mRefuseOrder.setEnabled(false);
        }

        String[] str = content.split("<b>|<span>|</span>|<br>|</b>");

        try {
            mTitle.setText(str[1]);
            mTimeHint.setText(str[3]);
            mTime.setText(str[4]);
            mServiceHint.setText(str[6]);
            mServiceItem.setText(str[7]);
        } catch (IndexOutOfBoundsException e) {

        }
        mAcceptOrder.setOnClickListener(v -> {
            String orderContent = content.replace("发起预约", "接受预约");
            mEMMessage.addBody(new EMTextMessageBody(orderContent));
            ChatHelper.getOrderMap().put(orderId, orderContent);
            doManageOrder(orderId, Constant.ORDER_STATUS_ACCEPT, orderContent);
            mAdapter.notifyDataSetInvalidated();
        });
        mRefuseOrder.setOnClickListener(v -> {
            String orderContent = content.replace("发起预约", "拒绝预约");
            mEMMessage.addBody(new EMTextMessageBody(orderContent));
            ChatHelper.getOrderMap().put(orderId, orderContent);
            doManageOrder(orderId, Constant.ORDER_STATUS_REJECTED, orderContent);

            mAdapter.notifyDataSetInvalidated();
        });
        handleTextMessage();
    }

    @Override
    protected void onBubbleClick() {

    }

    private void handleMessage() {

    }

    private void doManageOrder(String orderId, String type, String reason) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PROCESS_TYPE, type);
        params.put(RequestConstant.KEY_ID, orderId);
        params.put(RequestConstant.KEY_REASON, reason);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANAGE_ORDER, params);
    }


}