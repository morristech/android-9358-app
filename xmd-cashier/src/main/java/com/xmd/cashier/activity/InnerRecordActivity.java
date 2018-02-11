package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.InnerRecordAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerRecordContract;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.presenter.InnerRecordPresenter;
import com.xmd.cashier.widget.CustomLoadingLayout;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.List;

/**
 * Created by zr on 17-11-1.
 * 内网收银记录
 */

public class InnerRecordActivity extends BaseActivity implements InnerRecordContract.View {
    private InnerRecordContract.Presenter mPresenter;
    private InnerRecordAdapter mAdapter;
    private LinearLayoutManager mLayoutManger;

    private SwipeRefreshLayout mRefreshLayout;
    private CustomLoadingLayout mLoadingLayout;
    private RecyclerView mRecyclerList;

    private int mLastVisibleItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_record);
        mPresenter = new InnerRecordPresenter(this, this);
        initView();
        initRefresh();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "结账提醒");
        mAdapter = new InnerRecordAdapter(this);
        mLayoutManger = new LinearLayoutManager(this);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ly_list_refresh);
        mLoadingLayout = (CustomLoadingLayout) findViewById(R.id.ly_load_status);
        mRecyclerList = (RecyclerView) findViewById(R.id.rv_list_data);

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return ViewCompat.canScrollVertically(mRecyclerList, -1);
            }
        });
        mRecyclerList.setHasFixedSize(true);
        mRecyclerList.setLayoutManager(mLayoutManger);
        mRecyclerList.setItemAnimator(new DefaultItemAnimator());
        mRecyclerList.addItemDecoration(new CustomRecycleViewDecoration(2));
        mRecyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    mPresenter.loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManger.findLastVisibleItemPosition();
            }
        });

        mAdapter.setCallBack(new InnerRecordAdapter.InnerRecordCallBack() {
            @Override
            public void onLoadMore() {
                mPresenter.loadMore();
            }

            @Override
            public void onItemDetail(TradeRecordInfo info) {
                mPresenter.onDetail(info);
            }

            @Override
            public void onItemPrintClient(TradeRecordInfo info) {
                mPresenter.printClient(info);
            }

            @Override
            public void onItemPrintClub(TradeRecordInfo info) {
                mPresenter.printClub(info);
            }

            @Override
            public void onItemPay(TradeRecordInfo info) {
                mPresenter.onPay(info);
            }
        });
        mRecyclerList.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.load(false);
            }
        });

        mLoadingLayout.setOnReloadListener(new CustomLoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View view) {
                mPresenter.load(false);
            }
        });
    }

    private void initRefresh() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mPresenter.load(true);
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
    public void setPresenter(InnerRecordContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void clearData() {
        mAdapter.clearData();
        mRecyclerList.removeAllViews();
    }

    @Override
    public void showData(List<TradeRecordInfo> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRefreshIng() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_LOADING);
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showRefreshError() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_ERROR);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshEmpty() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_EMPTY);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshNoNetwork() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_NONETWORK);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshSuccess() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_SUCCESS);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMoreLoading() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_LOADING);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    @Override
    public void showMoreError() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_ERROR);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    @Override
    public void showMoreNoNetwork() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_NO_NETWORK);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    @Override
    public void showMoreNone() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_NONE);
    }

    @Override
    public void showMoreSuccess() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_SUCCESS);
    }
}
