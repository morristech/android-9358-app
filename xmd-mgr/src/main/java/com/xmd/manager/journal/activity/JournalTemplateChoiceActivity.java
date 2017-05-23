package com.xmd.manager.journal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.xmd.manager.R;
import com.xmd.manager.journal.BaseActivity;
import com.xmd.manager.journal.UINavigation;
import com.xmd.manager.journal.adapter.JournalTemplateAdapter;
import com.xmd.manager.journal.adapter.JournalTemplateItemAdapter;
import com.xmd.manager.journal.contract.JournalTemplateChoiceContract;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalContentType;
import com.xmd.manager.journal.model.JournalTemplate;
import com.xmd.manager.journal.presenter.JournalTemplateChoicePresenter;
import com.xmd.manager.widget.CombineLoadingView;
import com.xmd.manager.widget.CustomRecycleViewDecoration;

import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;
import app.dinus.com.loadingdrawable.render.LoadingRenderer;
import app.dinus.com.loadingdrawable.render.LoadingRendererFactory;

public class JournalTemplateChoiceActivity extends BaseActivity implements JournalTemplateChoiceContract.View {
    private JournalTemplateChoiceContract.Presenter mPresenter;
    private JournalTemplateAdapter mJournalTemplateAdapter;
    private JournalTemplateItemAdapter mJournalTemplateItemAdapter;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_tempate_choice);

        setTitle(getString(R.string.journal_title_choice_template));

        mPresenter = new JournalTemplateChoicePresenter(this, this);

        RecyclerView templateRecyclerView = (RecyclerView) findViewById(R.id.recycleview_template);
        templateRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mJournalTemplateAdapter = new JournalTemplateAdapter(mPresenter);
        templateRecyclerView.addItemDecoration(new CustomRecycleViewDecoration(32, CustomRecycleViewDecoration.ORIENTATION_HORIZONTAL));
        templateRecyclerView.setAdapter(mJournalTemplateAdapter);

        RecyclerView templateItemRecyclerView = (RecyclerView) findViewById(R.id.recycleview_template_content);
        templateItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mJournalTemplateItemAdapter = new JournalTemplateItemAdapter(mPresenter);
        templateItemRecyclerView.setAdapter(mJournalTemplateItemAdapter);
        mNextButton = (Button) findViewById(R.id.btn_next);

        setLeftVisible(true, R.drawable.actionbar_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onKeyBack();
            }
        });


        try {
            LoadingRenderer loadingRenderer = LoadingRendererFactory.createLoadingRenderer(this, 13);
            LoadingView loadingView = new LoadingView(this);
            loadingView.setLoadingRenderer(loadingRenderer);
            mCombineLoadingView.init(loadingView, R.drawable.image_journal_no_data, "暂无模板", R.drawable.image_journal_error, "加载失败，点击重试");
            mCombineLoadingView.setOnClickErrorViewListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.reloadData();
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
    public void showLoadingTemplates() {
        mCombineLoadingView.setStatus(CombineLoadingView.STATUS_LOADING);
    }

    @Override
    public void showTemplates(List<JournalTemplate> templates, Journal journal, List<JournalContentType> unCancelContentTypes) {
        if (templates.size() > 0) {
            mCombineLoadingView.setStatus(CombineLoadingView.STATUS_SUCCESS);
            mJournalTemplateAdapter.setData(templates, journal);
            mJournalTemplateAdapter.notifyDataSetChanged();
            mJournalTemplateItemAdapter.setData(journal, unCancelContentTypes);
            mJournalTemplateItemAdapter.notifyDataSetChanged();
        } else {
            mCombineLoadingView.setStatus(CombineLoadingView.STATUS_EMPTY);
        }
    }

    @Override
    public void showLoadingTemplatesError() {
        mCombineLoadingView.setStatus(CombineLoadingView.STATUS_ERROR);
    }

    @Override
    public void showSelectedTemplate(Journal journal) {
        mJournalTemplateAdapter.notifyDataSetChanged();
        mJournalTemplateItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void setNextButtonEnable(boolean enable) {
        mNextButton.setEnabled(enable);
    }

    @Override
    public int getJournalIdFromIntent() {
        return getIntent().getIntExtra(UINavigation.EXTRA_INT_JOURNAL_ID, -1);
    }

    @Override
    public void setPresenter(JournalTemplateChoiceContract.Presenter presenter) {

    }

    public void onClickNext(View view) {
        mPresenter.onEditContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mPresenter.onKeyBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
