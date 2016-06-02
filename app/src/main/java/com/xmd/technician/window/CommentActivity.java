package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xmd.technician.Adapter.CommentAdapter;
import com.xmd.technician.R;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CommentResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

public class CommentActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.comment_list) RecyclerView mListView;
    @Bind(R.id.swipe_refresh_widget) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.empty_view_widget) EmptyView mEmptyView;

    private CommentAdapter mAdapter;
    private Subscription mCommentSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment);

        ButterKnife.bind(this);

        setTitle(R.string.personal_fragment_layout_my_comment);
        setBackVisible(true);

        mAdapter = new CommentAdapter(this);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager( new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorMain);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mEmptyView.setEmptyPic(R.drawable.empty);
        mEmptyView.setEmptyTip("");

        mCommentSubscription = RxBus.getInstance().toObservable(CommentResult.class).subscribe(
                commentResult -> handleCommentResult(commentResult)
        );

        loadData();
    }

    private void loadData(){
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE_NUMBER,"1");
        params.put(RequestConstant.KEY_PAGE_SIZE,"10");
        params.put(RequestConstant.KEY_SORT_TYPE,"createdAt");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COMMENT_LIST,params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mCommentSubscription);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    private void handleCommentResult(CommentResult result){
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.setData(result.respData);
        if(result.respData == null || result.respData.isEmpty()){
            mEmptyView.setStatus(EmptyView.Status.Empty);
        }else {
            mEmptyView.setStatus(EmptyView.Status.Gone);
        }
    }
}
