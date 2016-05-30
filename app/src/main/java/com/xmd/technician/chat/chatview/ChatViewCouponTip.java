package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.window.BaseActivity;

/**
 * Created by sdcm on 16-5-30.
 */
public class ChatViewCouponTip extends BaseChatView{
    private TextView mCouponTip;
    public ChatViewCouponTip(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

    @Override
    protected void onInflateView() {
        LayoutInflater.from(context).inflate(
                R.layout.chat_received_coupon_tip , this);
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
        mCouponTip.setText(String.format("%s领取了您的\"%s\"", message.getStringAttribute(ChatConstant.KEY_NAME, ""), content));
    }
}
