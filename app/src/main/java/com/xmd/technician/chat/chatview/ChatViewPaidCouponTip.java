package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;

/**
 * Created by sdcm on 16-5-11.
 */
public class ChatViewPaidCouponTip extends BaseChatView{
    private TextView mCouponTip;
    public ChatViewPaidCouponTip(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

    @Override
    protected void onInflateView() {
        LayoutInflater.from(context).inflate(
                R.layout.chat_received_paid_coupon_tip , this);
    }

    @Override
    protected void onFindViewById() {
        mCouponTip = (TextView) findViewById(R.id.paid_coupon_tip);
    }

    @Override
    protected void onSetUpView() {
        userAvatarView.setVisibility(GONE);
        timeStampView.setVisibility(GONE);
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        mCouponTip.setText(String.format("%s购买了您\"%s\"", message.getStringAttribute(ChatConstant.KEY_NAME, ""), content.substring(0, content.indexOf("&"))));
    }
}
