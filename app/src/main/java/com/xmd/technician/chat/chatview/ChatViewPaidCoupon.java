package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;

/**
 * Created by sdcm on 16-4-18.
 */
public class ChatViewPaidCoupon extends BaseChatView{
    private TextView mCouponValue;
    private TextView mCouponPeriod;
    private TextView mCouponTip;

    public ChatViewPaidCoupon(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

    /*@Override
    protected void onInflateView() {

    }*/

    @Override
    protected void onFindViewById() {
        findViewById(R.id.paid_coupon_container).setVisibility(VISIBLE);
        mCouponValue = (TextView) findViewById(R.id.paid_coupon_amount);
        mCouponPeriod = (TextView) findViewById(R.id.paid_coupon_period);
        mCouponTip = (TextView) findViewById(R.id.paid_coupon_tip);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        String[] str = content.split("<b>|</b>|<span>|</span>|<i>|</i>");

        try {
            mCouponValue.setText(str[2]+"￥"+str[3]);
            mCouponPeriod.setText(str[5]);
        }catch (IndexOutOfBoundsException e){

        }
        if(mCouponTip != null){
            mCouponTip.setVisibility(VISIBLE);
            mCouponTip.setText(String.format("%s购买了您＂%s＂点钟券", message.getStringAttribute(ChatConstant.KEY_NAME, ""), str[2]));
        }
    }
}
