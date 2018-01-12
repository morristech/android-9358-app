package com.m.pk;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.m.pk.adapter.TechPkActivityAdapter;
import com.m.pk.bean.ActivityRankingBean;
import com.m.pk.bean.PkItemBean;
import com.m.pk.databinding.ActivityTechPkActiveListBinding;
import com.m.pk.httprequest.DataManager;
import com.m.pk.httprequest.response.PKActivityListResult;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.DateUtil;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.m.network.NetworkSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 18-1-4.
 */

public class TechPKActiveListActivity extends BaseActivity implements TechPkActivityAdapter.OnItemClickListener {

    ActivityTechPkActiveListBinding mBinding;

    private int mPage;
    private int mPageSize;
    private boolean hasMore;
    protected int mLastVisibleItem;
    private TechPkActivityAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private List<ActivityRankingBean> mActivityList;

    public static void startTechPKActiveListActivity(Context activity, String appType) {
        Intent intent = new Intent(activity, TechPKActiveListActivity.class);
        intent.putExtra(Constant.INTENT_KEY_APP_TYPE, appType);
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
        mPage = 0;
        mPageSize = 10;
        showLoading(ResourceUtils.getString(R.string.loading_hint_message), true);
        initRecyclerView();
        getPkActiveData();
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new TechPkActivityAdapter(this);
        mAdapter.setOnItemClickedListener(this);
        mActivityList = new ArrayList<>();
        mBinding.list.setLayoutManager(layoutManager);
        mBinding.list.setAdapter(mAdapter);
        mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        mBinding.swipeRefreshWidget.setColorSchemeColors(0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffffff, 0xff000000);
        mBinding.swipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                mActivityList.clear();
                getPkActiveData();
            }
        });
    }

    private void loadMore() {
        if (hasMore) {
            mBinding.swipeRefreshWidget.setRefreshing(true);
            getPkActiveData();
        }
    }

    private void getPkActiveData() {
        mPage++;
        DataManager.getInstance().getPkActivityList(String.valueOf(mPage), String.valueOf(mPageSize), new NetworkSubscriber<PKActivityListResult>() {
            @Override
            public void onCallbackSuccess(PKActivityListResult result) {
                hideLoading();
                hasMore = (mPage != result.getPageCount());
                mBinding.swipeRefreshWidget.setRefreshing(false);
                mActivityList.addAll(result.getRespData());
                mAdapter.setListData(mActivityList, hasMore);
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
        List<PkItemBean> pkList = new ArrayList<>();
        if (TextUtils.isEmpty(bean.getRankingList().get(0).getCategoryId())) {
            pkList.add(new PkItemBean(bean.getCategoryId(), bean.getCategoryName()));
        } else {
            for (int i = 0; i < bean.getRankingList().size(); i++) {
                pkList.add(new PkItemBean(bean.getRankingList().get(i).getCategoryId(), bean.getRankingList().get(i).getCategoryName()));
            }
        }
        TechPkRankingActivity.startTechPkRankingActivity(TechPKActiveListActivity.this, bean.getPkActivityId(), bean.getStatus(), bean.getStartDate(),
                TextUtils.isEmpty(bean.getEndDate()) ? DateUtil.getDate(System.currentTimeMillis()) : bean.getEndDate(),
                getIntent().getStringExtra(Constant.INTENT_KEY_APP_TYPE), pkList);
    }

    public void onRankingDetailClick(View view) {
        TechCommonRankingDetailActivity.startTechCommonRankingDetailActivity(TechPKActiveListActivity.this, getIntent().getStringExtra(Constant.INTENT_KEY_APP_TYPE));
    }
}
