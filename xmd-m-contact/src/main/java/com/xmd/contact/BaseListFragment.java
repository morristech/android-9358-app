package com.xmd.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.xmd.app.BaseFragment;
import com.xmd.contact.adapter.ListRecycleViewAdapter;



import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static com.xmd.contact.R.id.swipe_refresh_widget;

/**
 * Created by Lhj on 17-7-27.
 */

public abstract class BaseListFragment<T> extends BaseFragment implements ListRecycleViewAdapter.Callback<T>, SwipeRefreshLayout.OnRefreshListener {


    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mListView;
    protected LinearLayout mLlContactNone;

    protected static final int PAGE_START = 0;
    protected static final int PAGE_SIZE = 20;
    protected LinearLayoutManager mLayoutManager;
    protected ListRecycleViewAdapter mListAdapter;
    protected int mPages = PAGE_START;
    protected boolean mIsLoadingMore = false;
    protected int mLastVisibleItem;
    protected int mPageCount = -1;
    protected List<T> mData = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(swipe_refresh_widget);
        mListView = (RecyclerView) getView().findViewById(R.id.contact_list);
        mLlContactNone = (LinearLayout) getView().findViewById(R.id.ll_contact_none);
        initContent();

    }

    private void initContent() {
        initListLayout();
        getListSafe();
    }

    protected void initListLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(mLayoutManager);
        mListAdapter = new ListRecycleViewAdapter(getActivity(), mData, this);
        mListView.setAdapter(mListAdapter);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastViewPosition = mLayoutManager.findLastVisibleItemPosition();
                if (lastViewPosition != NO_POSITION) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastViewPosition + 1 == mListAdapter.getItemCount()) {
                        loadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    protected void onGetListSucceeded(int pageCount, List<T> list, boolean isManager) {
        mPageCount = pageCount;
        mSwipeRefreshLayout.setRefreshing(false);
        if (list != null) {
            if (!mIsLoadingMore)
                mData.clear();
        }
        mData.addAll(list);
        mListAdapter.setIsNoMore(mPages == mPageCount);
        mListAdapter.setContactData(mData, isManager);
    }

    private void loadMore() {
        if (getListSafe()) {
            //上拉刷新，加载更多数据
            mSwipeRefreshLayout.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }

    private boolean getListSafe() {
        if (mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages++;
            dispatchRequest();
            return true;
        }
        return false;
    }

    protected abstract void dispatchRequest();

    @Override
    public void onRefresh() {
        mIsLoadingMore = false;
        mPages = PAGE_START;
        mPageCount = -1;
        getListSafe();
    }

    @Override
    public void onItemClicked(T bean, String type) {

    }

    @Override
    public void onPositiveButtonClicked(T bean, int position,boolean isThanks) {

    }

    @Override
    public boolean isPaged() {
        return true;
    }
}
