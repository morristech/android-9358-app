package com.xmd.m.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.m.R;
import com.xmd.m.comment.adapter.ListRecycleViewAdapter;
import com.xmd.m.comment.bean.RewardBean;
import com.xmd.m.comment.bean.RewardListResult;
import com.xmd.m.comment.bean.TechRewardListResult;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.m.comment.httprequest.DataManager;
import com.xmd.m.comment.httprequest.RequestConstant;
import com.xmd.m.network.NetworkSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-7-5.
 */

public class CustomerRewardDetailFragment extends BaseFragment implements ListRecycleViewAdapter.Callback<RewardBean> {

    private static final int PAGE_START = 1;
    private static final int PAGE_SIZE = 10;
    private LinearLayoutManager mLayoutManager;
    private ListRecycleViewAdapter mListAdapter;
    private int mPages;
    private boolean mIsLoadingMore = false;
    private int mLastVisibleItem;
    private int mPageCount = -1;
    private List<RewardBean> mRewardList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private String userId;
    private String intentType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward_detail_list, container, false);
        userId = getArguments().getString(RequestConstant.KEY_USER_ID);
        intentType = getArguments().getString(ConstantResources.INTENT_TYPE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_view);
        initListLayout();
        getRewardList();
        return view;
    }

    private void initListLayout() {
        mPages = PAGE_START;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mListAdapter = new ListRecycleViewAdapter(getContext(), mRewardList, this);
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mListAdapter.getItemCount() && mRewardList.size() > 0) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void loadMore() {
        if (getListSafe()) {
            mIsLoadingMore = true;
        }
    }

    private boolean getListSafe() {
        if (mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages++;
            getRewardList();
            return true;
        }
        return false;
    }


    private void getRewardList() {
        if (intentType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
            DataManager.getInstance().loadRewardList(String.valueOf(mPages), String.valueOf(PAGE_SIZE), userId, new NetworkSubscriber<RewardListResult>() {
                @Override
                public void onCallbackSuccess(RewardListResult result) {
                    onGetListSucceeded(result.getPageCount(), result.getRespData());
                }

                @Override
                public void onCallbackError(Throwable e) {
                    onGetListFailed(e.getLocalizedMessage());
                }
            });
        } else {
            DataManager.getInstance().loadTechRewardList(String.valueOf(mPages), String.valueOf(PAGE_SIZE), userId, new NetworkSubscriber<TechRewardListResult>() {
                @Override
                public void onCallbackSuccess(TechRewardListResult result) {
                    onGetListSucceeded(result.getPageCount(), result.getRespData());
                }

                @Override
                public void onCallbackError(Throwable e) {
                    onGetListFailed(e.getLocalizedMessage());
                }
            });
        }

    }

    private void onGetListSucceeded(int pageCount, List<RewardBean> list) {
        mPageCount = pageCount;
        if (list != null) {
            if (!mIsLoadingMore || pageCount <= -1) {
                mRewardList.clear();
            }
            mRewardList.addAll(list);
            mListAdapter.setIsNoMore(mPages == mPageCount);
            if (intentType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
                mListAdapter.setData(mRewardList, true,"");
            } else {
                mListAdapter.setData(mRewardList, false,"");
            }

        }
    }

    private void onGetListFailed(String localizedMessage) {
        XToast.show(localizedMessage);
    }



    @Override
    public void onItemClicked(RewardBean bean, String type) {

    }

    @Override
    public void onNegativeButtonClicked(RewardBean bean, int position) {

    }

    @Override
    public void onPositiveButtonClicked(RewardBean bean, int position) {

    }

    @Override
    public void onLoadMoreButtonClicked() {

    }

    @Override
    public boolean isPaged() {
        return true;
    }
}
