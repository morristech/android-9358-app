package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.Logger;

/**
 * Created by Lhj on 17-4-7.
 */

public class ChatRowPaidCouponView extends BaseEaseChatView {
    private TextView mCouponValue;
    private TextView mCouponPeriod;
    private TextView mCouponType;
    private TextView mCouponTip;

    public ChatRowPaidCouponView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(mEMMessage.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.chat_row_received_paid_coupon : R.layout.chat_row_sent_paid_coupon, this);
    }

    @Override
    protected void onFindViewById() {
        mCouponValue = (TextView) findViewById(R.id.paid_coupon_amount);
        mCouponPeriod = (TextView) findViewById(R.id.coupon_period);
        mCouponType = (TextView) findViewById(R.id.paid_coupon_type);
        mCouponTip = (TextView) findViewById(R.id.paid_coupon_tip);
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
                    if(ChatConstant.ERROR_IN_BLACKLIST.equals(errorCode)){
                        mProgressBar.setVisibility(View.GONE);
                        mStatusView.setVisibility(View.GONE);
                    }else {
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
        String[] str = content.split("<b>|</b>|<span>|</span>|<i>|</i>");
        if (mEMMessage.direct() == EMMessage.Direct.SEND) {
            try {
                mCouponType.setText(str[1]);
                mCouponValue.setText(str[2] + str[3] + str[4]);
                mCouponPeriod.setText(str[5]);
            } catch (IndexOutOfBoundsException e) {
                Logger.d("chat", "couponException");
            }
        } else {
            mCouponTip.setText(String.format("%s购买了您＂%s＂点钟券", mEMMessage.getStringAttribute(ChatConstant.KEY_NAME, ""), str[2]));
        }

        handleTextMessage();
    }

    @Override
    protected void onBubbleClick() {

    }

}
