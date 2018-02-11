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
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.presenter.InnerDetailPresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.List;

/**
 * Created by zr on 17-11-7.
 */

public class InnerDetailActivity extends BaseActivity implements InnerDetailContract.View {
    private RecyclerView mOrderDetailList;
    private LinearLayout mOrderOperateLayout;
    private TextView mOrderDetailNegative;
    private TextView mOrderDetailPositive;
    private TextView mOrderDetailPrint;
    private TextView mOrderDetailPay;

    private InnerOrderAdapter mOrderAdapter;

    private InnerDetailContract.Presenter mPresenter;

    private TradeRecordInfo mRecordInfo;
    private String mSource;

    private TextView mOriginAmount;
    private LinearLayout mDiscountAmountLayout;
    private TextView mDiscountAmount;
    private LinearLayout mPaidAmountLayout;
    private TextView mPaidAmount;
    private TextView mNeedAmount;
    private TextView mNeedDesc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_detail);
        mSource = getIntent().getStringExtra(AppConstants.EXTRA_INNER_DETAIL_SOURCE);
        mRecordInfo = (TradeRecordInfo) getIntent().getSerializableExtra(AppConstants.EXTRA_INNER_RECORD_DETAIL);
        mPresenter = new InnerDetailPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        mOrderDetailList = (RecyclerView) findViewById(R.id.rv_order_detail);
        mOrderOperateLayout = (LinearLayout) findViewById(R.id.layout_order_operate);
        mOrderDetailNegative = (TextView) findViewById(R.id.tv_order_negative);
        mOrderDetailPositive = (TextView) findViewById(R.id.tv_order_positive);
        mOrderDetailPrint = (TextView) findViewById(R.id.tv_order_print);
        mOrderDetailPay = (TextView) findViewById(R.id.tv_order_to_pay);

        mOriginAmount = (TextView) findViewById(R.id.tv_origin_amount);
        mDiscountAmountLayout = (LinearLayout) findViewById(R.id.layout_discount_amount);
        mDiscountAmount = (TextView) findViewById(R.id.tv_discount_amount);
        mPaidAmountLayout = (LinearLayout) findViewById(R.id.layout_paid_amount);
        mPaidAmount = (TextView) findViewById(R.id.tv_paid_amount);
        mNeedAmount = (TextView) findViewById(R.id.tv_need_amount);
        mNeedDesc = (TextView) findViewById(R.id.tv_need_desc);

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

        mOrderDetailPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onDetailPay();
            }
        });

        mOrderDetailPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onDetailPrint();
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
    public TradeRecordInfo returnRecordInfo() {
        return mRecordInfo;
    }

    @Override
    public String returnSource() {
        return mSource;
    }

    @Override
    public void showRecordDetail(List<InnerOrderInfo> list) {
        mOrderAdapter.setData(list);
    }

    @Override
    public void showAmount(TradeRecordInfo recordInfo) {
        // 订单金额
        mOriginAmount.setText("￥" + Utils.moneyToStringEx(recordInfo.originalAmount));
        // 优惠金额
        int discountAmount = recordInfo.originalAmount - recordInfo.payAmount;
        if (discountAmount > 0) {
            mDiscountAmountLayout.setVisibility(View.VISIBLE);
            mDiscountAmount.setText("￥" + Utils.moneyToStringEx(discountAmount));
        } else {
            mDiscountAmountLayout.setVisibility(View.GONE);
        }

        if (AppConstants.INNER_BATCH_STATUS_UNPAID.equals(recordInfo.status)) { // 未支付
            // 已付金额
            if (recordInfo.paidAmount > 0) {
                mPaidAmountLayout.setVisibility(View.VISIBLE);
                mPaidAmount.setText("￥" + Utils.moneyToStringEx(recordInfo.paidAmount));
            } else {
                mPaidAmountLayout.setVisibility(View.GONE);
            }
            // 待付金额
            mNeedDesc.setText("待付金额：");
            mNeedAmount.setText("￥" + Utils.moneyToStringEx(recordInfo.payAmount - recordInfo.paidAmount));
        } else {    //已支付
            mPaidAmountLayout.setVisibility(View.GONE);
            mNeedDesc.setText("实收金额：");
            mNeedAmount.setText("￥" + Utils.moneyToStringEx(recordInfo.payAmount));
        }
    }

    @Override
    public void showAmount(int amount) {
        mDiscountAmountLayout.setVisibility(View.GONE);
        mPaidAmountLayout.setVisibility(View.GONE);
        mOriginAmount.setText("￥" + Utils.moneyToStringEx(amount));
        mNeedDesc.setText("待付金额：");
        mNeedAmount.setText("￥" + Utils.moneyToStringEx(amount));
    }

    @Override
    public void showOperate() {
        mOrderDetailPositive.setVisibility(View.GONE);
        mOrderOperateLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPositive() {
        mOrderDetailPositive.setVisibility(View.VISIBLE);
        mOrderOperateLayout.setVisibility(View.GONE);
    }

    @Override
    public void setOperate(boolean pay) {
        mOrderDetailPay.setVisibility(pay ? View.VISIBLE : View.GONE);
        mOrderDetailPrint.setVisibility(pay ? View.GONE : View.VISIBLE);
    }
}
