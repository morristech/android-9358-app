package com.xmd.manager.window;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.R;
import com.xmd.manager.beans.Order;
import com.xmd.manager.common.OrderManagementHelper;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.OrderListFilterResult;
import com.xmd.manager.service.response.OrderManageResult;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Lhj on 17-9-21.
 */

public class OrderListFilterFragment extends BaseListFragment<Order> {

    private String mStartDate;
    private String mEndDate;
    private String mItemId;
    private String mStatus;
    private String mTechId;
    private String mSearchPhone;

    private Map<String, String> mParams;
    private View mView;
    private Subscription mOrderListFilterSubscription;
    private Subscription mOrderManagerSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_order_filter_list, container, false);
        return mView;
    }

    @Override
    protected void dispatchRequest() {
        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        mParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        mParams.put(RequestConstant.KEY_ORDER_FILTER_START_DATE, TextUtils.isEmpty(mStartDate) ? "" : mStartDate);
        mParams.put(RequestConstant.KEY_ORDER_FILTER_END_DATE, TextUtils.isEmpty(mEndDate) ? "" : mEndDate);
        mParams.put(RequestConstant.KEY_ORDER_FILTER_STATUS, TextUtils.isEmpty(mStatus) ? "" : mStatus);
        mParams.put(RequestConstant.KEY_ORDER_FILTER_ITEM_ID, TextUtils.isEmpty(mItemId) ? "" : mItemId);
        mParams.put(RequestConstant.KEY_ORDER_FILTER_TECH_ID, TextUtils.isEmpty(mTechId) ? "" : mTechId);
        mParams.put(RequestConstant.KEY_ORDER_FILTER_TELEPHONE, TextUtils.isEmpty(mSearchPhone) ? "" : mSearchPhone);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ORDER_FILTER_LIST, mParams);
    }

    public void searchOrder(String phone){
        this.mSearchPhone = phone;
        onRefresh();
    }

    public void cancelSearchOrder(){
        this.mSearchPhone = "";
        onRefresh();
    }

    @Override
    protected void initView() {
        mParams = new HashMap<>();
        mStartDate = mArguments.getString(RequestConstant.KEY_ORDER_FILTER_START_DATE);
        mEndDate = mArguments.getString(RequestConstant.KEY_ORDER_FILTER_END_DATE);
        mStatus = mArguments.getString(RequestConstant.KEY_ORDER_FILTER_STATUS);
        mItemId = mArguments.getString(RequestConstant.KEY_ORDER_FILTER_ITEM_ID);
        mTechId = mArguments.getString(RequestConstant.KEY_ORDER_FILTER_TECH_ID);
        mSearchPhone = mArguments.getString(RequestConstant.KEY_ORDER_FILTER_TELEPHONE);
        mOrderListFilterSubscription = RxBus.getInstance().toObservable(OrderListFilterResult.class).subscribe(
                result -> handlerOrderListFilterResult(result)
        );
        mOrderManagerSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                result -> handlerOrderManagerResult(result)
        );
    }

    private void handlerOrderListFilterResult(OrderListFilterResult result) {
        if (result.statusCode == 200) {
            if (result.respData == null) {
                return;
            }
            onGetListSucceeded(result.pageCount,result.respData);
        }else{
            onGetListFailed("获取订单失败");
        }
    }

    private void handlerOrderManagerResult(OrderManageResult result) {
        XToast.show(result.msg);
        if (result.isSuccessful) {
            onRefresh();
        }
    }

    public void setRefreshData(String startDate, String endDate, String status, String itemId, String techId, String telephone) {
        mParams.clear();
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        this.mStatus = status;
        this.mItemId = itemId;
        this.mTechId = techId;
        this.mSearchPhone = telephone;
        onRefresh();
    }

    @Override
    public void onNegativeButtonClicked(Order bean) {
        OrderManagementHelper.handleOrderNegative(getActivity(), bean);
    }

    @Override
    public void onPositiveButtonClicked(Order bean) {
        OrderManagementHelper.handleOrderPositive(getActivity(), bean);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mOrderListFilterSubscription,mOrderManagerSubscription);
    }
}
