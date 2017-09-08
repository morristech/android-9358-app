package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.GiftActAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.GiftActContract;
import com.xmd.cashier.dal.bean.GiftActivityInfo;
import com.xmd.cashier.presenter.GiftActPresenter;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;
import com.xmd.cashier.widget.FullyGridLayoutManager;

import java.util.List;

/**
 * Created by zr on 17-9-8.
 */

public class GiftActActivity extends BaseActivity implements GiftActContract.View {
    private GiftActContract.Presenter mPresenter;
    private GiftActAdapter mAdapter;

    private RecyclerView mGiftActList;
    private TextView mGiftTime;
    private TextView mGiftCopyRight;
    private Button mConfirmBtn;

    private GiftActivityInfo mInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_act);
        mInfo = (GiftActivityInfo) getIntent().getSerializableExtra(AppConstants.EXTRA_GIFT_ACTIVITY_INFO);
        mPresenter = new GiftActPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        mGiftActList = (RecyclerView) findViewById(R.id.rv_act_list);
        mGiftTime = (TextView) findViewById(R.id.tv_act_time);
        mGiftCopyRight = (TextView) findViewById(R.id.tv_copy_right);
        mConfirmBtn = (Button) findViewById(R.id.btn_confirm);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSelf();
            }
        });

        mAdapter = new GiftActAdapter(this);
        mGiftActList.setHasFixedSize(true);
        mGiftActList.setNestedScrollingEnabled(false);
        mGiftActList.setLayoutManager(new FullyGridLayoutManager(this, 1));
        mGiftActList.setItemAnimator(new DefaultItemAnimator());
        mGiftActList.addItemDecoration(new CustomRecycleViewDecoration(15));
        mGiftActList.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(GiftActContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void setActivityData(List<GiftActivityInfo.GiftActivityPackage> data) {
        mAdapter.setData(data);
    }

    @Override
    public void setActivityTime(String time) {
        mGiftTime.setText(time);
    }

    @Override
    public void setActivityCopyRight(String copyright) {
        mGiftCopyRight.setText(copyright);
    }

    @Override
    public GiftActivityInfo getActivityInfo() {
        return mInfo;
    }
}
