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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.app.BaseDialogFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.net.NetworkSubscriber;
import com.xmd.appointment.beans.ServiceCategory;
import com.xmd.appointment.beans.ServiceData;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.ServiceListResult;
import com.xmd.appointment.databinding.FragmentServiceItemSelectBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by heyangya on 17-5-24.
 * 技师选择界面
 */

public class ServiceItemSelectFragment extends BaseDialogFragment {
    private final static String EXTRA_SELECTED_ID = "extra_selected_id";
    private final static String EXTRA_LIMIT_IDS = "extra_show_ids";

    public static ServiceItemSelectFragment newInstance(String serviceItemId, ArrayList<String> limitItemIdList) {
        ServiceItemSelectFragment fragment = new ServiceItemSelectFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_SELECTED_ID, serviceItemId);
        args.putStringArrayList(EXTRA_LIMIT_IDS, limitItemIdList);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentServiceItemSelectBinding mBinding;
    private CommonRecyclerViewAdapter<ServiceData> mCategoryAdapter;
    private CommonRecyclerViewAdapter<ServiceItem> mItemAdapter;
    private ServiceData mSelectedServiceData;
    private ServiceItem mSelectedItem;
    private String mArgumentItemId;
    private ArrayList<String> mArgumentItemIdList;
    private ServiceData mEmptyData;
    private String mEmptyId = "notSure";

    public ObservableField<String> selectedItemName = new ObservableField<>();
    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableField<String> loadingError = new ObservableField<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Listener)) {
            throw new RuntimeException("activity must implement interface Listener!");
        }
        ScreenUtils.initScreenSize(getActivity().getWindowManager());
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
                outRect.set(0, 0, 0, ScreenUtils.dpToPx(1));
            }
        });
        adapter.setHandler(BR.handler, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setTitle("选择技师");
        mEmptyData = mockEmptyData();
        mArgumentItemId = (String) getArguments().get(EXTRA_SELECTED_ID);
        if (mArgumentItemId == null) {
            mArgumentItemId = mEmptyId;
        }
        mArgumentItemIdList = (ArrayList<String>) getArguments().get(EXTRA_LIMIT_IDS);

        loading.set(true);
        loadingError.set(null);
        DataManager.getInstance().loadServiceList(new NetworkSubscriber<ServiceListResult>() {
            @Override
            public void onCallbackSuccess(ServiceListResult result) {
                loading.set(false);
                loadingError.set(null);
                //过滤数据
                filterData(result.getRespData());
                //增加到店选择数据
                result.getRespData().add(0, mEmptyData);
                mCategoryAdapter.setData(R.layout.list_item_service_category, BR.data, result.getRespData());
                mCategoryAdapter.notifyDataSetChanged();
                //设置选中状态
                if (!mArgumentItemId.equals(mEmptyId)) {
                    for (ServiceData data : result.getRespData()) {
                        for (ServiceItem serviceItem : data.itemList) {
                            if (serviceItem.getId().equals(mArgumentItemId)) {
                                mSelectedServiceData = data;
                                mSelectedItem = serviceItem;
                                break;
                            }
                        }
                    }
                }

                if (mSelectedServiceData == null) {
                    mSelectedServiceData = mEmptyData;
                    mSelectedItem = mEmptyData.itemList.get(0);
                }

                //更新项目列表
                mSelectedServiceData.viewSelected.set(true);
                mSelectedItem.viewSelected.set(true);
                mItemAdapter.setData(R.layout.list_item_service_item, BR.data, mSelectedServiceData.itemList);
                mItemAdapter.notifyDataSetChanged();
                selectedItemName.set(mSelectedItem.getName());
            }

            @Override
            public void onCallbackError(Throwable e) {
                loading.set(false);
                loadingError.set("加载失败：" + e.getLocalizedMessage());
                mCategoryAdapter.setData(R.layout.list_item_technician, BR.data, new ArrayList<ServiceData>());
                mCategoryAdapter.notifyDataSetChanged();
            }
        });
    }

    private void filterData(List<ServiceData> dataList) {
        //只显示需要显示的数据
        if (mArgumentItemIdList != null) {
            for (ServiceData data : dataList) {
                List<ServiceItem> items = data.itemList;
                if (items == null || items.size() == 0) {
                    continue;
                }
                Iterator<ServiceItem> itemIterator = items.iterator();
                while (itemIterator.hasNext()) {
                    ServiceItem item = itemIterator.next();
                    if (!mArgumentItemIdList.contains(item.getId())) {
                        itemIterator.remove();
                    }
                }
            }
        }

        //移除没有项目的分类
        Iterator<ServiceData> dataIterator = dataList.iterator();
        while (dataIterator.hasNext()) {
            List<ServiceItem> items = dataIterator.next().itemList;
            if (items == null || items.size() == 0) {
                dataIterator.remove();
            }
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
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ScreenUtils.getScreenWidth();
            lp.height = ScreenUtils.getScreenHeight() * 3 / 5;
            window.setAttributes(lp);
            window.setGravity(Gravity.BOTTOM);
        }
    }

    public void onClickOK() {
        if (mSelectedItem == null) {
            onClickCancel();
            return;
        }
        if (mSelectedItem.getId().equals(mEmptyId)) {
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

    public void onClickCategory(ServiceData serviceData) {
        mSelectedServiceData.viewSelected.set(false);
        mSelectedServiceData = serviceData;
        mSelectedServiceData.viewSelected.set(true);
        mItemAdapter.setData(R.layout.list_item_service_item, BR.data, serviceData.itemList);
        mItemAdapter.notifyDataSetChanged();
    }

    public void onClickServiceItem(ServiceItem item) {
        mSelectedItem.viewSelected.set(false);
        mSelectedItem = item;
        mSelectedItem.viewSelected.set(true);
        selectedItemName.set(mSelectedItem.getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataManager.getInstance().cancelLoadServiceList();
    }

    public interface Listener {
        void onSelectServiceItem(ServiceItem serviceItem);

        void onCleanServiceItem();
    }
}
