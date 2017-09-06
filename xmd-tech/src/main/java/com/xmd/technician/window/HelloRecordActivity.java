package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xmd.technician.Adapter.HelloRecordAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.HelloRecordInfo;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.HelloRecordListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by ZR on 17-2-27.
 * 招呼记录
 */

public class HelloRecordActivity extends BaseActivity {
    protected static final int DEFAULT_PAGE_SIZE = 20;

    @BindView(R.id.hello_swipe_refresh)
    SwipeRefreshLayout mHelloRefresh;
    @BindView(R.id.hello_list)
    RecyclerView mHelloRecycler;
    @BindView(R.id.empty_view_widget)
    EmptyView mHelloEmpty;

    private LinearLayoutManager mLayoutManager;
    private HelloRecordAdapter mAdapter;

    private int mLastVisibleItem;
    private int mPageCount = -1;
    private int mPages = 0;
    private boolean mIsLoadingMore = false;
    private List<HelloRecordInfo> mList = new ArrayList<>();

    private Subscription mGetHelloRecordListSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_record);
        ButterKnife.bind(this);

        setTitle(R.string.hello_record_bar_title);
        setBackVisible(true);

        mAdapter = new HelloRecordAdapter(this);
        mAdapter.setOnFooterClickListener(v -> loadMore());
        mAdapter.setOnItemClickCallback(info -> handleItemClick(info));

        mLayoutManager = new LinearLayoutManager(this);
        mHelloRecycler.setHasFixedSize(true);
        mHelloRecycler.setLayoutManager(mLayoutManager);
        mHelloRecycler.setAdapter(mAdapter);
        mHelloRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    // 加载更多
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        mHelloRefresh.setColorSchemeColors(ResourceUtils.getColor(R.color.colorMain));
        mHelloRefresh.setOnRefreshListener(() -> {
            mIsLoadingMore = false;
            mPages = 0;
            mPageCount = -1;
            loadData();
        });

        mGetHelloRecordListSubscription = RxBus.getInstance().toObservable(HelloRecordListResult.class).subscribe(
                helloRecordResult -> handleHelloRecordResult(helloRecordResult)
        );

        loadData();
    }

    private void handleItemClick(HelloRecordInfo info) {
        // 聊天
       // MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(info.receiverEmChatId, info.receiverName, info.receiverAvatar, ChatConstant.TO_CHAT_USER_TYPE_CUSTOMER));
        UINavigation.gotoChatActivity(this, info.receiverEmChatId);
    }

    private void handleHelloRecordResult(HelloRecordListResult result) {
        mHelloRefresh.setRefreshing(false);
        mPageCount = result.pageCount;
        if (result.respData != null) {
            if (!mIsLoadingMore) {
                mList.clear();
            }
            mList.addAll(result.respData);
            mAdapter.setIsNoMore(mPages == mPageCount);
            mAdapter.setData(mList);
        }
    }

    private void loadMore() {
        if (loadData()) {
            mHelloRefresh.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }

    private boolean loadData() {
        if (mPageCount < 0 || ((mPages + 1) <= mPageCount)) {
            mPages++;
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
            params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(DEFAULT_PAGE_SIZE));
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_HELLO_RECORD_LIST, params);
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.getInstance().unsubscribe(mGetHelloRecordListSubscription);
    }
}
