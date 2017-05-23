package com.xmd.manager.journal.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xmd.manager.R;
import com.xmd.manager.journal.BaseActivity;
import com.xmd.manager.journal.adapter.JournalListAdapter;
import com.xmd.manager.journal.contract.JournalListContract;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.presenter.JournalListPresenter;
import com.xmd.manager.widget.CombineLoadingView;
import com.xmd.manager.widget.CustomRecycleViewDecoration;

import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;
import app.dinus.com.loadingdrawable.render.LoadingRenderer;
import app.dinus.com.loadingdrawable.render.LoadingRendererFactory;

public class JournalListActivity extends BaseActivity implements JournalListContract.View {
    private JournalListContract.Presenter mPresenter;
    private RecyclerView mRecyclerView;
    private JournalListAdapter mJournalListAdapter;
    private SwipeRefreshLayout mPullRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        setTitle(getString(R.string.journal_title_list));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);

        mPresenter = new JournalListPresenter(this, this);
        mJournalListAdapter = new JournalListAdapter(mPresenter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new CustomRecycleViewDecoration(32));
        mRecyclerView.setAdapter(mJournalListAdapter);

        setRightVisible(true, "新建", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.newJournal();
            }
        });

        mPullRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pull_refresh_layout);
        mPullRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshJournal();
            }
        });

        try {
            LoadingRenderer loadingRenderer = LoadingRendererFactory.createLoadingRenderer(this, 13);
            LoadingView loadingView = new LoadingView(this);
            loadingView.setLoadingRenderer(loadingRenderer);
            mCombineLoadingView.init(loadingView, R.drawable.image_journal_no_data, "暂无期刊", R.drawable.image_journal_error, "加载失败，点击重试");
            mCombineLoadingView.setOnClickErrorViewListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.refreshJournal();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void showLoadingJournalList() {
        mCombineLoadingView.setStatus(CombineLoadingView.STATUS_LOADING);
    }

    @Override
    public void showJournalList(List<Journal> journals) {
        mPullRefreshLayout.setRefreshing(false);
        if (journals.size() > 0) {
            mCombineLoadingView.setStatus(CombineLoadingView.STATUS_SUCCESS);
            mJournalListAdapter.setData(journals);
            mJournalListAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
        } else {
            mCombineLoadingView.setStatus(CombineLoadingView.STATUS_EMPTY);
        }
    }

    @Override
    public void showLoadJournalListError(String error) {
        mPullRefreshLayout.setRefreshing(false);
        mCombineLoadingView.setStatus(CombineLoadingView.STATUS_ERROR);
    }

    @Override
    public void updateJournalList() {
        mJournalListAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateJournalListItem(int index) {
        mJournalListAdapter.notifyItemChanged(index);
    }

    @Override
    public void setPresenter(JournalListContract.Presenter presenter) {

    }


}
