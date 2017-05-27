package com.xmd.appointment;


import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.app.BaseDialogFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.Constants;
import com.xmd.app.net.NetworkSubscriber;
import com.xmd.appointment.beans.Technician;
import com.xmd.appointment.beans.TechnicianListResult;
import com.xmd.appointment.databinding.FragmentTechSelectBinding;

import java.util.ArrayList;

/**
 * Created by heyangya on 17-5-24.
 * 技师选择界面
 */

public class TechSelectFragment extends BaseDialogFragment {
    public static TechSelectFragment newInstance(String techId) {
        TechSelectFragment fragment = new TechSelectFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_DATA, techId);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentTechSelectBinding mBinding;
    private CommonRecyclerViewAdapter<Technician> mAdapter;
    private Technician mSelectedTechnician;


    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableField<String> loadingError = new ObservableField<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tech_select, container, false);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CommonRecyclerViewAdapter<>();
        mAdapter.setHandler(BR.handler, this);

        mBinding.recyclerView.setAdapter(mAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setTitle("选择技师");
        loading.set(true);
        loadingError.set(null);
        DataManager.getInstance().getTechnicianList(new NetworkSubscriber<TechnicianListResult>() {
            @Override
            public void onCallbackSuccess(TechnicianListResult result) {
                loading.set(false);
                loadingError.set(null);
                mAdapter.setData(R.layout.list_item_technician, BR.data, result.getRespData());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCallbackError(Throwable e) {
                loading.set(false);
                loadingError.set("加载失败：" + e.getLocalizedMessage());
                mAdapter.setData(R.layout.list_item_technician, BR.data, new ArrayList<Technician>());
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        ScreenUtils.initScreenSize(getActivity().getWindowManager());
        window.setLayout(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight() * 4 / 5);
    }

    public void onClickOK() {

    }

    public void onClickClean() {

    }

    public void onClickCancel() {

    }

    public void onClickTechnician(Technician technician) {
        if (mSelectedTechnician != null) {
            if (mSelectedTechnician.getId().equals(technician.getId())) {
                return;
            }
            mSelectedTechnician.viewSelected.set(false);
        }
        mSelectedTechnician = technician;
        mSelectedTechnician.viewSelected.set(true);
    }
}
