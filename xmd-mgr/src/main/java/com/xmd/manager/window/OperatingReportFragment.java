package com.xmd.manager.window;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.event.OrderCountUpDate;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.ClubSwitchResult;
import com.xmd.manager.service.response.ReportNewsResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperatingReportFragment extends BaseFragment {
    @BindView(R.id.rl_cashier)
    RelativeLayout mCashierLayout;
    @BindView(R.id.rl_salary)
    RelativeLayout mSalaryLayout;

    @BindView(R.id.image_operate_new)
    ImageView imageOperateNew;
    @BindView(R.id.img_order_new)
    ImageView imgOrderNew;

    Unbinder unbinder;
    View view;
    public Subscription mReportNewSubscribe;
    public Subscription mGetClubNativeSwitchSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_operating_report, container, false);
        unbinder = ButterKnife.bind(this, view);
        mGetClubNativeSwitchSubscription = RxBus.getInstance().toObservable(ClubSwitchResult.class).subscribe(
                result -> handleClubNativeSwitch(result)
        );
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //   MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_REPORT_NEWS);

    }

    @Override
    protected void initView() {
        view.findViewById(R.id.toolbar_left).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(ResourceUtils.getString(R.string.operate_data_title));
        mReportNewSubscribe = RxBus.getInstance().toObservable(ReportNewsResult.class).subscribe(
                reportNewsResult -> {
                    handlerReportNewsResult(reportNewsResult);
                }
        );

        mCashierLayout.setVisibility(SharedPreferenceHelper.getClubNativeSwitch() ? View.VISIBLE : View.GONE);
        mSalaryLayout.setVisibility(SharedPreferenceHelper.getClubNativeSwitch() ? View.VISIBLE : View.GONE);
    }

    private void handleClubNativeSwitch(ClubSwitchResult result) {
        // 内网开关控制
        if (result.respData != null) {
            boolean sw = Constant.REQUEST_YES.equals(result.respData.status);
            mCashierLayout.setVisibility(sw ? View.VISIBLE : View.GONE);
            mSalaryLayout.setVisibility(sw ? View.VISIBLE : View.GONE);
        }
    }

    private void handlerReportNewsResult(ReportNewsResult reportNewsResult) {
        if (reportNewsResult.statusCode == 200) {
            imageOperateNew.setVisibility(reportNewsResult.respData.count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        RxBus.getInstance().unsubscribe(mGetClubNativeSwitchSubscription);
    }

    @Subscribe
    public void newOrderSubscribe(OrderCountUpDate orderCount) {
        if (orderCount.newOrder > 0) {
            imgOrderNew.setVisibility(View.VISIBLE);
        } else {
            imgOrderNew.setVisibility(View.GONE);
        }
    }

    //运营报表
    @OnClick(R.id.rl_operate)
    public void onRlOperateClicked() {
        startActivity(new Intent(getActivity(), OperationReportActivity.class));
    }

    // 买单收银报表
    @OnClick(R.id.rl_cashier)
    public void onRlCashierClicked() {
        startActivity(new Intent(getActivity(), CashierReportActivity.class));
    }

    // 技师工资报表
    @OnClick(R.id.rl_salary)
    public void onRlSalaryClicked() {
        startActivity(new Intent(getActivity(), SalaryReportActivity.class));
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

    //优惠券
    @OnClick(R.id.rl_coupon_data)
    public void onRlCouponDataClicked() {
        startActivity(new Intent(getActivity(), CouponOperateDataActivity.class));
    }
}
