package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.TechnicianAdapter;
import com.xmd.cashier.contract.TechnicianContract;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.presenter.TechnicianPresenter;
import com.xmd.cashier.widget.CustomLoadingLayout;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.List;

/**
 * Created by zr on 17-7-11.
 * 会所技师列表
 */

public class TechnicianActivity extends BaseActivity implements TechnicianContract.View {
    private TechnicianContract.Presenter mPresenter;

    private TechnicianAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private SwipeRefreshLayout mRefreshLayout;
    private CustomLoadingLayout mLoadLayout;
    private RecyclerView mRecyclerList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician);
        mPresenter = new TechnicianPresenter(this, this);
        initView();
        onInit();
    }

    private void onInit() {
        // 进入页面时显示加载进度
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mPresenter.load(true);
            }
        });
    }

    private void initView() {
        showToolbar(R.id.toolbar, "会所技师");
        mLayoutManager = new LinearLayoutManager(this);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ly_tech_refresh);
        mLoadLayout = (CustomLoadingLayout) findViewById(R.id.ly_tech_load_status);
        mRecyclerList = (RecyclerView) findViewById(R.id.rv_tech_list);
        mAdapter = new TechnicianAdapter(this);

        mAdapter.setCallBack(new TechnicianAdapter.CallBack() {
            @Override
            public void onTechItemClick(TechInfo info) {
                mPresenter.onTechSelect(info);
            }
        });

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRecyclerList.setHasFixedSize(true);
        mRecyclerList.setLayoutManager(mLayoutManager);
        mRecyclerList.setItemAnimator(new DefaultItemAnimator());
        mRecyclerList.addItemDecoration(new CustomRecycleViewDecoration(2));
        mRecyclerList.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.load(false);
            }
        });

        mLoadLayout.setOnReloadListener(new CustomLoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View view) {
                mPresenter.load(false);
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
    public void setPresenter(TechnicianContract.Presenter presenter) {
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
    public void showData(List<TechInfo> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRefreshIng() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_LOADING);
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showRefreshError() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_ERROR);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshEmpty() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_EMPTY);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshNoNetwork() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_NONETWORK);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshSuccess() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_SUCCESS);
        mRefreshLayout.setRefreshing(false);
    }
}
