package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.Order;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.AlertDialogBuilder;
import com.xmd.technician.widget.DividerItemDecoration;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import rx.Subscription;

/**
 * Created by sdcm on 16-3-24.
 */
public class OrderFragment extends BaseListFragment<Order> {

    @Bind(R.id.filter_order) RadioGroup mRgFilterOrder;
    private String mFilterOrder = Constant.FILTER_ORDER_SUBMIT;
    private Subscription mGetOrderListSubscription;
    private Subscription mOrderManageSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    protected void initView(){
        initTitleView(ResourceUtils.getString(R.string.order_fragment_title));
        mRgFilterOrder.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_pending_order:
                    filterOrder(Constant.FILTER_ORDER_SUBMIT);
                    break;
                case R.id.rb_accept_order:
                    filterOrder(Constant.FILTER_ORDER_ACCEPT);
                    break;
                case R.id.rb_complete_order:
                    filterOrder(Constant.FILTER_ORDER_COMPLETE);
                    break;
            }
        });
        mListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mGetOrderListSubscription = RxBus.getInstance().toObservable(OrderListResult.class).subscribe(
                orderListResult -> handleGetOrderListResult(orderListResult)
        );

        mOrderManageSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                orderManageResult -> onRefresh()
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetOrderListSubscription, mOrderManageSubscription);
    }

    private void filterOrder(String filterOrder) {
        mFilterOrder = filterOrder;
        onRefresh();
    }

    private void handleGetOrderListResult(OrderListResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetListFailed(result.msg);
        } else {
            onGetListSucceeded(result.pageCount, result.respData);
        }
    }

    @Override
    public void onItemClicked(Order order) {
        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.KEY_ORDER, order);
        startActivity(intent);
    }

    @Override
    public void onNegativeButtonClicked(Order order) {
        if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
            doNegativeOrder(ResourceUtils.getString(R.string.order_detail_reject_order_confirm), Constant.ORDER_STATUS_REJECTED, order, "");
        } else if (Constant.ORDER_STATUS_ACCEPT.equals(order.status)) {
            doNegativeOrder(ResourceUtils.getString(R.string.order_detail_failure_order_confirm), Constant.ORDER_STATUS_FAILURE, order, "");
        }
    }

    @Override
    public void onPositiveButtonClicked(Order order) {
        if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
            doManageOrder(Constant.ORDER_STATUS_ACCEPT, order, "");
        } else if (Constant.ORDER_STATUS_ACCEPT.equals(order.status)) {
            doManageOrder(Constant.ORDER_STATUS_COMPLETE, order, "");
        } else {
            doNegativeOrder(ResourceUtils.getString(R.string.order_detail_delete_order_confirm), Constant.ORDER_STATUS_DELETE, order, "");
        }
    }

    private void doNegativeOrder(String description, String type, Order order, String reason) {
        new AlertDialogBuilder(getActivity())
                .setMessage(description)
                .setPositiveButton(ResourceUtils.getString(R.string.confirm), v -> doManageOrder(type, order,reason))
                .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                .show();
    }

    private void doManageOrder(String type, Order order, String reason) {
        Map<String,String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PROCESS_TYPE, type);
        params.put(RequestConstant.KEY_ID, order.orderId);
        params.put(RequestConstant.KEY_REASON, reason);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANAGE_ORDER, params);
    }

    @Override
    protected void dispatchRequest() {
        Map<String,String> params = new HashMap<>();
        params.put(RequestConstant.KEY_FILTER_ORDER, mFilterOrder);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ORDER_LIST, params);
    }
}
