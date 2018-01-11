package com.m.pk;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.m.pk.adapter.TechPkActivityAdapter;
import com.m.pk.bean.ActivityRankingBean;
import com.m.pk.databinding.ActivityTechPkActiveListBinding;
import com.m.pk.httprequest.DataManager;
import com.m.pk.httprequest.response.PKActivityListResult;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.m.network.NetworkSubscriber;

/**
 * Created by Lhj on 18-1-4.
 */

public class TechPKActiveListActivity extends BaseActivity implements TechPkActivityAdapter.OnItemClickListener {

    ActivityTechPkActiveListBinding mBinding;

    private int mPage;
    private int mPageSize;
    private TechPkActivityAdapter mAdapter;
    private LinearLayoutManager layoutManager;

    public static void startTechPKActiveListActivity(Context activity, String appType){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_KEY_USER_TYPE,appType);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tech_pk_active_list);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.pk_active_title));
        setBackVisible(true);
        mPage = 1;
        mPageSize = 10;
        showLoading(ResourceUtils.getString(R.string.loading_hint_message), true);
        initRecyclerView();
        getPkActiveData();
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new TechPkActivityAdapter(this);
        mAdapter.setOnItemClickedListener(this);
        mBinding.list.setLayoutManager(layoutManager);
        mBinding.list.setAdapter(mAdapter);
        mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int last = layoutManager.findLastVisibleItemPosition();
                    //in
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mBinding.swipeRefreshWidget.setColorSchemeColors(0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffffff, 0xff000000);
        mBinding.swipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getPkActiveData();
            }
        });
    }


    private void getPkActiveData() {
        DataManager.getInstance().getPkActivityList(String.valueOf(mPage), String.valueOf(mPageSize), new NetworkSubscriber<PKActivityListResult>() {
            @Override
            public void onCallbackSuccess(PKActivityListResult result) {
                hideLoading();
                mBinding.swipeRefreshWidget.setRefreshing(false);
                mAdapter.setListData(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {
                hideLoading();
                mBinding.swipeRefreshWidget.setRefreshing(false);
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onActivityClick(ActivityRankingBean bean) {
        XLogger.i(">>>", "点击了Bean");
    }

    public void onRankingDetailClick(View view) {
        TechCommonRankingDetailActivity.startTechCommonRankingDetailActivity(TechPKActiveListActivity.this,getIntent().getStringExtra(Constant.INTENT_KEY_USER_TYPE));
       // Intent intent = new Intent(TechPKActiveListActivity.this,TechCommonRankingDetailActivity.class);
    }
}