package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.InnerTechedOrderAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerTechedOrderContract;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.presenter.InnerTechedOrderPresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.List;

/**
 * Created by zr on 17-11-7.
 * 技师订单列表
 */

public class InnerTechedOrderActivity extends BaseActivity implements InnerTechedOrderContract.View {
    private InnerTechedOrderContract.Presenter mPresenter;

    private TextView mDescText;
    private RecyclerView mOrderList;
    private TextView mNegativeText;
    private TextView mPositiveText;

    private InnerTechedOrderAdapter mTechAdapter;

    private String mEmpId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_teched_order);
        mEmpId = getIntent().getStringExtra(AppConstants.EXTRA_INNER_EMP_ID);
        mPresenter = new InnerTechedOrderPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        mOrderList = (RecyclerView) findViewById(R.id.rv_order_detail);
        mNegativeText = (TextView) findViewById(R.id.tv_tech_negative);
        mPositiveText = (TextView) findViewById(R.id.tv_tech_positive);
        mDescText = (TextView) findViewById(R.id.tv_order_result_desc);

        mTechAdapter = new InnerTechedOrderAdapter(InnerTechedOrderActivity.this);
        mTechAdapter.setCallBack(new InnerTechedOrderAdapter.CallBack() {
            @Override
            public void onItemClick(InnerOrderInfo info, int position) {
                mPresenter.onItemSelect(info, position);
            }
        });

        mOrderList.setLayoutManager(new LinearLayoutManager(InnerTechedOrderActivity.this));
        mOrderList.addItemDecoration(new CustomRecycleViewDecoration(3));
        mOrderList.setAdapter(mTechAdapter);

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
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(InnerTechedOrderContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showOrderData(List<InnerOrderInfo> list) {
        mTechAdapter.setData(list);
    }

    @Override
    public void updateItem(int position) {
        mTechAdapter.notifyItemChanged(position);
    }

    @Override
    public String returnEmpId() {
        return mEmpId;
    }

    @Override
    public void showDesc(String desc) {
        mDescText.setVisibility(View.VISIBLE);
        mDescText.setText(desc);
    }

    @Override
    public void hideDesc() {
        mDescText.setVisibility(View.GONE);
    }
}
