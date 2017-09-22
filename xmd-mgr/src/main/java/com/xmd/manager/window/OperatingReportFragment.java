package com.xmd.manager.window;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.event.OrderCountUpDate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperatingReportFragment extends BaseFragment {

    @BindView(R.id.image_operate_new)
    ImageView imageOperateNew;
    @BindView(R.id.img_order_new)
    ImageView imgOrderNew;

    Unbinder unbinder;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_operating_report, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    protected void initView() {
        view.findViewById(R.id.toolbar_left).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(ResourceUtils.getString(R.string.operate_data_title));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Subscribe
    public void newOrderSubscribe(OrderCountUpDate orderCount){
        if(orderCount.newOrder>0){
            imgOrderNew.setVisibility(View.VISIBLE);
        }else{
            imgOrderNew.setVisibility(View.GONE);
        }
    }

    //运营报表
    @OnClick(R.id.rl_operate)
    public void onRlOperateClicked() {
        //startActivity(new Intent(getActivity(), OperationReportActivity.class));

    }

    //核销记录
    @OnClick(R.id.rl_verification_record)
    public void onRlVerificationRecordClicked() {
        startActivity(new Intent(getActivity(), VerificationRecordListActivity.class));
    }

    //在线预约
    @OnClick(R.id.rl_online_order)
    public void onRlOnlineOrderClicked() {
        startActivity(new Intent(getActivity(), ReserveDataActivity.class));
    }
}
