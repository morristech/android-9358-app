package com.xmd.manager.chat.chatview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;

/**
 * Created by sdcm on 16-4-15.
 */
public class ChatViewCoupon extends BaseChatView {

    private TextView mCouponValue;
    private TextView mCouponPeriod;

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
    }

    @Override
    protected void onSetupView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        String[] str = content.split("<b>|</b>|<span>|</span>|<i>|</i>");
        try {
            Drawable leftDrawable;
            if (str[1].equals("优惠券")) {
                leftDrawable = ResourceUtils.getDrawable(R.drawable.yhq);
            } else {
                leftDrawable = ResourceUtils.getDrawable(R.drawable.xjq);
            }
            leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
            mCouponValue.setCompoundDrawables(leftDrawable, null, null, null);
            mCouponValue.setText(str[3] + str[4]);
            mCouponPeriod.setText(str[5]);
        } catch (IndexOutOfBoundsException e) {

        }
    }
}
