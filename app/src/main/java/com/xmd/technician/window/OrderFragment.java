package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xmd.technician.Adapter.OrderListRecycleViewAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.beans.Order;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by sdcm on 16-3-24.
 */
public class OrderFragment extends BaseFragment implements OrderListRecycleViewAdapter.OnManageButtonClickedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String FILTER_ORDER_SUBMIT = "submit";
    private static final String FILTER_ORDER_ACCEPTE = "accept";
    private static final String FILTER_ORDER_COMPLETE = "complete";

    private static final int PAGE_START = 0;
    private static final int PAGE_SIZE = 20;

    @Bind(R.id.filter_order) RadioGroup mRgFilterOrder;
    @Bind(R.id.swipe_refresh_widget) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.order_list) RecyclerView mOrderListView;

    private LinearLayoutManager mLayoutManager;
    private OrderListRecycleViewAdapter mOrderListAdapter;

    private int mPages = PAGE_START;
    private boolean mIsLoadingMore = false;
    private String mFilterOrder = FILTER_ORDER_SUBMIT;
    private int mLastVisibleItem;
    private int mPageCount = -1;
    private List<Order> mOrderList = new ArrayList<>();

    private Subscription mGetOrderListSubscription;
    private Subscription mOrderManageSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ButterKnife.bind(this, getView());
        initView();
    }

    private void initView(){
        ((TextView)getView().findViewById(R.id.toolbar_title)).setText(R.string.order_fragment_title);
        mRgFilterOrder.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_pending_order:
                    filterOrder(FILTER_ORDER_SUBMIT);
                    break;
                case R.id.rb_accept_order:
                    filterOrder(FILTER_ORDER_ACCEPTE);
                    break;
                case R.id.rb_complete_order:
                    filterOrder(FILTER_ORDER_COMPLETE);
                    break;
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mOrderListView.setHasFixedSize(true);
        mOrderListView.setLayoutManager(mLayoutManager);
        mOrderListAdapter = new OrderListRecycleViewAdapter(getActivity(), mOrderList, this);
        mOrderListView.setAdapter(mOrderListAdapter);
        mOrderListView.setItemAnimator(new DefaultItemAnimator());
        mOrderListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mOrderListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItem + 1 == mOrderListAdapter.getItemCount()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        mGetOrderListSubscription = RxBus.getInstance().toObservable(OrderListResult.class).subscribe(
                orderListResult -> handleGetOrderListResult(orderListResult)
        );

        mOrderManageSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                orderManageResult -> refresh()
        );

        getOrderListSafe();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetOrderListSubscription, mOrderManageSubscription);
    }

    private void filterOrder(String filterOrder) {
        mFilterOrder = filterOrder;
        refresh();
    }

    private void handleGetOrderListResult(OrderListResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetOrderListFailed(result.msg);
        } else {
            onGetOrderListSucceeded(result.pageCount, result.respData);
        }
    }

    private void onGetOrderListSucceeded(int pageCount, List<Order> orderList) {
        mPageCount = pageCount;
        mSwipeRefreshLayout.setRefreshing(false);
        if (orderList != null) {
            if(!mIsLoadingMore) {
                mOrderList.clear();
            }
            mOrderList.addAll(orderList);
            mOrderListAdapter.setIsNoMore(mPages == mPageCount);
            mOrderListAdapter.setData(mOrderList);
        }
    }

    private void onGetOrderListFailed(String errorMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        Utils.makeShortToast(getActivity(), errorMsg);
    }

    private void loadMore() {
        if(getOrderListSafe()){
            //上拉刷新，加载更多数据
            mSwipeRefreshLayout.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }

    /**
     * Refresh current page and retrieve the new data from the server, not loading more
     */
    private void refresh() {
        mIsLoadingMore = false;
        mPages = PAGE_START;
        mPageCount = -1;
        getOrderListSafe();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onNegativeButtonClicked(Order order) {
        if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
            manageOrder(Constant.ORDER_STATUS_REJECTED, order, "");
        } else if (Constant.ORDER_STATUS_ACCEPT.equals(order.status)) {
            manageOrder(Constant.ORDER_STATUS_FAILURE, order, "");
        }
    }

    @Override
    public void onPositiveButtonClicked(Order order) {
        if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
            manageOrder(Constant.ORDER_STATUS_ACCEPT, order, "");
        } else if (Constant.ORDER_STATUS_ACCEPT.equals(order.status)) {
            manageOrder(Constant.ORDER_STATUS_COMPLETE, order, "");
        }
    }

    private void manageOrder(String type, Order order, String reason) {
        Map<String,String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PROCESS_TYPE, type);
        params.put(RequestConstant.KEY_ID, order.orderId);
        params.put(RequestConstant.KEY_REASON, reason);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANAGE_ORDER, params);
    }

    @Override
    public void onLoadMoreButtonClicked() {
        loadMore();
    }

    /**
     * Only when current page is less than the pageCount, it's able to send request to server
     * @return true means it really sends the request
     */
    private boolean getOrderListSafe(){
        if(mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages ++;
            Map<String,String> params = new HashMap<>();
            params.put(RequestConstant.KEY_FILTER_ORDER, mFilterOrder);
            params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
            params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ORDER_LIST, params);
            return true;
        }
        return false;
    }

}
