package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.PayOrderDetailBean;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.VerificationManagementHelper;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/6.
 */

public class OrderVerificationFragment extends BaseFragment {

    @Bind(R.id.verification_order_customer_name)
    TextView verificationOrderCustomerName;
    @Bind(R.id.verification_order_start_time)
    TextView verificationOrderStartTime;
    @Bind(R.id.verification_order_time)
    TextView verificationOrderTime;
    @Bind(R.id.verification_order_tech)
    TextView verificationOrderTech;
    @Bind(R.id.verification_order_pay)
    TextView verificationOrderPay;
    @Bind(R.id.verification_order_state)
    TextView verificationOrderState;
    @Bind(R.id.btn_order_verification)
    Button btnOrderVerification;
    @Bind(R.id.btn_order_over_time)
    Button btnOrderOverTime;
    @Bind(R.id.btn_order_cancel)
    Button btnOrderCancel;

    private PayOrderDetailBean mOrder;
    private String mOrderNo;

    public static OrderVerificationFragment getInstance(PayOrderDetailBean order) {
        OrderVerificationFragment of = new OrderVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VerificationManagementHelper.VERIFICATION_ORDER_TYPE, order);
        of.setArguments(bundle);
        return of;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_verification, container, false);
        ButterKnife.bind(this, view);
        mOrder = (PayOrderDetailBean) getArguments().getSerializable(VerificationManagementHelper.VERIFICATION_ORDER_TYPE);
        return view;
    }

    @Override
    protected void initView() {
        mOrderNo = mOrder.orderNo;
        if (Utils.isNotEmpty(mOrder.phoneNum)) {
            verificationOrderCustomerName.setText(String.format("%s (%s)", mOrder.customerName, mOrder.phoneNum));
        } else {
            verificationOrderCustomerName.setText(mOrder.customerName);
        }
        verificationOrderStartTime.setText(mOrder.appointTime);
        verificationOrderTime.setText(mOrder.createdAt);
        if (Utils.isNotEmpty(mOrder.techName)) {
            verificationOrderTech.setText(mOrder.techName);
        }
        if (mOrder.isExpire) {
            btnOrderOverTime.setVisibility(View.VISIBLE);
        } else {
            btnOrderOverTime.setVisibility(View.GONE);
        }
        float mDownPayment = mOrder.downPayment / 100f;
        verificationOrderPay.setText(String.format("%1.2f", mDownPayment) + "元");
        verificationOrderState.setText(mOrder.statusName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btn_order_verification, R.id.btn_order_over_time, R.id.btn_order_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_order_verification:
                Map<String, String> params = new HashMap<>();
                params.put(RequestConstant.KEY_ORDER_NO, mOrderNo);
                params.put(RequestConstant.KEY_PAY_ORDER_PROCESS_TYPE, RequestConstant.ORDER_VERIFICATION_VERIFIED);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHECK_INFO_ORDER_SAVE, params);
                break;
            case R.id.btn_order_over_time:
                Map<String, String> paramExpire = new HashMap<>();
                paramExpire.put(RequestConstant.KEY_ORDER_NO, mOrderNo);
                paramExpire.put(RequestConstant.KEY_PAY_ORDER_PROCESS_TYPE, RequestConstant.ORDER_VERIFICATION_EXPIRE);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHECK_INFO_ORDER_SAVE, paramExpire);
                break;
            case R.id.btn_order_cancel:
                getActivity().finish();
                break;
        }
    }
}
