package com.xmd.manager.journal.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import com.xmd.manager.R;
import com.xmd.manager.common.ScreenUtils;
import com.xmd.manager.journal.BaseActivity;
import com.xmd.manager.journal.UINavigation;
import com.xmd.manager.journal.adapter.JournalContentEditAdapter;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.contract.JournalTemplateChoiceContract;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.presenter.JournalContentEditPresenter;

import java.util.List;

public class JournalContentEditActivity extends BaseActivity implements JournalContentEditContract.View {
    private JournalContentEditContract.Presenter mPresenter;
    private JournalContentEditAdapter mAdapter;
    private RecyclerView recyclerView;
    private String mTemplateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_content_edit);

        mPresenter = new JournalContentEditPresenter(this, this);

        setTitle(getString(R.string.journal_title_edit_content));
        mTemplateId = getIntent().getStringExtra(UINavigation.EXTRA_INT_TEMPLATE_ID);
        ScreenUtils.initScreenSize(getWindowManager());
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildViewHolder(view).getItemViewType() == JournalContentEditAdapter.ITEM_VIEW_TYPE_NORMAL) {
                    outRect.set(0, ScreenUtils.dpToPx(8), 0, ScreenUtils.dpToPx(8));
                } else if (parent.getChildViewHolder(view).getItemViewType() == JournalContentEditAdapter.ITEM_VIEW_TYPE_HEADER) {
                    outRect.set(0, 0, 0, ScreenUtils.dpToPx(8));
                }
            }
        });
        mAdapter = new JournalContentEditAdapter(mPresenter, mTemplateId);
        recyclerView.setAdapter(mAdapter);
        setLeftVisible(true, R.drawable.actionbar_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onBackKey();
            }
        });
        setRightVisible(true, "清空", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClearContent();
            }
        });

        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void showJournal(Journal journal, List<JournalContent> deletedContents) {
        mAdapter.setData(journal, deletedContents);
        mAdapter.notifyDataSetChanged();
    }

//    private Handler mHandler = new Handler();

    @Override
    public void updateJournalContentView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateJournalContentSubview(int viewPosition) {
//        //process Cannot call this method while RecyclerView is computing a layout or scrolling
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!recyclerView.isComputingLayout()) {
//                    mAdapter.notifyItemChanged(viewPosition);
//                } else {
//                    updateJournalContentSubview(viewPosition);
//                }
//            }
//        }, 1000);
    }

    @Override
    public int getJournalIdFromIntent() {
        return getIntent().getIntExtra(UINavigation.EXTRA_INT_JOURNAL_ID, -1);
    }

    @Override
    public void setPresenter(JournalTemplateChoiceContract.Presenter presenter) {

    }

    public void onClickPreview(View view) {
        mPresenter.onClickPreView();
    }

    public void onClickSave(View view) {
        mPresenter.onClickSaveDraft();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mPresenter.onBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
