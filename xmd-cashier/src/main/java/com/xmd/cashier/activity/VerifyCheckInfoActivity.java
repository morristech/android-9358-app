package com.xmd.cashier.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.CheckInfoAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyCheckInfoContract;
import com.xmd.cashier.dal.bean.CheckInfo;
import com.xmd.cashier.presenter.VerifyCheckInfoPresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.List;

/**
 * Created by zr on 17-5-19.
 */

public class VerifyCheckInfoActivity extends BaseActivity implements VerifyCheckInfoContract.View {
    private VerifyCheckInfoContract.Presenter mPresenter;
    private RecyclerView mCheckInfoListView;
    private CheckInfoAdapter mAdapter;
    private RelativeLayout mOperateLayout;
    private TextView mOperateDesc;
    private Button mOperateBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_check_info);
        mPresenter = new VerifyCheckInfoPresenter(this, this);
        String phone = getIntent().getStringExtra(AppConstants.EXTRA_PHONE_VERIFY);
        mPresenter.setPhone(phone);
        showToolbar(R.id.toolbar, phone);

        mCheckInfoListView = (RecyclerView) findViewById(R.id.list_check_info);
        mOperateLayout = (RelativeLayout) findViewById(R.id.layout_verify_op);
        mOperateDesc = (TextView) findViewById(R.id.tv_op_desc);
        mOperateBtn = (Button) findViewById(R.id.btn_op_verify);
        mOperateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onVerify();
            }
        });

        mCheckInfoListView.setLayoutManager(new LinearLayoutManager(this));
        mCheckInfoListView.setItemAnimator(new DefaultItemAnimator());
        mCheckInfoListView.addItemDecoration(new CustomRecycleViewDecoration(32));
        mAdapter = new CheckInfoAdapter(this);
        mAdapter.setCallBack(new CheckInfoAdapter.OnCheckItemCallBack() {
            @Override
            public void onInfoClick(CheckInfo info) {
                mPresenter.onItemClick(info);
            }

            @Override
            public void onInfoSelect(CheckInfo info, boolean selected) {
                mPresenter.onItemSelect(info, selected);
            }

            @Override
            public void onInfoSelectValid(CheckInfo info) {
                mPresenter.onItemSelectValid(info);
            }
        });
        mCheckInfoListView.setAdapter(mAdapter);
        // 获取数据
        mPresenter.onLoad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCheckInfo();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(VerifyCheckInfoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showCheckInfo(List<CheckInfo> list) {
        mAdapter.setData(list);
    }

    @Override
    public void clearCheckInfo() {
        mCheckInfoListView.removeAllViews();
        mAdapter.clearData();
        hideBottomLayout();
    }

    @Override
    public void showBottomLayout() {
        mOperateLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBottomLayout() {
        mOperateLayout.setVisibility(View.GONE);
    }

    @Override
    public void updateBottomLayout(int selected) {
        String temp = "已选择" + selected + "张";
        mOperateDesc.setText(Utils.changeColor(temp, getResources().getColor(R.color.colorPink), 3, temp.length() - 1));
        mOperateBtn.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(data, requestCode, resultCode);
    }
}
