package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.PaidCouponDetailResult;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by lhj on 2016/6/28.
 */
public class PaidCouponDetailFragment extends BaseFragment {


    public static final String BIZ_TYPE = "type";
    public static final int TAB_TODAY = 0;
    public static final int TAB_CURRENT_WEEK = 1;
    public static final int TAB_CURRENT_MONTH = 2;
    public static final int TAB_ACCUMULATE = 3;
    @Bind(R.id.tv_purchase)
    TextView tvPurchase;
    @Bind(R.id.tv_use)
    TextView tvUse;
    @Bind(R.id.tv_share)
    TextView tvShare;
    @Bind(R.id.tv_pasted)
    TextView tvPasted;
    @Bind(R.id.tv_total_income)
    TextView tvTotalIncome;
    @Bind(R.id.tv_technician_income)
    TextView tvTechnicianIncome;

    private int mRange;
    private Subscription mGetPaidCouponDetailSubscription;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paidcoupon_deatail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mRange = mArguments.getInt(BIZ_TYPE);
        mGetPaidCouponDetailSubscription = RxBus.getInstance().toObservable(PaidCouponDetailResult.class).subscribe(result -> {
            int type = Integer.parseInt(result.type);
            if (type != mRange) {
                return;
            }
            if (result.statusCode != RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
                tvPurchase.setText(String.valueOf(result.respData.paidCount));
                tvUse.setText(String.valueOf(result.respData.useCount));
                tvShare.setText(String.valueOf(result.respData.share));
                tvPasted.setText(String.valueOf(result.respData.expireCount));
                tvTotalIncome.setText(String.valueOf(result.respData.clubAmount) + "元");
                tvTechnicianIncome.setText(String.valueOf(result.respData.techCommission) + "元");

            }
        });
        String startDate = "";
        String endDate = DateUtil.getCurrentDate();

        switch (mRange) {
            case TAB_TODAY:
                startDate = DateUtil.getCurrentDate();
                break;
            case TAB_CURRENT_WEEK:
                startDate = DateUtil.getMondayOfWeek();
                break;
            case TAB_CURRENT_MONTH:
                startDate = DateUtil.getFirstDayOfMonth();
                break;
            case TAB_ACCUMULATE:
                break;
        }
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TYPE, String.valueOf(mRange));
        params.put(RequestConstant.KEY_START_DATE, startDate);
        params.put(RequestConstant.KEY_END_DATE, endDate);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_DELIVERY_DETAIL, params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        RxBus.getInstance().unsubscribe(mGetPaidCouponDetailSubscription);
    }
}
