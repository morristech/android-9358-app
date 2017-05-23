package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.ListRecycleViewAdapter;
import com.xmd.manager.beans.Order;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.OrderManagementHelper;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.OrderFilterChangeResult;
import com.xmd.manager.service.response.OrderListResult;
import com.xmd.manager.service.response.OrderManageResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by lhj on 2016/12/13.
 */

public class NewOrderFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, ListRecycleViewAdapter.Callback {

    @Bind(R.id.list)
    RecyclerView mList;
    @Bind(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshWidget;


    public static final String BIZ_TYPE = "type";
    private int PAGE_START = 0;
    private int PAGE_SIZE = 20;
    private View view;
    private String mCurrentType;
    private Subscription mGetOrderListSubscription;
    private Subscription mOrderManageSubscription;
    private Subscription mFilterOrderSubscription;
    private LinearLayoutManager mLinearLayoutManager;
    private ListRecycleViewAdapter mListAdapter;
    private int mPages = PAGE_START;
    private int mLastVisibleItem;
    private int mPageCount = -1;
    private List<Order> mData = new ArrayList<>();
    private boolean mIsLoadingMore;
    private String startDate;
    private String endDate;
    private boolean isVisible;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_order, container, false);
        initData();
        ButterKnife.bind(this, view);
        isVisible = true;
        return view;
    }

    private void initData() {

        mGetOrderListSubscription = RxBus.getInstance().toObservable(OrderListResult.class).subscribe(
                orderListResult -> handleGetOrderListResult(orderListResult)
        );
        mOrderManageSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                orderManageResult -> {
                    if (orderManageResult.isSuccessful) {
                        onRefresh();
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT);
                    } else {
                        Utils.makeShortToast(getActivity(), "操作失败");
                    }

                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListLayout();
    }

    private void initListLayout() {
        mSwipeRefreshWidget.setColorSchemeResources(R.color.primary_color);
        mSwipeRefreshWidget.setOnRefreshListener(this);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mList.setHasFixedSize(true);
        mList.setLayoutManager(mLinearLayoutManager);
        mListAdapter = new ListRecycleViewAdapter(getActivity(), mData, this);
        mList.setAdapter(mListAdapter);
        mList.setItemAnimator(new DefaultItemAnimator());
        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                mLastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }

    private void loadMore() {
        if (getListSafe()) {
            //上拉刷新，加载更多数据
            mSwipeRefreshWidget.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }

    private boolean getListSafe() {
        if (mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages++;
            getData();
            return true;
        }
        return false;
    }


    private void handleGetOrderListResult(OrderListResult result) {
        if (result.statusCode == 200) {
            startDate = result.startTime;
            endDate = result.endTime;
            if (isVisible) {
                TextView tvST = (TextView) getActivity().findViewById(R.id.startTime);
                TextView tvED = (TextView) getActivity().findViewById(R.id.endTime);
                tvST.setText(startDate);
                tvED.setText(endDate);
            }
            mSwipeRefreshWidget.setRefreshing(false);
            mPageCount = result.pageCount;
            if (result.respData != null) {
                if (!mIsLoadingMore) {
                    mData.clear();
                }
                mData.addAll(result.respData);
                mListAdapter.setIsNoMore(mPages == mPageCount);
                mListAdapter.setData(mData);
            }
        } else {
            mSwipeRefreshWidget.setRefreshing(false);
            Utils.makeShortToast(getActivity(), result.msg);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && isVisible) {
            mSwipeRefreshWidget.setRefreshing(true);
            TextView tvStart = (TextView) getActivity().findViewById(R.id.startTime);
            TextView tvEnd = (TextView) getActivity().findViewById(R.id.endTime);
            startDate = tvStart.getText().toString();
            endDate = tvEnd.getText().toString();
            getData();

        }


    }

    @Override
    protected void initView() {
        mCurrentType = "";
        mFilterOrderSubscription = RxBus.getInstance().toObservable(OrderFilterChangeResult.class).subscribe(
                orderType -> {
                    startDate = orderType.startTime;
                    endDate = orderType.endTime;
                    mCurrentType = Constant.ORDER_TYPE_LABELS.get(orderType.filterText);
                    if (getUserVisibleHint()) {
                        onRefresh();
                    }
                }
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetOrderListSubscription, mOrderManageSubscription, mFilterOrderSubscription);
        ButterKnife.unbind(this);
    }

    private void getData() {
        if (Utils.isEmpty(mCurrentType)) {
            mCurrentType = "";
        }
        if (Utils.isEmpty(startDate)) {
            startDate = DateUtil.getCurrentDate();
        }
        if (Utils.isEmpty(endDate)) {
            endDate = DateUtil.getCurrentDate();
        }
        if (mPages == 0) {
            mPages = 1;
        }

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_ORDER_STATUS, mCurrentType);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        params.put(RequestConstant.KEY_START_DATE, startDate);
        params.put(RequestConstant.KEY_END_DATE, endDate);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ORDER_LIST, params);
    }

    @Override
    public void onRefresh() {
        mPages = 0;
        mData.clear();
        getData();
    }

    @Override
    public void onItemClicked(Object bean) {

    }

    @Override
    public void onNegativeButtonClicked(Object bean) {
        OrderManagementHelper.handleOrderNegative(getActivity(), (Order) bean);
    }

    @Override
    public void onPositiveButtonClicked(Object bean) {
        OrderManagementHelper.handleOrderPositive(getActivity(), (Order) bean);
    }

    @Override
    public void onLoadMoreButtonClicked() {

    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Override
    public void onSlideDeleteItem(Object bean) {

    }

    @Override
    public boolean isSlideable() {
        return false;
    }

    @Override
    public void onLongClicked(Object bean) {

    }
}
