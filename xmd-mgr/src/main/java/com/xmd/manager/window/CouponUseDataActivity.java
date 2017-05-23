package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.CouponUseDataListRecycleViewAdapter;
import com.xmd.manager.beans.ConsumeInfo;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CouponUseDataResult;
import com.xmd.manager.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Subscription;

/**
 * Created by sdcm on 15-12-15.
 */
public class CouponUseDataActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final int PAGE_START = 1;
    public static final int PAGE_SIZE = 500;

    @Bind(R.id.total_count)
    TextView mTotalCount;
    @Bind(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.coupon_consume_list)
    RecyclerView mCouponUseListView;

    private LinearLayoutManager mLayoutManager;
    private CouponUseDataListRecycleViewAdapter mCouponUseDataListRecycleViewAdapter;
    private List<ConsumeInfo> mData = new ArrayList<>();
    private String mActId;

    private Subscription mGetCouponUseDataSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coupon_use_data);

        Intent data = getIntent();
        mActId = data.getStringExtra(Constant.PARAM_ACT_ID);

        initViews();

        mGetCouponUseDataSubscription = RxBus.getInstance().toObservable(CouponUseDataResult.class).subscribe(
                couponUseDataResult -> handleCouponUseDataResult(couponUseDataResult)
        );

        getCouponUseDataSafe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetCouponUseDataSubscription);
    }

    private void initViews() {

        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        mCouponUseListView.setHasFixedSize(true);
        mCouponUseListView.setLayoutManager(mLayoutManager);
        mCouponUseListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mCouponUseDataListRecycleViewAdapter = new CouponUseDataListRecycleViewAdapter(mData);
        mCouponUseListView.setAdapter(mCouponUseDataListRecycleViewAdapter);
    }

    private void handleCouponUseDataResult(CouponUseDataResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetCouponUseDataFailed(result.msg);
        } else {
            onGetCouponUseDataSucceeded(result);
        }
    }

    private void onGetCouponUseDataSucceeded(CouponUseDataResult couponUseDataResult) {

        mTotalCount.setText(couponUseDataResult.respData.total);
        mSwipeRefreshLayout.setRefreshing(false);
        mData = couponUseDataResult.respData.consumes;
        mCouponUseDataListRecycleViewAdapter.setData(mData);
    }

    private void onGetCouponUseDataFailed(String errorMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        makeShortToast(errorMsg);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    /**
     * Only when current page is less than the pageCount, it's able to send request to server
     *
     * @return true means it really sends the request
     */
    private boolean getCouponUseDataSafe() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_ACT_ID, mActId);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(PAGE_START));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_USE_DATA, params);
        return true;
    }

    private void refresh() {
        getCouponUseDataSafe();
    }
}
