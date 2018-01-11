package com.m.pk;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m.pk.adapter.TechCommonRankingDetailAdapter;
import com.m.pk.bean.TechRankingBean;
import com.m.pk.databinding.FragmentTechCommonRankingDetailBinding;
import com.m.pk.event.DateChangedEvent;
import com.m.pk.httprequest.DataManager;
import com.m.pk.httprequest.RequestConstant;
import com.m.pk.httprequest.response.TechRankingListResult;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.utils.DateUtil;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Lhj on 17-3-9.
 */

public class TechCommonRankingDetailFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String BIZ_TYPE = "type";
    public static final String USER_TYPE = "user_type";

    private int mRange;
    private String mUserType;
    private String mSortKey;
    private String mStartDate;
    private String mEndDate;
    private LinearLayoutManager mLayoutManager;
    private List<TechRankingBean> mDataList;
    private int mPage;
    private int mPageSize = 20;
    private boolean hasMore;
    protected int mLastVisibleItem;

    private FragmentTechCommonRankingDetailBinding mBinding;
    private TechCommonRankingDetailAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tech_common_ranking_detail, container, false);
        EventBus.getDefault().register(this);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mRange = getArguments().getInt(BIZ_TYPE);
        mUserType = getArguments().getString(USER_TYPE);
        switch (mRange) {
            case Constant.BIZ_TYPE_REGISTER:
                mSortKey = RequestConstant.KEY_TECH_SORT_BY_USER;
                break;
            case Constant.BIZ_TYPE_SALE:
                mSortKey = RequestConstant.KEY_TECH_SORT_BY_PAID;
                break;
            case Constant.BIZ_TYPE_SERVICE:
                mSortKey = RequestConstant.KEY_TECH_SORT_BY_COMMENT;
                break;

        }
        mDataList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.swipeTechRankingList.setOnRefreshListener(this);
        mBinding.swipeTechRankingList.setColorSchemeColors(0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffffff, 0xff000000);
        mAdapter = new TechCommonRankingDetailAdapter(getActivity());
        mBinding.recyclerRankingList.setLayoutManager(mLayoutManager);
        mBinding.recyclerRankingList.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerRankingList.setHasFixedSize(true);
        mBinding.recyclerRankingList.setAdapter(mAdapter);
        mStartDate = DateUtil.getFirstDayOfWeek(new Date(), Constant.FORMAT_YEAR);
        mEndDate = DateUtil.getCurrentDate(System.currentTimeMillis(), Constant.FORMAT_YEAR);
        mBinding.recyclerRankingList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
        onRefresh();
    }

    private void loadMore() {
        if (hasMore) {
            getTechCommonRankingList();
        }
    }

    private void getTechCommonRankingList() {
        mBinding.swipeTechRankingList.setRefreshing(true);
        mPage++;
        DataManager.getInstance().getTechRankingList(mUserType, mSortKey, mStartDate, mEndDate, String.valueOf(mPage), String.valueOf(mPageSize), new NetworkSubscriber<TechRankingListResult>() {
            @Override
            public void onCallbackSuccess(TechRankingListResult result) {
                hasMore = (mPage != result.getPageCount());
                for (TechRankingBean bean : result.getRespData()) {
                    bean.setType(mSortKey);
                }
                mDataList.addAll(result.getRespData());
                mAdapter.setRankingListDetailData(mDataList, hasMore);
                mBinding.swipeTechRankingList.setRefreshing(false);
            }

            @Override
            public void onCallbackError(Throwable e) {
                mBinding.swipeTechRankingList.setRefreshing(false);
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    @Subscribe
    public void DateChangedSubscribe(DateChangedEvent event) {
        mStartDate = event.startDate;
        mEndDate = event.endDate;
        onRefresh();
    }

    @Override
    public void onRefresh() {
        mPage = 0;
        mDataList.clear();
        getTechCommonRankingList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
