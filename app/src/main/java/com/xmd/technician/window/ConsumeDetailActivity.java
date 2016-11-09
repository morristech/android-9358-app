package com.xmd.technician.window;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xmd.technician.Adapter.ConsumeDetailAdapter;
import com.xmd.technician.R;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ConsumeDetailResult;
import com.xmd.technician.bean.ConsumeInfo;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

public class ConsumeDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    public static final String EXTRA_CONSUME_TYPE = "consumeType";

    private static final int mActivityTitles[] = {R.string.user_reward_detail, R.string.coupon_reward_detail,R.string.paid_coupon_detail,R.string.paid_order_detail};
    public static final String[] mConsumeTypes ={"user_reward","coupon_reward","paid_coupon","paid_order","withdrawal"};

    @Bind(R.id.swipe_refresh_widget) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.list_view) RecyclerView mListView;

    private ConsumeDetailAdapter mAdapter;
    private Subscription mDetailSubscription;
    private String mConsumeType;
    private int mCurrentPage = 0;
    private int mPageCount = -1;
    private static final int PAGE_SIZE = 20;

    private List<ConsumeInfo> mConsumeList = new ArrayList<>();
    private boolean mIsLoadingMore = false;
    private int mLastVisibleItem;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        int type = getIntent().getExtras().getInt(EXTRA_CONSUME_TYPE);
        mConsumeType = mConsumeTypes[type];

        setTitle(mActivityTitles[type]);
        setBackVisible(true);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ConsumeDetailAdapter(this, 1);
        mAdapter.setOnFooterClickListener(v -> loadMore());
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setAdapter(mAdapter);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorMain);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mDetailSubscription = RxBus.getInstance().toObservable(ConsumeDetailResult.class).subscribe(
                consumeDetailResult -> getConsumeDetailResult(consumeDetailResult));

        getConsumeDetailInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mDetailSubscription);
    }

    @Override
    public void onRefresh() {
        mIsLoadingMore = false;
        mCurrentPage = 0;
        mPageCount = -1;
        getConsumeDetailInfo();
    }

    private boolean getConsumeDetailInfo(){
        if(mPageCount < 0 || ((mCurrentPage + 1) <= mPageCount)){
            mCurrentPage++;
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_CONSUME_TYPE, mConsumeType);
            params.put(RequestConstant.KEY_PAGE, String.valueOf(mCurrentPage));
            params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONSUME_DETAIL, params);
            return true;
        }
        return false;
    }

    private void getConsumeDetailResult(ConsumeDetailResult consumeDetailResult){
        mSwipeRefreshLayout.setRefreshing(false);
        mPageCount = consumeDetailResult.pageCount;
        if(consumeDetailResult.respData != null){
            if(!mIsLoadingMore){
                mConsumeList.clear();
            }
            mConsumeList.addAll(consumeDetailResult.respData);
            mAdapter.setIsNoMore(mCurrentPage == mPageCount);
            mAdapter.refreshDataSet(mConsumeList);
        }
    }

    private void loadMore(){
        if(getConsumeDetailInfo()){
            mSwipeRefreshLayout.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }
}
