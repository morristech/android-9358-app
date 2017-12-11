package com.xmd.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.app.BaseDialogFragment;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.inner.adapter.ServiceSelectAdapter;
import com.xmd.inner.bean.NativeServiceItemBean;
import com.xmd.inner.event.ServiceItemChangedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-12-4.
 */

public class SeatBillServiceSelectFragment extends BaseDialogFragment {

    @BindView(R2.id.fragment_title)
    TextView fragmentTitle;
    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.tv_dialog_cancel)
    TextView tvDialogCancel;
    @BindView(R2.id.dialog_make_sure)
    TextView dialogMakeSure;
    Unbinder unbinder;

    private ServiceSelectAdapter mAdapter;
    private int billItemPosition;
    private NativeServiceItemBean mServiceSelectedBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat_bill_service_item_select, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        fragmentTitle.setText(ResourceUtils.getString(R.string.service_service_and_goods_item_choice));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ServiceSelectAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setItemSelectedListener(new ServiceSelectAdapter.ItemSelectedListener() {
            @Override
            public void itemSelected(Object data, int position) {
                mServiceSelectedBean = (NativeServiceItemBean) data;
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

    public void setServiceItemData(int position, List<NativeServiceItemBean> serviceItems) {
        if (mAdapter == null) {
            return;
        }
        billItemPosition = position;
        for (NativeServiceItemBean bean : serviceItems){
            bean.isSelected =false;
        }
        mAdapter.setData(serviceItems);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.tv_dialog_cancel)
    public void onTvDialogCancelClicked() {
        this.dismiss();
    }

    @OnClick(R2.id.dialog_make_sure)
    public void onDialogMakeSureClicked() {
        if (mServiceSelectedBean == null) {
            this.dismiss();
            return;
        }
        ServiceItemChangedEvent serviceEvent = new ServiceItemChangedEvent(mServiceSelectedBean.id, mServiceSelectedBean.name, billItemPosition);
        EventBus.getDefault().post(serviceEvent);
        this.dismiss();
    }
}
