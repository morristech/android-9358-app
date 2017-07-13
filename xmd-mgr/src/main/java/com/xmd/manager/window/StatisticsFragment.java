package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.auth.AuthConstants;
import com.xmd.manager.auth.AuthHelper;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.WidgetUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.StatisticsHomeDataResult;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-26.
 */
public class StatisticsFragment extends BaseFragment {

    public static final int TAB_TODAY = 0;
    public static final int TAB_CURRENT_WEEK = 1;
    public static final int TAB_CURRENT_MONTH = 2;
    public static final int TAB_ACCUMULATE = 3;

    private int mRange;
    private Subscription mGetStatisticsHomeDataSubscription;

    @BindView(R.id.tv_register_users)
    TextView mTvRegisterUsers;

    @BindView(R.id.tv_propagate)
    TextView mTvPropagate;

    @BindView(R.id.tv_delivery)
    TextView mTvDelivery;

    @BindView(R.id.tv_share)
    TextView mTvShare;

    @BindView(R.id.tv_to_club)
    TextView mTvToClub;

    @BindView(R.id.tv_total_income)
    TextView mTvTotalIncome;

    @BindView(R.id.tv_tech_income)
    TextView mTvTechIncome;

    @BindView(R.id.tv_wifi_propagate)
    TextView mTvWifiPropagate;

    @BindView(R.id.tv_statistics_visit)
    TextView mTvVisitStatistics;

    @BindView(R.id.rl_wifi_propagate)
    RelativeLayout rlWifiPropagate;
    @BindView(R.id.rl_online_visit)
    RelativeLayout rlOnlineVisit;
    @BindView(R.id.rl_register)
    RelativeLayout rlRegister;
    @BindView(R.id.ll_coupons)
    LinearLayout llCoupons;
    @BindView(R.id.ll_dianzhong)
    LinearLayout llDianzhong;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        return view;
    }

    @OnClick({R.id.rl_register, R.id.ll_coupons, R.id.ll_dianzhong})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_register:
                Intent intentRegister = new Intent(getActivity(), RegisterDetailActivity.class);
                intentRegister.putExtra(Constant.PARAM_RANGE, mRange);
                startActivity(intentRegister);
                break;
            case R.id.ll_coupons:
                Intent intentCoupons = new Intent(getActivity(), CouponsDetailActivity.class);
                intentCoupons.putExtra(Constant.PARAM_RANGE, mRange);
                startActivity(intentCoupons);
                break;
            case R.id.ll_dianzhong:
                Intent intentPaidCoupon = new Intent(getActivity(), PaidCouponDetailActivity.class);
                intentPaidCoupon.putExtra(Constant.PARAM_RANGE, mRange);
                startActivity(intentPaidCoupon);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetStatisticsHomeDataSubscription);
    }

    private void initVisibilityForViews() {
        WidgetUtils.setViewVisibleOrGone(rlRegister, AuthHelper.checkAuthorized(AuthConstants.AUTH_CODE_INDEX_REGISTER));
        WidgetUtils.setViewVisibleOrGone(rlWifiPropagate, AuthHelper.checkAuthorized(AuthConstants.AUTH_CODE_INDEX_WIFI));
        WidgetUtils.setViewVisibleOrGone(rlOnlineVisit, AuthHelper.checkAuthorized(AuthConstants.AUTH_CODE_INDEX_ONLINE));
        WidgetUtils.setViewVisibleOrGone(llCoupons, AuthHelper.checkAuthorized(AuthConstants.AUTH_CODE_INDEX_COUPON));
        WidgetUtils.setViewVisibleOrGone(llDianzhong, AuthHelper.checkAuthorized(AuthConstants.AUTH_CODE_INDEX_COUPON));
    }

    @Override
    protected void initView() {
        initVisibilityForViews();
        mRange = mArguments.getInt(Constant.PARAM_RANGE);
        mGetStatisticsHomeDataSubscription = RxBus.getInstance().toObservable(StatisticsHomeDataResult.class).subscribe(
                result -> {
                    int type = Integer.parseInt(result.type);
                    if (type != mRange) {
                        return;
                    }
                    if (result.statusCode != RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
                        mTvRegisterUsers.setText(String.valueOf(result.respData.userCount));
                        mTvPropagate.setText(String.valueOf(result.respData.couponOpenCount));
                        mTvDelivery.setText(String.valueOf(result.respData.couponGetCount));
                        mTvShare.setText(String.valueOf(result.respData.couponShareCount));
                        mTvToClub.setText(String.valueOf(result.respData.couponUseCount));
                        mTvTotalIncome.setText(String.format(ResourceUtils.getString(R.string.float_amount_unit_format), result.respData.clubAmount));
                        mTvTechIncome.setText(String.format(ResourceUtils.getString(R.string.float_amount_unit_format), result.respData.techCommission));
                        mTvWifiPropagate.setText(String.valueOf(result.respData.wifiCount));
                        mTvVisitStatistics.setText(String.valueOf(result.respData.uv));
                    }
                }
        );

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
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_STATISTICS_HOME_DATA, params);
    }

}
