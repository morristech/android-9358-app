package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Adapter.ListRecycleViewAdapter;
import com.xmd.technician.R;
import com.xmd.technician.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by linms@xiaomodo.com on 16-4-29.
 */
public abstract class BaseListFragment<T> extends BaseFragment implements ListRecycleViewAdapter.Callback<T>, SwipeRefreshLayout.OnRefreshListener{
    protected static final int PAGE_START = 0;
    protected static final int PAGE_SIZE = 20;

    @Bind(R.id.swipe_refresh_widget) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.list) RecyclerView mListView;

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
        ButterKnife.bind(this, getView());
        initContent();
    }

    private void initContent() {
        initListLayout();
        initView();
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItem + 1 == mListAdapter.getItemCount()) {
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

        if(getListSafe()){
            //上拉刷新，加载更多数据
            mSwipeRefreshLayout.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }

    /**
     * Only when current page is less than the pageCount, it's able to send request to server
     * @return true means it really sends the request
     */
    private boolean getListSafe(){
        if(mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages ++;
          dispatchRequest();
            return true;
        }
        return false;
    }

    /**
     *
     * @param pageCount
     * @param list - The List with T
     */
    protected void onGetListSucceeded(int pageCount, List<T> list) {

        mPageCount = pageCount;
        mSwipeRefreshLayout.setRefreshing(false);
        if (list != null) {
            if(!mIsLoadingMore || pageCount<-100) {
                if(pageCount <-100){
                    mPages = PAGE_START;
                }
                mData.clear();
            }
            mData.addAll(list);
            mListAdapter.setIsNoMore(mPages == mPageCount);
            mListAdapter.setData(mData);
        }
    }

    protected void onGetListFailed(String errorMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        ((BaseFragmentActivity)getActivity()).makeShortToast(errorMsg);
    }

    @Override
    public void onItemClicked(T bean) throws HyphenateException {
        //implemented by sub class
    }

    @Override
    public void onNegativeButtonClicked(T bean) {
        //implemented by sub class
    }

    @Override
    public void onPositiveButtonClicked(T bean) {
        //implemented by sub class
    }

    @Override
    public void onLoadMoreButtonClicked() {
        loadMore();
    }

    /**
     * Refresh current page and retrieve the new data from the server, not loading more
     */
    @Override
    public void onRefresh() {
        mIsLoadingMore = false;
        mPages = PAGE_START;
        mPageCount = -1;
        getListSafe();
    }

    @Override
    public boolean isHorizontalSliding(){
        return false;
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    /****  Abstract function , implemented by sub class ***/

    /**
     * deliver the http request, retrieve the data fro mserver
     */
    protected abstract void dispatchRequest();

    protected abstract void initView();

    @Override
    public void onSayHiButtonClicked(T bean) {

    }
}
