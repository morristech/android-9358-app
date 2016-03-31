package com.xmd.technician.window;

import android.os.Bundle;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.msgctrl.RxBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class MyAccountActivity extends BaseActivity {

    @Bind(R.id.customer_reward_amount) TextView mCustomerAmount;
    @Bind(R.id.coupon_commission_amount) TextView mCouponAmount;
    @Bind(R.id.clock_commission_amount) TextView mClockAmount;

    private Subscription mInitSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        ButterKnife.bind(this);

        setTitle(R.string.personal_fragment_layout_account);
        setBackVisible(true);

        mInitSubscription = RxBus.getInstance().toObservable(BaseResult.class).subscribe(
                result -> initView());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mInitSubscription);
    }

    private void initView(){

    }

    @OnClick(R.id.customer_consume)
    public void customerRewardConsume(){
        makeShortToast("ceshi");
    }

    @OnClick(R.id.coupon_consume)
    public void couponCommissionConsume(){
        makeShortToast("ceshi");
    }

    @OnClick(R.id.clock_consume)
    public void clockCommissionConsume(){
        makeShortToast("ceshi");
    }

}
