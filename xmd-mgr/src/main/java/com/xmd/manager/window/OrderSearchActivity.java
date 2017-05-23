package com.xmd.manager.window;

import android.view.View;
import android.widget.ImageView;

import com.xmd.manager.R;
import com.xmd.manager.beans.Order;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.OrderManagementHelper;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.OrderManageResult;
import com.xmd.manager.service.response.OrderSearchListResult;
import com.xmd.manager.widget.ClearableEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/12/23.
 */

public class OrderSearchActivity extends BaseListActivity<Order, OrderSearchListResult> {


    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.search_order)
    ClearableEditText mSearchOrder;
    @Bind(R.id.iv_search)
    ImageView mIvSearch;

    private String mSearchPhone;
    private boolean mIsLoaded = false;
    private Subscription mOrderManageSubscription;

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_order_search);
    }

    @Override
    protected void initOtherViews() {
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mOrderManageSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                orderManageResult -> {
                    onRefresh();
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT);
                }
        );
    }

    @Override
    protected void dispatchRequest() {
        if (mIsLoaded) {
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_ORDER_STATUS, "");
            params.put(RequestConstant.KEY_SEARCH_TELEPHONE, mSearchPhone);
            params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
            params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
            params.put(RequestConstant.KEY_START_DATE, "2015-01-01");
            params.put(RequestConstant.KEY_END_DATE, DateUtil.getCurrentDate());
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SEARCH_ORDER_LIST, params);
        }

    }


    @OnClick({R.id.iv_back, R.id.iv_search})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_search:
                mSearchPhone = mSearchOrder.getText().toString();
                if (Utils.isEmpty(mSearchPhone)) {
                    makeShortToast(ResourceUtils.getString(R.string.alert_order_search));
                    return;
                }
                mIsLoaded = true;
                onRefresh();
                break;
        }
    }

    @Override
    public void onNegativeButtonClicked(Order bean) {
        OrderManagementHelper.handleOrderNegative(this, bean);
    }

    @Override
    public void onPositiveButtonClicked(Order bean) {
        OrderManagementHelper.handleOrderPositive(this, bean);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mOrderManageSubscription);
    }
}
