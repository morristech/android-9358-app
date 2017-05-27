package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyOrderContract;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.presenter.VerifyOrderPresenter;

import org.xml.sax.XMLReader;

/**
 * Created by zr on 2017/4/16 0016.
 * 核销预约订单
 */

public class VerifyOrderActivity extends BaseActivity implements VerifyOrderContract.View {
    private VerifyOrderContract.Presenter mPresenter;

    private OrderInfo mOrder;
    private boolean isShow;

    private TextView mOrderNo;
    private TextView mOrderType;
    private TextView mOrderCustomer;
    private TextView mOrderStatus;
    private TextView mOrderPayment;
    private TextView mOrderArriveTime;
    private TextView mOrderTech;
    private LinearLayout mLayoutServiceItem;
    private TextView mOrderServiceItem;
    private LinearLayout mLayoutCreateTime;
    private TextView mOrderCreateTime;
    private TextView mOrderDescription;

    private Button mVerifyBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_order);
        mPresenter = new VerifyOrderPresenter(this, this);
        mOrder = getIntent().getParcelableExtra(AppConstants.EXTRA_ORDER_VERIFY_INFO);
        isShow = getIntent().getBooleanExtra(AppConstants.EXTRA_IS_SHOW, true);
        if (mOrder == null) {
            showToast("无效核销信息");
            finishSelf();
            return;
        }
        initView();
    }

    private void initView() {
        showToolbar(R.id.toolbar, mOrder.customerName); //标题为客户名称

        mOrderNo = (TextView) findViewById(R.id.tv_order_no);
        mOrderType = (TextView) findViewById(R.id.tv_order_type);
        mOrderCustomer = (TextView) findViewById(R.id.tv_order_customer_name);
        mOrderStatus = (TextView) findViewById(R.id.tv_order_status);
        mOrderPayment = (TextView) findViewById(R.id.tv_order_down_pay);
        mOrderArriveTime = (TextView) findViewById(R.id.tv_order_arrive_time);
        mOrderTech = (TextView) findViewById(R.id.tv_order_tech_name);
        mLayoutServiceItem = (LinearLayout) findViewById(R.id.ly_order_service_item);
        mOrderServiceItem = (TextView) findViewById(R.id.tv_order_service_item);
        mLayoutCreateTime = (LinearLayout) findViewById(R.id.ly_order_create_time);
        mOrderCreateTime = (TextView) findViewById(R.id.tv_order_create_time);
        mVerifyBtn = (Button) findViewById(R.id.btn_order_verify);
        mOrderDescription = (TextView) findViewById(R.id.tv_order_description);

        if (TextUtils.isEmpty(mOrder.orderNo)) {
            mOrderNo.setVisibility(View.GONE);
        } else {
            mOrderNo.setVisibility(View.VISIBLE);
            mOrderNo.setText(mOrder.orderNo);
        }

        mOrderType.setText("付费预约");
        mOrderCustomer.setText(mOrder.customerName);
        mOrderPayment.setText("预约定金" + Utils.moneyToStringEx(mOrder.downPayment) + "元");
        mOrderArriveTime.setText(mOrder.appointTime);
        if (!TextUtils.isEmpty(mOrder.techName)) {
            if (TextUtils.isEmpty(mOrder.techNo)) {
                mOrderTech.setText(mOrder.techName);
            } else {
                mOrderTech.setText(String.format("%s[%s]", mOrder.techName, mOrder.techNo));
            }
        }
        mOrderStatus.setText(mOrder.statusName);

        mLayoutCreateTime.setVisibility(View.VISIBLE);
        mOrderCreateTime.setText(mOrder.createdAt);

        mLayoutServiceItem.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mOrder.serviceItemName)) {
            mOrderServiceItem.setText("到店选择");
        } else {
            mOrderServiceItem.setText(mOrder.serviceItemName);
        }

        if (TextUtils.isEmpty(mOrder.description)) {
            mOrderDescription.setText("无");
        } else {
            mOrderDescription.setText(Html.fromHtml(mOrder.description, null, new Html.TagHandler() {
                @Override
                public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                    if (tag.equals("ul") && !opening) {
                        output.append("\n");
                    }
                    if (tag.equals("li") && opening) {
                        output.append("\n\t•  ");
                    }
                }
            }));
        }

        mVerifyBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onVerify(mOrder);
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
