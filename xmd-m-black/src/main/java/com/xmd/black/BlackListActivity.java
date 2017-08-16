package com.xmd.black;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.black.adapter.ListRecycleViewAdapter;
import com.xmd.black.bean.BlackListResult;
import com.xmd.black.bean.CustomerInfo;
import com.xmd.black.event.AddOrRemoveBlackEvent;
import com.xmd.black.event.ToBlackCustomerInfoDetailActivityEvent;
import com.xmd.black.httprequest.ConstantResource;
import com.xmd.black.httprequest.DataManager;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-7-22.
 */

public class BlackListActivity extends BaseActivity implements ListRecycleViewAdapter.Callback {
    private RecyclerView mRecyclerView;
    private ListRecycleViewAdapter mAdapter;
    private boolean isFromManager;
    private int mPages = 1;
    private int mPageSize = 20;
    private List<CustomerInfo> mListData;
    private int mLastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private int mPageCount;

    public static void startBlackListActivity(Activity activity, boolean isManager) {
        Intent intent = new Intent(activity, BlackListActivity.class);
        intent.putExtra(ConstantResource.INTENT_APP_TYPE, isManager);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        getIntentData();
        initView();
    }

    private void initView() {
        setTitle("黑名单管理");
        getBlackList();
        mPageCount = -1;
        mRecyclerView = (RecyclerView) findViewById(R.id.black_list);
        mListData = new ArrayList<>();
        mAdapter = new ListRecycleViewAdapter(BlackListActivity.this, mListData, this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(BlackListActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
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
        mPages++;
        if (mPageCount == -1 || mPageCount > mPages + 1) {
            getBlackList();
        }
    }

    private void getBlackList() {
        DataManager.getInstance().loadUserBlackList(String.valueOf(mPages), String.valueOf(mPageSize), new NetworkSubscriber<BlackListResult>() {
            @Override
            public void onCallbackSuccess(BlackListResult result) {
                onGetListSuccess(result.getPageCount(), result.getRespData());
            }


            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    private void onGetListSuccess(Integer pageCount, List<CustomerInfo> respData) {

        mPageCount = pageCount;
        if (mPages == 1) {
            mListData.clear();
        }
        mListData.addAll(respData);
        mAdapter.setIsNoMore(mPages == mPageCount);
        if (isFromManager) {
            mAdapter.setData(mListData, true);
        } else {
            mAdapter.setData(mListData, false);
        }
    }

    private void getIntentData() {
        isFromManager = getIntent().getBooleanExtra(ConstantResource.INTENT_APP_TYPE, false);
    }


    @Override
    public void onItemClicked(CustomerInfo bean, boolean isFromManager) {
        if (TextUtils.isEmpty(bean.userId) || Long.parseLong(bean.userId) <= 0) {
            XToast.show("该用户无详情信息");
        } else {
            EventBus.getDefault().post(new ToBlackCustomerInfoDetailActivityEvent(bean.userId));
        }
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Subscribe
    public void addOrRemoveBlackList(AddOrRemoveBlackEvent event) {
        mPages = 1;
        getBlackList();
        this.finish();
    }

}
