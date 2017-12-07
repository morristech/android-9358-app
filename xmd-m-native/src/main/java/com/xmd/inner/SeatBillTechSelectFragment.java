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
import com.xmd.inner.adapter.ServiceSelectAdapter;
import com.xmd.inner.bean.NativeTechnician;
import com.xmd.inner.event.EmployeeChangedEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-12-4.
 */

public class SeatBillTechSelectFragment extends BaseDialogFragment {

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
    private NativeTechnician mTechnician;
    private int billItemPosition;
    private int parentPosition;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat_bill_service_item_select, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        fragmentTitle.setText("选择营销人员");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ServiceSelectAdapter();
        recyclerView.setAdapter(mAdapter);
        mDataManager = SeatBillDataManager.getManagerInstance();
        mAdapter.setData(mDataManager.getTechList());
        mAdapter.setItemSelectedListener(new ServiceSelectAdapter.ItemSelectedListener() {
            @Override
            public void itemSelected(Object data, int position) {
                mTechnician = (NativeTechnician) data;
            }
        });
    }

    public void setTechData(int parent, int position) {
        billItemPosition = position;
        parentPosition = parent;
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
        if(mTechnician == null){
            this.dismiss();
            return;
        }
        String techNo = mTechnician.techNo;
        String techName = mTechnician.name;
        String techId = mTechnician.id;
        EventBus.getDefault().post(new EmployeeChangedEvent(techId,techNo,techName,parentPosition,billItemPosition));
        this.dismiss();
    }
}
