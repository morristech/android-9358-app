package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberCashierContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.presenter.MemberCashierPresenter;
import com.xmd.cashier.widget.CircleImageView;

/**
 * Created by zr on 17-7-22.
 */

public class MemberCashierActivity extends BaseActivity implements MemberCashierContract.View {
    public MemberCashierContract.Presenter mPresenter;

    private CircleImageView mMemberAvatar;
    private TextView mMemberName;
    private TextView mMemberLevel;
    private TextView mMemberPhone;
    private TextView mMemberCardNo;
    private TextView mMemberAccountAmount;

    private TextView mOriginAmount;
    private TextView mDiscountRate;
    private TextView mDiscountAmount;
    private TextView mNeedAmount;

    private Button mMemberPayBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_cashier);
        mPresenter = new MemberCashierPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "会员消费");
        mMemberAvatar = (CircleImageView) findViewById(R.id.img_member_avatar);
        mMemberName = (TextView) findViewById(R.id.tv_member_name);
        mMemberLevel = (TextView) findViewById(R.id.tv_member_level);
        mMemberPhone = (TextView) findViewById(R.id.tv_member_phone);
        mMemberCardNo = (TextView) findViewById(R.id.tv_member_card_no);
        mMemberAccountAmount = (TextView) findViewById(R.id.tv_member_account_amount);

        mOriginAmount = (TextView) findViewById(R.id.tv_origin_amount);
        mDiscountRate = (TextView) findViewById(R.id.tv_member_discount);
        mDiscountAmount = (TextView) findViewById(R.id.tv_member_discount_amount);
        mNeedAmount = (TextView) findViewById(R.id.tv_member_need_amount);

        mMemberPayBtn = (Button) findViewById(R.id.btn_member_pay);

        mMemberPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onMemberPay();
            }
        });
    }

    @Override
    public void showInfo(MemberInfo info) {
        Glide.with(this).load(info.avatarUrl).dontAnimate().placeholder(R.drawable.ic_avatar).into(mMemberAvatar);
        mMemberName.setText(info.name);
        mMemberLevel.setText(info.memberTypeName);
        mMemberPhone.setText(info.phoneNum);
        mMemberCardNo.setText(info.cardNo);
        mMemberAccountAmount.setText(Utils.moneyToStringEx(info.amount));
        mDiscountRate.setText(String.format("%.02f", info.discount / 100.0f));
    }

    @Override
    public void showButton(boolean enable) {
        mMemberPayBtn.setEnabled(enable);
    }

    @Override
    public void showOriginAmount(String origin) {
        mOriginAmount.setText(origin);
    }

    @Override
    public void showDiscountAmount(String discount) {
        mDiscountAmount.setText("- " + discount);
    }

    @Override
    public void showNeedAmount(String need) {
        mNeedAmount.setText(need);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onNavigationBack();
        return super.onKeyEventBack();
    }

    @Override
    public void setPresenter(MemberCashierContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }
}
