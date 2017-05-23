package com.xmd.manager.window;

import com.xmd.manager.Constant;
import com.xmd.manager.beans.Order;
import com.xmd.manager.common.OrderManagementHelper;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CustomerOrdersResult;
import com.xmd.manager.service.response.OrderManageResult;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-25.
 */
public class CustomerOrdersActivity extends BaseListActivity<Order, CustomerOrdersResult> {

    private String mCustomerId;
    private Subscription mOrderManageSubscription;

    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER_ID, mCustomerId);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_ORDERS, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mOrderManageSubscription);
    }

    @Override
    protected void initOtherViews() {
        mCustomerId = getIntent().getStringExtra(Constant.PARAM_CUSTOMER_ID);
        if (Utils.isEmpty(mCustomerId)) {
            finish();
        }
        mOrderManageSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                orderManageResult -> onRefresh()
        );
    }

    @Override
    public void onNegativeButtonClicked(Order order) {
        OrderManagementHelper.handleOrderNegative(this, order);
    }

    @Override
    public void onPositiveButtonClicked(Order order) {
        OrderManagementHelper.handleOrderPositive(this, order);
    }

}
