package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.InnerOrderAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerDetailContract;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.bean.OrderDiscountInfo;
import com.xmd.cashier.presenter.InnerDetailPresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.List;

/**
 * Created by zr on 17-11-7.
 */

public class InnerDetailActivity extends BaseActivity implements InnerDetailContract.View {
    private RecyclerView mOrderDetailList;
    private TextView mOrderDetailNegative;
    private TextView mOrderDetailPositive;

    private InnerOrderAdapter mOrderAdapter;

    private InnerDetailContract.Presenter mPresenter;

    private InnerRecordInfo mRecordInfo;

    private TextView mOriginAmount;
    private LinearLayout mPaidOrderLayout;
    private TextView mPaidOrderAmount;
    private LinearLayout mPaidCouponLayout;
    private TextView mCouponAmount;
    private LinearLayout mPaidMemberLayout;
    private TextView mMemberAmount;
    private TextView mNeedAmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_detail);
        mRecordInfo = (InnerRecordInfo) getIntent().getSerializableExtra(AppConstants.EXTRA_INNER_RECORD_DETAIL);
        mPresenter = new InnerDetailPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        mOrderDetailList = (RecyclerView) findViewById(R.id.rv_order_detail);
        mOrderDetailNegative = (TextView) findViewById(R.id.tv_order_negative);
        mOrderDetailPositive = (TextView) findViewById(R.id.tv_order_positive);

        mOriginAmount = (TextView) findViewById(R.id.tv_origin_amount);
        mPaidOrderLayout = (LinearLayout) findViewById(R.id.layout_paid_order);
        mPaidOrderAmount = (TextView) findViewById(R.id.tv_paid_order_amount);
        mPaidCouponLayout = (LinearLayout) findViewById(R.id.layout_paid_coupon);
        mCouponAmount = (TextView) findViewById(R.id.tv_coupon_amount);
        mPaidMemberLayout = (LinearLayout) findViewById(R.id.layout_paid_member);
        mMemberAmount = (TextView) findViewById(R.id.tv_member_amount);
        mNeedAmount = (TextView) findViewById(R.id.tv_need_amount);

        mOrderDetailNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onDetailNegative();
            }
        });

        mOrderDetailPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onDetailPositive();
            }
        });

        mOrderAdapter = new InnerOrderAdapter(this, false);
        mOrderDetailList.setLayoutManager(new LinearLayoutManager(this));
        mOrderDetailList.addItemDecoration(new CustomRecycleViewDecoration(8));
        mOrderDetailList.setAdapter(mOrderAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setPresenter(InnerDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public InnerRecordInfo returnRecordInfo() {
        return mRecordInfo;
    }

    @Override
    public void showRecordDetail(List<InnerOrderInfo> list) {
        mOrderAdapter.setData(list);
    }

    @Override
    public void showAmount(InnerRecordInfo recordInfo) {
        int paidOrderAmount = 0;
        int paidCouponAmount = 0;
        int paidMemberAmount = 0;
        for (OrderDiscountInfo discountInfo : recordInfo.orderDiscountList) {
            switch (discountInfo.type) {
                case AppConstants.PAY_DISCOUNT_COUPON:
                    paidCouponAmount += discountInfo.amount;
                    break;
                case AppConstants.PAY_DISCOUNT_MEMBER:
                    paidMemberAmount += discountInfo.amount;
                    break;
                case AppConstants.PAY_DISCOUNT_ORDER:
                    paidOrderAmount += discountInfo.amount;
                    break;
                default:
                    break;
            }
        }
        mOriginAmount.setText("￥" + Utils.moneyToStringEx(recordInfo.originalAmount));
        if (paidOrderAmount > 0) {
            mPaidOrderLayout.setVisibility(View.VISIBLE);
            mPaidOrderAmount.setText("￥" + Utils.moneyToStringEx(paidOrderAmount));
        } else {
            mPaidOrderLayout.setVisibility(View.GONE);
        }
        if (paidCouponAmount > 0) {
            mPaidCouponLayout.setVisibility(View.VISIBLE);
            mCouponAmount.setText("￥" + Utils.moneyToStringEx(paidCouponAmount));
        } else {
            mPaidCouponLayout.setVisibility(View.GONE);
        }
        if (paidMemberAmount > 0) {
            mPaidMemberLayout.setVisibility(View.VISIBLE);
            mMemberAmount.setText("￥" + Utils.moneyToStringEx(paidMemberAmount));
        } else {
            mPaidMemberLayout.setVisibility(View.GONE);
        }
        mNeedAmount.setText("￥" + Utils.moneyToStringEx(recordInfo.payAmount));
    }
}
