package com.xmd.m.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xmd.app.BaseFragment;
import com.xmd.m.R;
import com.xmd.m.comment.adapter.ListRecycleViewAdapter;
import com.xmd.m.comment.bean.ConsumeBean;
import com.xmd.m.comment.bean.ConsumeListResult;
import com.xmd.m.comment.bean.TechConsumeListResult;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.m.comment.httprequest.DataManager;
import com.xmd.m.comment.httprequest.RequestConstant;
import com.xmd.m.network.NetworkSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-7-5.
 */

public class CustomerConsumeDetailFragment extends BaseFragment implements ListRecycleViewAdapter.Callback<ConsumeBean> {

    private static final int PAGE_START = 1;
    private static final int PAGE_SIZE = 10;
    private LinearLayoutManager mLayoutManager;
    private ListRecycleViewAdapter mListAdapter;
    private int mPages;
    private boolean mIsLoadingMore = false;
    private int mLastVisibleItem;
    private int mPageCount = -1;
    private List<ConsumeBean> mConsumeList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private String userId;
    private String intentType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consume_detail_list, container, false);
        userId = getArguments().getString(RequestConstant.KEY_USER_ID);
        intentType = getArguments().getString(ConstantResources.INTENT_TYPE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_view);
        initListLayout();
        getConsumeList();
        return view;
    }

    private void initListLayout() {
        mPages = PAGE_START;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mListAdapter = new ListRecycleViewAdapter(getContext(), mConsumeList, this);
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mListAdapter.getItemCount() && mConsumeList.size() > 0) {
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
        if (getListSafe()) {
            mIsLoadingMore = true;
        }
    }

    private boolean getListSafe() {
        if (mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages++;
            getConsumeList();
            return true;
        }
        return false;
    }

    private void getConsumeList() {
        if (intentType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
            DataManager.getInstance().loadConsumeList(String.valueOf(mPages), String.valueOf(PAGE_SIZE), userId, new NetworkSubscriber<ConsumeListResult>() {
                @Override
                public void onCallbackSuccess(ConsumeListResult result) {
                    onGetListSucceeded(result.getPageCount(), result.getRespData());
                }

                @Override
                public void onCallbackError(Throwable e) {
                    onGetListFailed(e.getLocalizedMessage());
                }
            });
        } else {
            DataManager.getInstance().loadTechConsumeList(String.valueOf(mPages), String.valueOf(PAGE_SIZE), userId, new NetworkSubscriber<TechConsumeListResult>() {
                @Override
                public void onCallbackSuccess(TechConsumeListResult result) {
                    onGetListSucceeded(result.getPageCount(), result.getRespData());
                }

                @Override
                public void onCallbackError(Throwable e) {
                    onGetListFailed(e.getLocalizedMessage());
                }
            });
        }


    }

    private void onGetListSucceeded(Integer pageCount, List<ConsumeBean> list) {
        mPageCount = pageCount;
        if (list != null) {
            if (!mIsLoadingMore || pageCount <= -1) {
                mConsumeList.clear();
            }
            mConsumeList.addAll(list);
            mListAdapter.setIsNoMore(mPages == mPageCount);
            if (intentType.equals(ConstantResources.INTENT_TYPE_MANAGER)) {
                mListAdapter.setData(mConsumeList, true);
            } else {
                mListAdapter.setData(mConsumeList, false);
            }

        }
    }

    private void onGetListFailed(String localizedMessage) {
        Toast.makeText(getActivity(), localizedMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    @Override
    public void onItemClicked(ConsumeBean bean) {

    }

    @Override
    public void onNegativeButtonClicked(ConsumeBean bean, int position) {

    }

    @Override
    public void onPositiveButtonClicked(ConsumeBean bean, int position) {

    }

    @Override
    public void onLoadMoreButtonClicked() {

    }

    @Override
    public boolean isPaged() {
        return true;
    }
}
