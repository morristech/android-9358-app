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
import com.xmd.app.BaseDialogFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.Constants;
import com.xmd.app.net.NetworkSubscriber;
import com.xmd.appointment.beans.ServiceCategory;
import com.xmd.appointment.beans.ServiceData;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.ServiceListResult;
import com.xmd.appointment.databinding.FragmentServiceItemSelectBinding;

import java.util.ArrayList;

/**
 * Created by heyangya on 17-5-24.
 * 技师选择界面
 */

public class ServiceItemSelectFragment extends BaseDialogFragment {
    public static ServiceItemSelectFragment newInstance(String serviceItemId) {
        ServiceItemSelectFragment fragment = new ServiceItemSelectFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_DATA, serviceItemId);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentServiceItemSelectBinding mBinding;
    private CommonRecyclerViewAdapter<ServiceData> mCategoryAdapter;
    private CommonRecyclerViewAdapter<ServiceItem> mItemAdapter;
    private ServiceItem mSelectedItem;
    private String mSelectedItemId;
    private String mEmptyId = "notSure";

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_service_item_select, container, false);

        mCategoryAdapter = new CommonRecyclerViewAdapter<>();
        initRecyclerView(mBinding.categoryRecyclerView, mCategoryAdapter);
        mItemAdapter = new CommonRecyclerViewAdapter<>();
        initRecyclerView(mBinding.itemRecyclerView, mItemAdapter);

        mBinding.setHandler(this);
        return mBinding.getRoot();
    }

    private void initRecyclerView(RecyclerView recyclerView, CommonRecyclerViewAdapter adapter) {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, 1);
            }
        });
        adapter.setHandler(BR.handler, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setTitle("选择技师");
        getDialog().setCancelable(false);
        loading.set(true);
        loadingError.set(null);
        DataManager.getInstance().loadServiceList(new NetworkSubscriber<ServiceListResult>() {
            @Override
            public void onCallbackSuccess(ServiceListResult result) {
                loading.set(false);
                loadingError.set(null);
                //增加到店选择数据
                result.getRespData().add(0, mockEmptyData());
                mCategoryAdapter.setData(R.layout.list_item_service_category, BR.data, result.getRespData());
                mCategoryAdapter.notifyDataSetChanged();
                //设置选中状态
                if (!mSelectedItemId.equals(mEmptyId)) {
                    for (ServiceData data : result.getRespData()) {
                        if (data.itemList == null) {
                            continue;
                        }
                        for (ServiceItem serviceItem : data.itemList) {
                            if (serviceItem.getId().equals(mSelectedItemId)) {
                                mSelectedItem = serviceItem;
                                data.viewSelected.set(true);
                                mItemAdapter.setData(R.layout.list_item_service_item, BR.data, data.itemList);
                                mItemAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
                //没有找到数据，选中到店选择
                if (mSelectedItem == null) {
                    ServiceData data = result.getRespData().get(0);
                    mSelectedItem = data.itemList.get(0);
                    data.viewSelected.set(true);
                    mItemAdapter.setData(R.layout.list_item_service_item, BR.data, data.itemList);
                    mItemAdapter.notifyDataSetChanged();
                }
                mSelectedItem.viewSelected.set(true);
            }

            @Override
            public void onCallbackError(Throwable e) {
                loading.set(false);
                loadingError.set("加载失败：" + e.getLocalizedMessage());
                mCategoryAdapter.setData(R.layout.list_item_technician, BR.data, new ArrayList<ServiceData>());
                mCategoryAdapter.notifyDataSetChanged();
            }
        });

        mSelectedItemId = (String) getArguments().get(Constants.EXTRA_DATA);
        if (mSelectedItemId == null) {
            mSelectedItemId = mEmptyId;
        }
    }

    private ServiceData mockEmptyData() {
        ServiceData mock = new ServiceData();
        mock.categoryBean = new ServiceCategory();
        mock.categoryBean.setId(mEmptyId);
        mock.categoryBean.setName("到店选择");
        mock.itemList = new ArrayList<>();
        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setId(mEmptyId);
        serviceItem.setName("到店选择");
        mock.itemList.add(serviceItem);
        return mock;
    }

    @Override
    public void onResume() {
        super.onResume();
        ScreenUtils.initScreenSize(getActivity().getWindowManager());
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = ScreenUtils.getScreenHeight() * 4 / 5;
            window.setAttributes(lp);
        }
    }

    public void onClickOK() {
        if (mSelectedItem == null || mSelectedItem.getId().equals(mEmptyId)) {
            ((Listener) getActivity()).onCleanServiceItem();
        } else {
            ((Listener) getActivity()).onSelectServiceItem(mSelectedItem);
        }
        DataManager.getInstance().cancelLoadTechnicianList();
        getDialog().dismiss();
    }

    public void onClickCancel() {
        DataManager.getInstance().cancelLoadTechnicianList();
        getDialog().dismiss();
    }

    public void onClickCategory(ServiceCategory category) {

    }

    public void onClickServiceItem(ServiceItem item) {

    }

    public interface Listener {
        void onSelectServiceItem(ServiceItem serviceItem);

        void onCleanServiceItem();
    }
}
