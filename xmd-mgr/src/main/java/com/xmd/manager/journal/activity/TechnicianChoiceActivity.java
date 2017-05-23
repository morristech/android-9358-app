package com.xmd.manager.journal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xmd.manager.R;
import com.xmd.manager.journal.BaseActivity;
import com.xmd.manager.journal.UINavigation;
import com.xmd.manager.journal.adapter.TechnicianChoiceAdapter;
import com.xmd.manager.journal.contract.TechnicianChoiceContract;
import com.xmd.manager.journal.model.Technician;
import com.xmd.manager.journal.presenter.TechnicianChoicePresenter;
import com.xmd.manager.widget.CustomRecycleViewDecoration;

import java.util.List;

public class TechnicianChoiceActivity extends BaseActivity implements TechnicianChoiceContract.View {
    private TechnicianChoiceContract.Presenter mPresenter;
    private TechnicianChoiceAdapter mAdapter;
    private int mFirstItemPosition = -1;
    private int mLastItemPosition = -1;
    private Button mOkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_choice);

        mPresenter = new TechnicianChoicePresenter(this, this);

        setTitle(getString(R.string.journal_title_technician_choice));
        mOkButton = (Button) findViewById(R.id.btn_ok);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new CustomRecycleViewDecoration(2));
        mAdapter = new TechnicianChoiceAdapter(mPresenter);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                mFirstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                mLastItemPosition = linearLayoutManager.findLastVisibleItemPosition();
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
    public void showTechnicianList(List<Technician> technicianList, List<String> forbiddenTechNoList) {
        mAdapter.setData(technicianList, forbiddenTechNoList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showUnChecked(int viewPosition, ImageView mSelectedView) {
        if (mFirstItemPosition == -1 || (viewPosition >= mFirstItemPosition && viewPosition <= mLastItemPosition)) {
            mSelectedView.setImageResource(R.drawable.icon_checkbox);
        }
    }

    @Override
    public void showLoadTechnicianListFailed(String error) {

    }

    @Override
    public List<String> getForbiddenTechNoListFromIntent() {
        return getIntent().getStringArrayListExtra(UINavigation.EXTRA_STRING_LIST_FORBIDDEN_TECHNICIAN_NO);
    }

    @Override
    public void setOkButtonEnable(boolean enable) {
        mOkButton.setEnabled(enable);
    }

    @Override
    public void setPresenter(TechnicianChoiceContract.Presenter presenter) {

    }

    public void onClickOk(View view) {
        Intent intent = getIntent();
        intent.putExtra(UINavigation.EXTRA_STRING_TECHNICIAN_ID, mPresenter.getSelectedTechId());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
