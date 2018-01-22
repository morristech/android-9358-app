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
import com.xmd.cashier.adapter.SettleContentAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.SettleDetailContract;
import com.xmd.cashier.dal.bean.SettleRecordInfo;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.presenter.SettleDetailPresenter;

/**
 * Created by zr on 17-4-7.
 * 结算记录详情
 */

public class SettleDetailActivity extends BaseActivity implements SettleDetailContract.View {
    private SettleDetailContract.Presenter mPresenter;

    private Button mPrintBtn;

    private LinearLayout mResultContent;
    private TextView mTimeRange;
    private TextView mCreateTime;
    private TextView mOperatorName;
    private RecyclerView mContentList;

    private LinearLayout mResultError;
    private ImageView mErrorImage;
    private TextView mErrorDesc;

    private SettleContentAdapter mContentAdapter;

    private SettleRecordInfo mRecordInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_detail);
        mPresenter = new SettleDetailPresenter(this, this);
        mRecordInfo = (SettleRecordInfo) getIntent().getSerializableExtra(AppConstants.EXTRA_RECORD_INFO);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "结算记录");

        mResultContent = (LinearLayout) findViewById(R.id.ll_result_content);
        mTimeRange = (TextView) findViewById(R.id.tv_settle_time_range);
        mContentList = (RecyclerView) findViewById(R.id.rv_content_list);
        mCreateTime = (TextView) findViewById(R.id.tv_settle_create_time);
        mOperatorName = (TextView) findViewById(R.id.tv_settle_operator);

        mResultError = (LinearLayout) findViewById(R.id.ll_result_error);
        mErrorImage = (ImageView) findViewById(R.id.img_error);
        mErrorDesc = (TextView) findViewById(R.id.tv_error);

        mPrintBtn = (Button) findViewById(R.id.btn_settle_print);

        mContentAdapter = new SettleContentAdapter(this);
        mContentList.setHasFixedSize(true);
        mContentList.setLayoutManager(new LinearLayoutManager(this));
        mContentList.setAdapter(mContentAdapter);

        mPrintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPrint();
            }
        });
    }

    @Override
    public void setPresenter(SettleDetailContract.Presenter presenter) {
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
        mResultError.setVisibility(View.GONE);
        mResultContent.setVisibility(View.GONE);
        mPrintBtn.setEnabled(false);
    }

    @Override
    public SettleRecordInfo returnRecordInfo() {
        return mRecordInfo;
    }

    @Override
    public void onDetailSuccess(SettleSummaryResult.RespData respData) {
        mResultError.setVisibility(View.GONE);
        mResultContent.setVisibility(View.VISIBLE);
        mPrintBtn.setEnabled(true);
        mTimeRange.setText(respData.startTime + " ~ " + respData.endTime);
        mCreateTime.setText("结算时间：" + respData.createTime);
        mOperatorName.setText("结算人员：" + respData.settleName);
        mContentAdapter.setData(respData.settleList);
    }

    @Override
    public void onDetailFailed(String error) {
        mResultError.setVisibility(View.VISIBLE);
        mResultContent.setVisibility(View.GONE);
        mPrintBtn.setEnabled(false);
        mErrorImage.setImageResource(R.drawable.ic_load_empty);
        mErrorDesc.setText(error);
    }
}
