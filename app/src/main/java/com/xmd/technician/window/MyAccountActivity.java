package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.gson.AccountMoneyResult;
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
    private static final int TYPE_TIME_CARD = 4;
    private IWXAPI mWXapi;

    @Bind(R.id.customer_reward_amount)
    TextView mCustomerAmount;
    @Bind(R.id.coupon_commission_amount)
    TextView mCouponAmount;
    @Bind(R.id.clock_commission_amount)
    TextView mClockAmount;
    @Bind(R.id.order_commission_amount)
    TextView mPaidOrderAmount;
    @Bind(R.id.time_card_amount)
    TextView mTimeCardAmount;
    @Bind(R.id.clock_consume)
    Button mClockConsumeBtn;
    @Bind(R.id.coupon_consume)
    Button mCouponConsumeBtn;

    private Subscription mInitSubscription;
    private Boolean isCanDrawMoney;

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

    private void initView(AccountMoneyResult result) {
        if (result.respData != null) {
            mCustomerAmount.setText(String.format("%1.2f", result.respData.rewardMoney));
            mCouponAmount.setText(String.format("%1.2f", result.respData.redPack));
            mClockAmount.setText(String.format("%1.2f", result.respData.paidMoney));
            mPaidOrderAmount.setText(String.format("%1.2f", result.respData.orderMoney));
//            mTimeCardAmount.setText(String.format("%1.2f", result.respData.onceCardMoney));
            mCouponConsumeBtn.setEnabled(true);
            if (result.respData.withdrawal.equals("Y")) {
                isCanDrawMoney = true;
            } else {
                isCanDrawMoney = false;
                mCouponConsumeBtn.setBackground(ResourceUtils.getDrawable(R.drawable.account_button));
                mCouponConsumeBtn.setTextColor(ResourceUtils.getColor(R.color.colorBody));
            }
        }
    }

    @OnClick(R.id.customer_consume)
    public void customerRewardConsume() {
        showConfirmDialog();
    }

    @OnClick(R.id.coupon_consume)
    public void couponCommissionConsume() {
        if (isCanDrawMoney) {
            showConfirmDialog();
        } else {
            showFailureDialog();
        }
    }

    @OnClick(R.id.clock_consume)
    public void clockCommissionConsume() {
        showConfirmDialog();
    }

    @OnClick(R.id.order_consume)
    public void orderCommissionConsume() {
        showConfirmDialog();
    }

    @OnClick(R.id.time_card_consume)
    public void timeCardConsume() {
        showConfirmDialog();
    }

    private void showConfirmDialog() {
        Intent intent = new Intent(MyAccountActivity.this, IntroduceAccountActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.customer_reward_layout)
    public void showUserRewardDetail() {
        gotoDetailActivity(TYPE_USER_REWARD);
    }

    @OnClick(R.id.coupon_commission_layout)
    public void showCouponRewardDetail() {
        gotoDetailActivity(TYPE_COUPON_REWARD);
    }

    @OnClick(R.id.clock_commission_layout)
    public void showClockRewardDetail() {
        gotoDetailActivity(TYPE_PAID_COUPON);
    }

    @OnClick(R.id.order_commission_layout)
    public void showPaidOrderRewardDetail() {
        gotoDetailActivity(TYPE_PAID_ORDER);
    }

    @OnClick(R.id.time_card_layout)
    public void showTimeCardDetail() {
        gotoDetailActivity(TYPE_TIME_CARD);
    }

    private void gotoDetailActivity(int type) {
        Intent intent = new Intent(MyAccountActivity.this, ConsumeDetailActivity.class);
        intent.putExtra(ConsumeDetailActivity.EXTRA_CONSUME_TYPE, type);
        startActivity(intent);
    }

    private void showFailureDialog() {
        new ConfirmDialog(this, getString(R.string.draw_money_failure)) {
            @Override
            public void onConfirmClick() {

            }
        }.show();
    }
}
