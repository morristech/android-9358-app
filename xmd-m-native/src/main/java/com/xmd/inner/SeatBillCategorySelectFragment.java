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
import com.xmd.inner.bean.NativeCategoryBean;
import com.xmd.inner.event.CategoryChangedEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-12-4.
 */

public class SeatBillCategorySelectFragment extends BaseDialogFragment {

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
    private SeatBillDataManager mDataManager;
    private int billItemPosition;
    private NativeCategoryBean selectedBean;
    private int selectedPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat_bill_service_item_select, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        fragmentTitle.setText(ResourceUtils.getString(R.string.consume_item_choice));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ServiceSelectAdapter();
        recyclerView.setAdapter(mAdapter);
        mDataManager = SeatBillDataManager.getManagerInstance();
        mAdapter.setData(mDataManager.getCategoryList());
        mAdapter.setItemSelectedListener(new ServiceSelectAdapter.ItemSelectedListener() {
            @Override
            public void itemSelected(Object data, int position) {
                selectedBean = (NativeCategoryBean) data;
                selectedPosition = position;
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

    public void setCategoryData(int position) {
        billItemPosition = position;

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
        String categoryName = "";
        String categoryType = "";
        if (selectedBean != null) {
            categoryName = selectedBean.name;
            categoryType = selectedBean.scope;
        } else {
            this.dismiss();
            return;
        }

        EventBus.getDefault().post(new CategoryChangedEvent(categoryName, categoryType, billItemPosition, selectedPosition));
        mDataManager.clearCateGorySelected();
        mAdapter.setData(mDataManager.getCategoryList());
        this.dismiss();
    }
}
