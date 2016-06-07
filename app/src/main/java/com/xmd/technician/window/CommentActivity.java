package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xmd.technician.Adapter.CommentAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.CommentInfo;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CommentResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

public class CommentActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
    protected static final int PAGE_SIZE = 20;

    @Bind(R.id.comment_list) RecyclerView mListView;
    @Bind(R.id.swipe_refresh_widget) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.empty_view_widget) EmptyView mEmptyView;

    private CommentAdapter mAdapter;
    private Subscription mCommentSubscription;
    private int mLastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private int mPageCount = -1;
    private int mPages = 0;
    private boolean mIsLoadingMore = false;
    private List<CommentInfo> mCommentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment);

        ButterKnife.bind(this);

        setTitle(R.string.personal_fragment_layout_my_comment);
        setBackVisible(true);

        mAdapter = new CommentAdapter(this);
        mAdapter.setOnFooterClickListener(v -> loadMore());
        mLayoutManager =  new LinearLayoutManager(this);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setAdapter(mAdapter);
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

        mCommentSubscription = RxBus.getInstance().toObservable(CommentResult.class).subscribe(
                commentResult -> handleCommentResult(commentResult)
        );

        loadData();
    }

    private void loadMore(){
        if(loadData()){
            mSwipeRefreshLayout.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }

    private boolean loadData(){
        if(mPageCount < 0 || ((mPages + 1) <= mPageCount)){
            mPages++;
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_PAGE,String.valueOf(mPages));
            params.put(RequestConstant.KEY_PAGE_SIZE,String.valueOf(PAGE_SIZE));
            params.put(RequestConstant.KEY_SORT_TYPE,"createdAt");
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COMMENT_LIST,params);
            return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mCommentSubscription);
    }

    @Override
    public void onRefresh() {
        mIsLoadingMore = false;
        mPages = 0;
        mPageCount = -1;
        loadData();
    }

    private void handleCommentResult(CommentResult result){
        mSwipeRefreshLayout.setRefreshing(false);
        mPageCount = result.pageCount;
        if(result.respData != null){
            if(!mIsLoadingMore){
                mCommentList.clear();
            }
            mCommentList.addAll(result.respData);
            mAdapter.setIsNoMore(mPages == mPageCount);
            mAdapter.setData(mCommentList);
        }
    }
}
