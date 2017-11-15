package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.InnerOrderTechAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerOrderTechContract;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.presenter.InnerOrderTechPresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.List;

/**
 * Created by zr on 17-11-7.
 * 技师订单列表
 */

public class InnerOrderTechActivity extends BaseActivity implements InnerOrderTechContract.View {
    private InnerOrderTechContract.Presenter mPresenter;

    private TextView mDescText;
    private RecyclerView mOrderList;
    private TextView mNegativeText;
    private TextView mPositiveText;

    private InnerOrderTechAdapter mTechAdapter;

    private String mEmpId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_order_tech);
        mEmpId = getIntent().getStringExtra(AppConstants.EXTRA_INNER_EMP_ID);
        mPresenter = new InnerOrderTechPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        mOrderList = (RecyclerView) findViewById(R.id.rv_order_detail);
        mNegativeText = (TextView) findViewById(R.id.tv_tech_negative);
        mPositiveText = (TextView) findViewById(R.id.tv_tech_positive);
        mDescText = (TextView) findViewById(R.id.tv_order_result_desc);

        mTechAdapter = new InnerOrderTechAdapter(InnerOrderTechActivity.this);
        mTechAdapter.setCallBack(new InnerOrderTechAdapter.CallBack() {
            @Override
            public void onItemClick(InnerOrderInfo info, int position) {
                mPresenter.onItemSelect(info, position);
            }
        });

        mOrderList.setLayoutManager(new LinearLayoutManager(InnerOrderTechActivity.this));
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
    public void setPresenter(InnerOrderTechContract.Presenter presenter) {
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
