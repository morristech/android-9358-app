package com.xmd.cashier.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerPaymentContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.presenter.InnerPaymentPresenter;
import com.xmd.cashier.widget.CircleImageView;

/**
 * Created by zr on 17-11-4.
 * 处理扫码支付,现金支付,以及其他标记支付
 */

public class InnerPaymentActivity extends BaseActivity implements InnerPaymentContract.View {
    private InnerPaymentContract.Presenter mPresenter;

    private TextView mOriginAmount;
    private TextView mDiscountAmount;

    private ViewStub mMarkViewStub;//标记
    private TextView mMarkPaidAmount;
    private Button mMarkConfirmBtn;
    private TextView mMarkName;
    private TextView mMarkDesc;

    private ViewStub mScanViewStub;//扫码
    private TextView mScanPaidAmount;
    private TextView mScanActivity;
    private ImageView mScanQrcodeImage;

    private ViewStub mMemberViewStub;//会员
    private CircleImageView mMemberAvatar;
    private TextView mMemberName;
    private TextView mMemberLevel;
    private TextView mMemberPhone;
    private TextView mMemberCardNo;
    private TextView mMemberAccountAmount;
    private TextView mMemberOriginAmount;
    private TextView mMemberDiscount;
    private TextView mMemberDiscountAmount;
    private TextView mMemberPaidAmount;
    private TextView mMemberPayDesc;
    private Button mMemberConfirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_payment);
        mPresenter = new InnerPaymentPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "支付");
        mMarkViewStub = (ViewStub) findViewById(R.id.stub_mark_layout);
        mScanViewStub = (ViewStub) findViewById(R.id.stub_scan_layout);
        mMemberViewStub = (ViewStub) findViewById(R.id.stub_member_layout);
        mOriginAmount = (TextView) findViewById(R.id.tv_inner_origin_amount);
        mDiscountAmount = (TextView) findViewById(R.id.tv_inner_discount_amount);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(InnerPaymentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void setOrigin(String origin) {
        mOriginAmount.setText(origin);
    }

    @Override
    public void setDiscount(String discount) {
        mDiscountAmount.setText(discount);
    }

    @Override
    public void initMarkStub() {
        mScanViewStub.setVisibility(View.GONE);
        mMemberViewStub.setVisibility(View.GONE);
        mMarkViewStub.setVisibility(View.VISIBLE);
        mMarkPaidAmount = (TextView) findViewById(R.id.tv_mark_paid_money);
        mMarkConfirmBtn = (Button) findViewById(R.id.btn_inner_mark_confirm);
        mMarkName = (TextView) findViewById(R.id.tv_inner_mark_name);
        mMarkDesc = (TextView) findViewById(R.id.tv_inner_mark_desc);
        mMarkConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onMarkConfirm();
            }
        });
    }

    @Override
    public void setMarkPaid(String markPaid) {
        mMarkPaidAmount.setText(markPaid);
    }

    @Override
    public void setMarkName(String name) {
        mMarkName.setText(name);
    }

    @Override
    public void setMarkDesc(String desc) {
        if (TextUtils.isEmpty(desc)) {
            mMarkDesc.setVisibility(View.GONE);
        } else {
            mMarkDesc.setVisibility(View.VISIBLE);
            mMarkDesc.setText(desc);
        }
    }

    @Override
    public void initScanStub() {
        mScanViewStub.setVisibility(View.VISIBLE);
        mMemberViewStub.setVisibility(View.GONE);
        mMarkViewStub.setVisibility(View.GONE);
        mScanPaidAmount = (TextView) findViewById(R.id.tv_scan_paid_money);
        mScanActivity = (TextView) findViewById(R.id.tv_pay_activity);
        mScanQrcodeImage = (ImageView) findViewById(R.id.img_scan_qrcode);
        mScanActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPayActivityShow();
            }
        });
    }

    @Override
    public void setScanPaid(String scanPaid) {
        mScanPaidAmount.setText(scanPaid);
    }

    @Override
    public void setQrcode(Bitmap bitmap) {
        mScanQrcodeImage.setImageBitmap(bitmap);
    }

    @Override
    public void setPayActivity(int show) {
        mScanActivity.setVisibility(show);
    }

    @Override
    public void initMemberStub() {
        mScanViewStub.setVisibility(View.GONE);
        mMemberViewStub.setVisibility(View.VISIBLE);
        mMarkViewStub.setVisibility(View.GONE);
        mMemberAvatar = (CircleImageView) findViewById(R.id.img_member_avatar);
        mMemberName = (TextView) findViewById(R.id.tv_member_name);
        mMemberLevel = (TextView) findViewById(R.id.tv_member_level);
        mMemberPhone = (TextView) findViewById(R.id.tv_member_phone);
        mMemberCardNo = (TextView) findViewById(R.id.tv_member_card_no);
        mMemberAccountAmount = (TextView) findViewById(R.id.tv_member_account_amount);
        mMemberOriginAmount = (TextView) findViewById(R.id.tv_origin_amount);
        mMemberDiscount = (TextView) findViewById(R.id.tv_member_discount);
        mMemberDiscountAmount = (TextView) findViewById(R.id.tv_member_discount_amount);
        mMemberPaidAmount = (TextView) findViewById(R.id.tv_member_need_amount);
        mMemberPayDesc = (TextView) findViewById(R.id.tv_member_pay_desc);
        mMemberConfirmBtn = (Button) findViewById(R.id.btn_member_pay);
        mMemberConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onMemberConfirm();
            }
        });
    }

    @Override
    public void setMemberInfo(MemberInfo info) {
        Glide.with(this).load(info.avatarUrl).dontAnimate().placeholder(R.drawable.ic_avatar).into(mMemberAvatar);
        mMemberName.setText(info.name);
        mMemberLevel.setText(info.memberTypeName);
        mMemberPhone.setText(info.phoneNum);
        mMemberCardNo.setText(info.cardNo);
        mMemberAccountAmount.setText(Utils.moneyToStringEx(info.amount));
        mMemberDiscount.setText(String.format("%.02f", info.discount / 100.0f));
    }

    @Override
    public void setMemberOrigin(String memberOrigin) {
        mMemberOriginAmount.setText(memberOrigin);
    }

    @Override
    public void setMemberDiscount(String memberDiscount) {
        mMemberDiscountAmount.setText(memberDiscount);
    }

    @Override
    public void setMemberPaid(String memberPaid) {
        mMemberPaidAmount.setText(memberPaid);
    }

    @Override
    public void setConfirmEnable(boolean enable) {
        mMemberConfirmBtn.setEnabled(enable);
        mMemberPayDesc.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onEventBack();
        return true;
    }
}
