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

    private TextView mCustomerName;
    private TextView mArriveTime;
    private TextView mCreateTime;
    private TextView mTechName;
    private TextView mOrderPay;
    private TextView mOrderStatus;

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
        showToolbar(R.id.toolbar, "预约订单");
        mCustomerName = (TextView) findViewById(R.id.tv_customer_name);
        mArriveTime = (TextView) findViewById(R.id.tv_order_arrive_time);
        mCreateTime = (TextView) findViewById(R.id.tv_order_create_time);
        mTechName = (TextView) findViewById(R.id.tv_order_tech_name);
        mOrderPay = (TextView) findViewById(R.id.tv_order_pay_money);
        mOrderStatus = (TextView) findViewById(R.id.tv_order_status);
        mVerifyBtn = (Button) findViewById(R.id.btn_order_verify);
        mExpireBtn = (Button) findViewById(R.id.btn_order_expire);
        mCancelBtn = (Button) findViewById(R.id.btn_order_cancel);

        if (TextUtils.isEmpty(mOrder.phoneNum)) {
            mCustomerName.setText(mOrder.customerName);
        } else {
            mCustomerName.setText(String.format("%s(%s)", mOrder.customerName, mOrder.phoneNum));
        }
        mArriveTime.setText(mOrder.appointTime);
        mCreateTime.setText(mOrder.createdAt);
        if (!TextUtils.isEmpty(mOrder.techName)) {
            mTechName.setText(mOrder.techName);
        }
        mOrderPay.setText(Utils.moneyToStringEx(mOrder.downPayment) + "元");
        mOrderStatus.setText(mOrder.statusName);

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
