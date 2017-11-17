package com.xmd.appointment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseDialogFragment;
import com.xmd.app.CommonRecyclerViewAdapter;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.appointment.beans.Technician;
import com.xmd.appointment.beans.TechnicianListResult;
import com.xmd.appointment.databinding.FragmentTechSelectBinding;
import com.xmd.m.network.NetworkSubscriber;

import java.util.ArrayList;


/**
 * Created by heyangya on 17-5-24.
 * 技师选择界面
 */

public class TechSelectFragment extends BaseDialogFragment {
    private static final String EXTRA_SELECTED_TECH_ID = "extra_selected_tech_id";
    private static final String EXTRA_SELECTED_ITEM_ID = "extra_selected_item_id";

    public static TechSelectFragment newInstance(String techId, String itemId) {
        TechSelectFragment fragment = new TechSelectFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_SELECTED_TECH_ID, techId);
        args.putString(EXTRA_SELECTED_ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentTechSelectBinding mBinding;
    private CommonRecyclerViewAdapter<Technician> mAdapter;
    private Technician mSelectedTechnician;
    private String mSelectedTechId;
    private String mSelectedItemId;
    private String mEmptyTechId = "notSure";

    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableField<String> loadingError = new ObservableField<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Listener)) {
            throw new RuntimeException("activity must implement interface Listener!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tech_select, container, false);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, 1);
            }
        });
        mAdapter = new CommonRecyclerViewAdapter<>();
        mAdapter.setHandler(BR.handler, this);
        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.setHandler(this);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setTitle("选择技师");
        getDialog().setCancelable(false);

        mSelectedTechId = (String) getArguments().get(EXTRA_SELECTED_TECH_ID);
        if (mSelectedTechId == null) {
            mSelectedTechId = mEmptyTechId;
        }
        mSelectedItemId = (String) getArguments().get(EXTRA_SELECTED_ITEM_ID);

        loading.set(true);
        loadingError.set(null);
        DataManager.getInstance().loadTechnicianList(mSelectedItemId, new NetworkSubscriber<TechnicianListResult>() {
            @Override
            public void onCallbackSuccess(TechnicianListResult result) {
                loading.set(false);
                loadingError.set(null);
                //增加到店选择数据
                Technician mock = new Technician();
                mock.setId(mEmptyTechId);
                mock.setName("到店选择");
                result.getRespData().add(0, mock);
                mAdapter.setData(R.layout.list_item_technician, com.xmd.appointment.BR.data, result.getRespData());
                mAdapter.notifyDataSetChanged();
                //检查是否需要选中技师
                if (mSelectedTechId != null) {
                    for (Technician technician : result.getRespData()) {
                        if (technician.getId() != null && technician.getId().equals(mSelectedTechId)) {
                            mSelectedTechnician = technician;
                            mSelectedTechnician.viewSelected.set(true);
                            break;
                        }
                    }
                }
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
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ScreenUtils.getScreenWidth() * 5 / 6;
            lp.height = ScreenUtils.getScreenHeight() * 3 / 5;
            window.setAttributes(lp);
        }
    }

    public void onClickOK() {
        if (mSelectedTechnician == null || mSelectedTechnician.getId().equals(mEmptyTechId)) {
            ((Listener) getActivity()).onCleanTechnician();
        } else {
            if(mSelectedTechnician.getStatus().equals("rest")){
                XToast.show(ResourceUtils.getString(R.string.tech_rest_status_no_work_alter_message));
                return;
            }
            ((Listener) getActivity()).onSelectTechnician(mSelectedTechnician);
        }
        DataManager.getInstance().cancelLoadTechnicianList();
        getDialog().dismiss();
    }

    public void onClickCancel() {
        DataManager.getInstance().cancelLoadTechnicianList();
        getDialog().dismiss();
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

    public interface Listener {
        void onSelectTechnician(Technician technician);

        void onCleanTechnician();
    }
}
