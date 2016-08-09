package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.technician.R;

/**
 * Created by sdcm on 16-4-15.
 */
public class ChatViewCoupon extends BaseChatView{

    private TextView mCouponValue;
    private TextView mCouponPeriod;
    private TextView mCouponType;

    public ChatViewCoupon(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

/*    @Override
    protected void onInflateView() {

    }*/

    @Override
    protected void onFindViewById() {
        findViewById(R.id.coupon_container).setVisibility(VISIBLE);
        mCouponValue = (TextView) findViewById(R.id.coupon_amount);
        mCouponPeriod = (TextView) findViewById(R.id.coupon_period);
        mCouponType = (TextView) findViewById(R.id.coupon_type);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        String[] str = content.split("<b>|</b>|<span>|</span>|<i>|</i>");

        try {
            mCouponType.setText(str[1]);
         //   mCouponValue.setText("￥"+str[3]+str[4]);
            mCouponValue.setText("￥"+str[3]);
            mCouponPeriod.setText(str[5]);
        }catch (IndexOutOfBoundsException e){

        }
    }
}
