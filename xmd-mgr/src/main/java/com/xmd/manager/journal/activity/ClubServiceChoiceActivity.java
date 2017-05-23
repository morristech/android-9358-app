package com.xmd.manager.journal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.xmd.manager.R;
import com.xmd.manager.beans.ServiceItem;
import com.xmd.manager.beans.ServiceItemInfo;
import com.xmd.manager.journal.BaseActivity;
import com.xmd.manager.journal.UINavigation;
import com.xmd.manager.journal.adapter.ClubServiceAdapter;
import com.xmd.manager.journal.contract.ClubServiceChoiceContract;
import com.xmd.manager.journal.presenter.ClubServiceChoicePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2016/11/10.
 */
public class ClubServiceChoiceActivity extends BaseActivity implements ClubServiceChoiceContract.View {

    @Bind(R.id.list_service_view)
    RecyclerView listServiceView;
    @Bind(R.id.btn_service_cancel)
    Button btnServiceCancel;
    @Bind(R.id.btn_service_save)
    Button btnServiceSave;

    private ClubServiceChoiceContract.Presenter mPresenter;
    private ClubServiceAdapter mServiceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_choice);
        ButterKnife.bind(this);
        setTitle(getString(R.string.journal_title_edit_project));
        mPresenter = new ClubServiceChoicePresenter(this, this);

        mServiceAdapter = new ClubServiceAdapter(mPresenter);
        listServiceView.setHasFixedSize(true);
        listServiceView.setLayoutManager(new LinearLayoutManager(this));
        listServiceView.setAdapter(mServiceAdapter);

        mPresenter.onCreate();
    }

    @OnClick(R.id.btn_service_save)
    public void saveSelected() {
        mPresenter.onClickConfirmButton();
    }

    @OnClick(R.id.btn_service_cancel)
    public void cancelSelected() {
        mPresenter.onClickCancelButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }


    @Override
    public void setPresenter(ClubServiceChoiceContract.Presenter presenter) {

    }

    @Override
    public void showServiceList(List<ServiceItemInfo> serviceList) {
        mServiceAdapter.setData(serviceList);
        mServiceAdapter.notifyDataSetChanged();
    }

    @Override
    public void setConfirmButtonEnable(boolean enable) {
        btnServiceSave.setEnabled(enable);
    }

    @Override
    public ArrayList<ServiceItem> getSelectedServiceItem() {
        return getIntent().getParcelableArrayListExtra(UINavigation.EXTRA_PARCELABLE_SELECTED_SERVICE_ITEMS);
    }

    @Override
    public int getMaxSelectSize() {
        return getIntent().getIntExtra(UINavigation.EXTRA_INT_MAX_SIZE, 1);
    }
}

