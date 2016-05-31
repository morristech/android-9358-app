package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.http.gson.AccountMoneyResult;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ConfirmDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class MyAccountActivity extends BaseActivity {

    private static final int TYPE_USER_REWARD = 0;
    private static final int TYPE_COUPON_REWARD = 1;
    private static final int TYPE_PAID_COUPON = 2;
    private static final int TYPE_PAID_ORDER = 3;

    @Bind(R.id.customer_reward_amount) TextView mCustomerAmount;
    @Bind(R.id.coupon_commission_amount) TextView mCouponAmount;
    @Bind(R.id.clock_commission_amount) TextView mClockAmount;
    @Bind(R.id.order_commission_amount) TextView mPaidOrderAmount;
    @Bind(R.id.clock_consume) Button mClockConsumeBtn;
    @Bind(R.id.coupon_consume) Button mCouponConsumeBtn;

    private Subscription mInitSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        ButterKnife.bind(this);

        setTitle(R.string.personal_fragment_layout_account);
        setBackVisible(true);

        mInitSubscription = RxBus.getInstance().toObservable(AccountMoneyResult.class).subscribe(
                result -> initView(result));

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACCOUNT_MONEY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mInitSubscription);
    }

    private void initView(AccountMoneyResult result){
        if(result.respData != null){
            mCustomerAmount.setText(String.valueOf(result.respData.rewardMoney));
            mCouponAmount.setText(String.valueOf(result.respData.redPack));
            mClockAmount.setText(String.valueOf(result.respData.paidMoney));
            mPaidOrderAmount.setText(String.valueOf(result.respData.orderMoney));

            if(result.respData.withdrawal.equals("Y")){
                //mClockConsumeBtn.setEnabled(true);
                mCouponConsumeBtn.setEnabled(true);
            }else {
                //mClockConsumeBtn.setEnabled(false);
                mCouponConsumeBtn.setEnabled(false);
            }
        }
    }

    @OnClick(R.id.customer_consume)
    public void customerRewardConsume(){
        showConfirmDialog();
    }

    @OnClick(R.id.coupon_consume)
    public void couponCommissionConsume(){
        showConfirmDialog();
    }

    @OnClick(R.id.clock_consume)
    public void clockCommissionConsume(){
        showConfirmDialog();
    }

    @OnClick(R.id.order_consume)
    public void orderCommissionConsume(){
        showConfirmDialog();
    }

    private void showConfirmDialog(){
        new ConfirmDialog(this, getString(R.string.consume_tip)) {
            @Override
            public void onConfirmClick() {

            }
        }.show();
    }

    @OnClick(R.id.customer_reward_layout)
    public void showUserRewardDetail(){
        gotoDetailActivity(TYPE_USER_REWARD);
    }

    @OnClick(R.id.coupon_commission_layout)
    public void showCouponRewardDetail(){
        gotoDetailActivity(TYPE_COUPON_REWARD);
    }

    @OnClick(R.id.clock_commission_layout)
    public void showClockRewardDetail(){
        gotoDetailActivity(TYPE_PAID_COUPON);
    }

    @OnClick(R.id.order_commission_layout)
    public void showPaidOrderRewardDetail(){
        gotoDetailActivity(TYPE_PAID_ORDER);
    }

    private void gotoDetailActivity(int type){
        Intent intent = new Intent(MyAccountActivity.this, ConsumeDetailActivity.class);
        intent.putExtra(ConsumeDetailActivity.EXTRA_CONSUME_TYPE, type);
        startActivity(intent);
    }
}
