package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyOrderContract;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.presenter.VerifyOrderPresenter;

/**
 * Created by zr on 2017/4/16 0016.
 * 核销预约订单
 */

public class VerifyOrderActivity extends BaseActivity implements VerifyOrderContract.View {
    private VerifyOrderContract.Presenter mPresenter;

    private OrderInfo mOrder;

    private TextView mOrderNo;
    private TextView mCustomerName;
    private TextView mArriveTime;
    private TextView mCreateTime;
    private TextView mTechName;
    private TextView mOrderServiceItem;
    private TextView mOrderPay;
    private TextView mOrderStatus;
    private TextView mOrderDescription;

    private Button mVerifyBtn;
    private Button mExpireBtn;
    private Button mCancelBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_order);
        mPresenter = new VerifyOrderPresenter(this, this);
        mOrder = getIntent().getParcelableExtra(AppConstants.EXTRA_ORDER_VERIFY_INFO);
        if (mOrder == null) {
            showToast("无效核销信息");
            finishSelf();
            return;
        }
        initView();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "付费预约");

        mOrderNo = (TextView) findViewById(R.id.tv_order_no);
        mCustomerName = (TextView) findViewById(R.id.tv_customer_name);
        mArriveTime = (TextView) findViewById(R.id.tv_order_arrive_time);
        mCreateTime = (TextView) findViewById(R.id.tv_order_create_time);
        mTechName = (TextView) findViewById(R.id.tv_order_tech_name);
        mOrderPay = (TextView) findViewById(R.id.tv_order_pay_money);
        mOrderStatus = (TextView) findViewById(R.id.tv_order_status);
        mOrderServiceItem = (TextView) findViewById(R.id.tv_order_service_item);
        mVerifyBtn = (Button) findViewById(R.id.btn_order_verify);
        mExpireBtn = (Button) findViewById(R.id.btn_order_expire);
        mCancelBtn = (Button) findViewById(R.id.btn_order_cancel);
        mOrderDescription = (TextView) findViewById(R.id.tv_order_description);

        if (TextUtils.isEmpty(mOrder.orderNo)) {
            mOrderNo.setVisibility(View.GONE);
        } else {
            mOrderNo.setVisibility(View.VISIBLE);
            mOrderNo.setText(mOrder.orderNo);
        }

        if (TextUtils.isEmpty(mOrder.phoneNum)) {
            mCustomerName.setText(mOrder.customerName);
        } else {
            mCustomerName.setText(String.format("%s(%s)", mOrder.customerName, mOrder.phoneNum));
        }
        mArriveTime.setText(mOrder.appointTime);
        mCreateTime.setText(mOrder.createdAt);
        if (!TextUtils.isEmpty(mOrder.techName)) {
            if (TextUtils.isEmpty(mOrder.techNo)) {
                mTechName.setText(mOrder.techName);
            } else {
                mTechName.setText(String.format("%s[%s]", mOrder.techName, mOrder.techNo));
            }
        }
        mOrderPay.setText(Utils.moneyToStringEx(mOrder.downPayment) + "元");
        mOrderStatus.setText(mOrder.statusName);

        if (TextUtils.isEmpty(mOrder.description)) {
            mOrderDescription.setText("无");
        } else {
            mOrderDescription.setText(mOrder.description);
        }

        if (TextUtils.isEmpty(mOrder.serviceItemName)) {
            mOrderServiceItem.setText("到店选择");
        } else {
            mOrderServiceItem.setText(mOrder.serviceItemName);
        }

        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onVerify(mOrder);
            }
        });

        mExpireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onExpire(mOrder);
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCancel();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(VerifyOrderContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }
}