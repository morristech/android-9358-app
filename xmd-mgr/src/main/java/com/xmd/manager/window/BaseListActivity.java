package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xmd.manager.R;
import com.xmd.manager.adapter.ListRecycleViewAdapter;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.BaseListResult;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-24.
 */
public abstract class BaseListActivity<T, S extends BaseListResult> extends BaseActivity
        implements ListRecycleViewAdapter.Callback<T>, SwipeRefreshLayout.OnRefreshListener {

    protected static final int PAGE_START = 0;
    protected static final int PAGE_SIZE = 20;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list)
    RecyclerView mListView;

    protected LinearLayoutManager mLayoutManager;
    protected ListRecycleViewAdapter mListRecycleViewAdapter;

    protected int mPages = PAGE_START;


    protected boolean mIsLoadingMore = false;
    protected int mLastVisibleItem;
    protected int mPageCount = -1;
    protected List<T> mData = new ArrayList<>();

    /**
     * use this subscription to handle the data gaind by the request we dispatched through method <b>dispatchRequest</b>
     */
    private Subscription mGetListSubscription;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewLayout();
        ButterKnife.bind(this);
        initContent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetListSubscription);
    }

    /**
     * default we set the activity_list as the layout, which include the toolbar,
     * or it's ok to override this method for implementing your own layout,
     * but only need to keep it in mind, that you need to apply the <b>layout layout_swipe_refresh_list</b> in you layout
     */
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_list);
    }

    /**
     * default we don't need to implement the page if it's only a list, but somehow, we will need to add some other components in the page
     * so we can implement the custom view layout here
     */
    protected void initOtherViews() {

    }

    /**
     * default, we just put the return list as a result to the adapter, but somehow, we want to filter list on purpose, it's going to be done here
     */
    protected List<T> filterList(List<T> result) {
        return result;
    }

    private void initContent() {
        initListLayout();
        initOtherViews();
        onRefresh();
    }

    @SuppressWarnings("unchecked")
    private void initListLayout() {

        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(mLayoutManager);
        mListRecycleViewAdapter = new ListRecycleViewAdapter(this, mData, this);
        mListView.setAdapter(mListRecycleViewAdapter);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItem + 1 == mListRecycleViewAdapter.getItemCount()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        /**
         * handle the data returned by request dispatched through method dispatchRequest()
         */
        Class<S> resultClass = (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        mGetListSubscription = RxBus.getInstance().toObservable(resultClass).subscribe(
                result -> {
                    if (mSwipeRefreshLayout.getVisibility() == View.GONE) {
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    }
                    ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> {
                        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
                            onGetListFailed(result.msg);
                        } else {
                            onGetListSucceeded(isPaged() ? result.pageCount : 0, result.respData);
                        }
                    });
                }
        );
    }

    private void loadMore() {
        if (getListSafe()) {
            //上拉刷新，加载更多数据
            mSwipeRefreshLayout.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }

    /**
     * Only when current page is less than the pageCount, it's able to send request to server
     *
     * @return true means it really sends the request
     */
    private boolean getListSafe() {

        if (mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages++;
            dispatchRequest();
            return true;
        }
        return false;
    }

    /**
     * @param pageCount
     * @param list      - The List with T
     */
    protected void onGetListSucceeded(int pageCount, List<T> list) {

        mPageCount = pageCount;
        mSwipeRefreshLayout.setRefreshing(false);
        if (list != null) {
            if (!mIsLoadingMore) {
                mData.clear();
            }
            // we filter the result list for some purpose if necessary
            List<T> filteredResult = filterList(list);
            mData.addAll(filteredResult);
            mListRecycleViewAdapter.setIsNoMore(mPages == mPageCount);
            mListRecycleViewAdapter.setData(mData);
        }

    }

    protected void onGetListFailed(String errorMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        makeShortToast(errorMsg);
    }

    @Override
    public void onItemClicked(T bean) {

    }

    @Override
    public void onSlideDeleteItem(T bean) {

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
    public boolean isPaged() {
        return true;
    }

    /****
     * Abstract function , implemented by sub class
     ***/

    @Override
    public boolean isSlideable() {
        return false;
    }

    /**
     * deliver the http request, retrieve the data fro mserver
     */

    protected abstract void dispatchRequest();

    @Override
    public void onLongClicked(T bean) {

    }

    @Override
    public boolean showStatData() {
        return false;
    }
}
