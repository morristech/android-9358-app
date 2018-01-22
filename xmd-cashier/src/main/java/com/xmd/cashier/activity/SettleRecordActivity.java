package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.adapter.SettleRecordAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.SettleRecordContract;
import com.xmd.cashier.dal.bean.SettleRecordInfo;
import com.xmd.cashier.presenter.SettleRecordPresenter;
import com.xmd.cashier.widget.CustomLoadingLayout;
import com.xmd.cashier.widget.stickyview.StickyHeaderInterface;
import com.xmd.cashier.widget.stickyview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-3-29.
 * 结算记录
 */

public class SettleRecordActivity extends BaseActivity implements SettleRecordContract.View {
    private SettleRecordContract.Presenter mPresenter;

    private SettleRecordAdapter mAdapter;
    private RecyclerView rvSettleRecord;
    private CustomLoadingLayout lyLoad;

    private LinearLayoutManager mLayoutManager;

    private int mLastVisibleItem;

    private List<SettleRecordInfo> mDecorateData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_record);
        mPresenter = new SettleRecordPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "结算记录");

        mLayoutManager = new LinearLayoutManager(this);
        lyLoad = (CustomLoadingLayout) findViewById(R.id.layout_load);
        rvSettleRecord = (RecyclerView) findViewById(R.id.rv_record_list);

        mAdapter = new SettleRecordAdapter(this);
        mAdapter.setCallBack(new SettleRecordAdapter.CallBack() {
            @Override
            public void onItemClick(SettleRecordInfo info, int position) {
                mPresenter.onRecordClick(info);
            }

            @Override
            public void onLoadMore() {
                mPresenter.loadMore();
            }
        });
        rvSettleRecord.setHasFixedSize(true);
        rvSettleRecord.setLayoutManager(mLayoutManager);
        rvSettleRecord.addItemDecoration(new StickyRecyclerHeadersDecoration(new StickyHeaderInterface() {
            @Override
            protected String getHeaderId(int position) {
                if (TextUtils.isEmpty(mDecorateData.get(position).createTime)) {
                    return "未知";
                } else {
                    return DateUtils.doString2String(mDecorateData.get(position).createTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_ZH);
                }
            }

            @Override
            protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                return new HeadItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settle_header, parent, false));
            }

            @Override
            protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
                HeadItemHolder headItemHolder = (HeadItemHolder) holder;
                headItemHolder.mHeadYearMonth.setText(getHeaderId(position));
                headItemHolder.mHeadCount.setText("共" + mDecorateData.get(position).monthCount + "条记录");
            }

            @Override
            protected int getItemCount() {
                return mDecorateData.size();
            }

            @Override
            protected boolean showItemHeader() {
                return true;
            }
        }));
        rvSettleRecord.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    mPresenter.loadMore();
                }
            }
        });
        rvSettleRecord.setAdapter(mAdapter);

        lyLoad.setOnReloadListener(new CustomLoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View view) {
                mPresenter.loadInit();
            }
        });
    }

    public void onClickFilter(View view) {
        mPresenter.onPickView();
    }

    @Override
    public void showLoadIng() {
        lyLoad.setStatus(CustomLoadingLayout.STATUS_LOADING);
        showLoading();
    }

    @Override
    public void showLoadError() {
        lyLoad.setStatus(CustomLoadingLayout.STATUS_ERROR);
    }

    @Override
    public void showLoadEmpty() {
        lyLoad.setStatus(CustomLoadingLayout.STATUS_EMPTY);
    }

    @Override
    public void showLoadNoNetwork() {
        lyLoad.setStatus(CustomLoadingLayout.STATUS_NONETWORK);
    }

    @Override
    public void showLoadSuccess() {
        lyLoad.setStatus(CustomLoadingLayout.STATUS_SUCCESS);
    }

    @Override
    public void showMoreIng() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_LOADING);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreError() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_ERROR);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreNoNetwork() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_NO_NETWORK);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreNone() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_NONE);
    }

    @Override
    public void showMoreSuccess() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_SUCCESS);
    }

    @Override
    public void showData(List<SettleRecordInfo> list) {
        mDecorateData.addAll(list);
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearData() {
        mDecorateData.clear();
        mAdapter.clearData();
        rvSettleRecord.removeAllViews();
    }

    @Override
    public void setPresenter(SettleRecordContract.Presenter presenter) {
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

    public class HeadItemHolder extends RecyclerView.ViewHolder {
        public TextView mHeadYearMonth;
        public TextView mHeadCount;

        public HeadItemHolder(View itemView) {
            super(itemView);
            mHeadYearMonth = (TextView) itemView.findViewById(R.id.item_settle_year_month);
            mHeadCount = (TextView) itemView.findViewById(R.id.item_settle_count);
        }
    }
}
