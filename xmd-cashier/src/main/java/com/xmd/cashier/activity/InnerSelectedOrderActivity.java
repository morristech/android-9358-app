package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.InnerSelectedOrderAdapter;
import com.xmd.cashier.contract.InnerSelectedOrderContract;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.presenter.InnerSelectedOrderPresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.List;

/**
 * Created by zr on 17-11-7.
 * 订单选中列表
 */

public class InnerSelectedOrderActivity extends BaseActivity implements InnerSelectedOrderContract.View {
    private TextView mCountText;
    private TextView mNegativeText;
    private TextView mPositiveText;
    private RecyclerView mSelectOrderList;
    private InnerSelectedOrderAdapter mOrderAdapter;

    private InnerSelectedOrderContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_selected_order);
        mPresenter = new InnerSelectedOrderPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        mCountText = (TextView) findViewById(R.id.tv_select_count);
        mSelectOrderList = (RecyclerView) findViewById(R.id.rv_order_detail);
        mNegativeText = (TextView) findViewById(R.id.tv_select_negative);
        mPositiveText = (TextView) findViewById(R.id.tv_select_positive);
        mOrderAdapter = new InnerSelectedOrderAdapter(InnerSelectedOrderActivity.this);
        mOrderAdapter.setCallBack(new InnerSelectedOrderAdapter.CallBack() {
            @Override
            public void onItemClick(InnerOrderInfo info, int position) {
                mPresenter.onItemSelect(info, position);
            }
        });
        mSelectOrderList.setLayoutManager(new LinearLayoutManager(InnerSelectedOrderActivity.this));
        mSelectOrderList.addItemDecoration(new CustomRecycleViewDecoration(3));
        mSelectOrderList.setAdapter(mOrderAdapter);
        mNegativeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onNegative();
            }
        });

        mPositiveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPositive();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void setPresenter(InnerSelectedOrderContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showCountText(String text) {
        mCountText.setVisibility(View.VISIBLE);
        mCountText.setText(text);
    }

    @Override
    public void hideCountText() {
        mCountText.setVisibility(View.GONE);
    }

    @Override
    public void updateItem(int position) {
        mOrderAdapter.notifyItemChanged(position);
    }

    @Override
    public void showOrderData(List<InnerOrderInfo> list) {
        mOrderAdapter.setData(list);
    }
}
