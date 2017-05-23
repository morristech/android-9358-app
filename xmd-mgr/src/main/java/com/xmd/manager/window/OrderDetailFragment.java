package com.xmd.manager.window;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.OrderDetailResult;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 16-5-26.
 */
public class OrderDetailFragment extends BaseFragment {

    @Bind(R.id.untreatedOrders)
    TextView unTreatedOrders;
    @Bind(R.id.completedOrders)
    TextView completedOrders;
    @Bind(R.id.nullityOrders)
    TextView nullityOrders;
    @Bind(R.id.overtimeOrders)
    TextView overtimeOrders;
    @Bind(R.id.refusedOrders)
    TextView refusedOrders;
    @Bind(R.id.acceptOrders)
    TextView acceptOrders;
    @Bind(R.id.un_accept_total)
    TextView unAcceptTotal;
    @Bind(R.id.had_accept_total)
    TextView hadAcceptTotal;

    public static final String BIZ_TYPE = "type";
    private Map<String, String> mParams;
    private Map<String, String> mSwitchParams;
    private String mStartTime, mEndTime;
    private TextView tvStart, tvEnd;
    private int mRange;
    private boolean isVisible;

    private Subscription mGetOrderDetailDataSubscription;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_detail, container, false);
        ButterKnife.bind(this, view);
        mParams = new HashMap<>();
        mSwitchParams = new HashMap<>();
        tvStart = (TextView) getActivity().findViewById(R.id.startTime);
        tvEnd = (TextView) getActivity().findViewById(R.id.endTime);
        isVisible = true;
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetOrderDetailDataSubscription);
        ButterKnife.unbind(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && isVisible) {
            tvStart = (TextView) getActivity().findViewById(R.id.startTime);
            tvEnd = (TextView) getActivity().findViewById(R.id.endTime);
            mStartTime = tvStart.getText().toString();
            mEndTime = tvEnd.getText().toString();
            getData();
        }

    }

    private void getData() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TYPE, String.valueOf(mRange));
        params.put(RequestConstant.KEY_START_DATE, mStartTime);
        params.put(RequestConstant.KEY_END_DATE, mEndTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ORDER_DETAIL, params);
    }

    @Override
    protected void initView() {
        mRange = mArguments.getInt(BIZ_TYPE);
        mGetOrderDetailDataSubscription = RxBus.getInstance().toObservable(OrderDetailResult.class).subscribe(result -> {
            if (result.statusCode != RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
                unTreatedOrders.setText(String.valueOf(result.respData.submitCount));
                completedOrders.setText(String.valueOf(result.respData.completeCount));
                nullityOrders.setText(String.valueOf(result.respData.failureCount));
                overtimeOrders.setText(String.valueOf(result.respData.overtimeCount));
                refusedOrders.setText(String.valueOf(result.respData.rejectCount));
                acceptOrders.setText(String.valueOf(result.respData.acceptCount));
                unAcceptTotal.setText(String.valueOf(result.respData.submitCount + result.respData.rejectCount + result.respData.overtimeCount));
                hadAcceptTotal.setText(String.valueOf(result.respData.acceptCount + result.respData.completeCount + result.respData.failureCount));
                mStartTime = result.starTime;
                mEndTime = result.endTime;
            }
        });

    }


    @OnClick({R.id.order_submit, R.id.order_refused, R.id.order_over_time, R.id.order_accept, R.id.order_complete, R.id.order_failure})
    public void onClick(View view) {
        mParams.clear();
        switch (view.getId()) {
            case R.id.order_submit:
                mParams.put(Constant.ORDER_STATUS_TYPE, ResourceUtils.getString(R.string.untreated_orders));
                break;
            case R.id.order_refused:
                mParams.put(Constant.ORDER_STATUS_TYPE, ResourceUtils.getString(R.string.refused_orders));
                break;
            case R.id.order_over_time:
                mParams.put(Constant.ORDER_STATUS_TYPE, ResourceUtils.getString(R.string.overtime_orders));
                break;
            case R.id.order_accept:
                mParams.put(Constant.ORDER_STATUS_TYPE, ResourceUtils.getString(R.string.accept_orders));
                break;
            case R.id.order_complete:
                mParams.put(Constant.ORDER_STATUS_TYPE, ResourceUtils.getString(R.string.completed_orders));
                break;
            case R.id.order_failure:
                mParams.put(Constant.ORDER_STATUS_TYPE, ResourceUtils.getString(R.string.nullity_orders));
                break;
        }

        mParams.put(Constant.ORDER_START_TIME, mStartTime);
        mParams.put(Constant.ORDER_END_TIME, mEndTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_FILTER_ORDER_LIST, mParams);
        mSwitchParams.put(Constant.SWITCH_INDEX, "3");
        mSwitchParams.put(Constant.PARAM_RANGE, String.valueOf(mRange));
        mSwitchParams.put(Constant.ORDER_START_TIME, mStartTime);
        mSwitchParams.put(Constant.ORDER_END_TIME, mEndTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SWITCH_INDEX, mSwitchParams);
        getActivity().finish();
    }
}
