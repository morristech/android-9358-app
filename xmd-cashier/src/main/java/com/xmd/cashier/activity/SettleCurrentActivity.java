package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.adapter.SettleContentAdapter;
import com.xmd.cashier.contract.SettleCurrentContract;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.presenter.SettleCurrentPresenter;

/**
 * Created by zr on 17-3-29.
 * Pos结算:当前结算数据
 */

public class SettleCurrentActivity extends BaseActivity implements SettleCurrentContract.View {
    private SettleCurrentContract.Presenter mPresenter;

    private LinearLayout llResultError;
    private ImageView imgError;
    private TextView tvError;
    private TextView tvRefresh;

    private LinearLayout llResultContent;
    private TextView tvTimeRange;
    private RecyclerView rvContentList;

    private Button mSettleBtn;

    private SettleContentAdapter mContentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_current);
        mPresenter = new SettleCurrentPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "交接班结算");

        llResultError = (LinearLayout) findViewById(R.id.ll_result_error);
        imgError = (ImageView) findViewById(R.id.img_error);
        tvError = (TextView) findViewById(R.id.tv_error);
        tvRefresh = (TextView) findViewById(R.id.tv_click_error);

        llResultContent = (LinearLayout) findViewById(R.id.ll_result_content);
        tvTimeRange = (TextView) findViewById(R.id.tv_settle_time_range);
        rvContentList = (RecyclerView) findViewById(R.id.rv_content_list);
        mSettleBtn = (Button) findViewById(R.id.btn_settle_confirm);

        mContentAdapter = new SettleContentAdapter(this);
        rvContentList.setHasFixedSize(true);
        rvContentList.setLayoutManager(new LinearLayoutManager(this));
        rvContentList.setAdapter(mContentAdapter);

        mSettleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSettle();
            }
        });

        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getSettle();
            }
        });
    }

    public void onClickSettleRecord(View view) {
        UiNavigation.gotoSettleRecordActivity(this);
    }

    @Override
    public void setPresenter(SettleCurrentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void initLayout() {
        llResultError.setVisibility(View.GONE);
        llResultContent.setVisibility(View.GONE);
        mSettleBtn.setEnabled(false);
    }

    @Override
    public void onCurrentSuccess(SettleSummaryResult.RespData detailData) {
        llResultContent.setVisibility(View.VISIBLE);
        llResultError.setVisibility(View.GONE);
        mSettleBtn.setEnabled(true);
        tvTimeRange.setText(detailData.startTime + " ~ " + detailData.endTime);
        mContentAdapter.setData(detailData.settleList);
    }

    @Override
    public void onCurrentFailed(String error) {
        llResultError.setVisibility(View.VISIBLE);
        llResultContent.setVisibility(View.GONE);
        mSettleBtn.setEnabled(false);
        imgError.setImageResource(R.drawable.ic_load_empty);
        tvError.setText(error);
        tvRefresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCurrentEmpty() {
        llResultError.setVisibility(View.VISIBLE);
        llResultContent.setVisibility(View.GONE);
        mSettleBtn.setEnabled(false);
        imgError.setImageResource(R.drawable.ic_load_empty);
        tvError.setText("当前没有可结算数据");
        tvRefresh.setVisibility(View.GONE);
    }
}
