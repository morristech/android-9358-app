package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.StatisticsHomeDataResult;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by lhj on 2016/6/28.
 */
public class CouponsDetailFragment extends BaseFragment {

    @Bind(R.id.tv_propagate)
    TextView tvPropagate;
    @Bind(R.id.tv_delivery)
    TextView tvDelivery;
    @Bind(R.id.tv_share)
    TextView tvShare;
    @Bind(R.id.tv_to_club)
    TextView tvToClub;

    private int mRange;
    private Subscription mGetCouponsDetailDataSubscription;
    public static final String BIZ_TYPE = "type";
    private Map<String, String> mParams;
    private Map<String, String> mSwitchParams;
    private String startDate, endDate;
    private TextView tvStart, tvEnd;
    private boolean isVisible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupons_detail, container, false);
        ButterKnife.bind(this, view);
        mParams = new HashMap<>();
        mSwitchParams = new HashMap<>();
        tvStart = (TextView) getActivity().findViewById(R.id.startTime);
        tvEnd = (TextView) getActivity().findViewById(R.id.endTime);
        isVisible = true;
        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && isVisible) {
            TextView tvStart = (TextView) getActivity().findViewById(R.id.startTime);
            TextView tvEnd = (TextView) getActivity().findViewById(R.id.endTime);
            startDate = tvStart.getText().toString();
            endDate = tvEnd.getText().toString();
            getData();
        }
    }

    private void getData() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TYPE, String.valueOf(mRange));
        params.put(RequestConstant.KEY_START_DATE, startDate);
        params.put(RequestConstant.KEY_END_DATE, endDate);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_STATISTICS_HOME_DATA, params);
    }


    @Override
    protected void initView() {
        mRange = mArguments.getInt(BIZ_TYPE);
        mGetCouponsDetailDataSubscription = RxBus.getInstance().toObservable(StatisticsHomeDataResult.class)
                .subscribe(result -> {
                    int type = Integer.parseInt(result.type);
                    if (type != mRange) {
                        return;
                    }
                    if (result.statusCode != RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
                        tvPropagate.setText(String.valueOf(result.respData.couponOpenCount));
                        tvDelivery.setText(String.valueOf(result.respData.couponGetCount));
                        tvShare.setText(String.valueOf(result.respData.couponShareCount));
                        tvToClub.setText(String.valueOf(result.respData.couponUseCount));
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        RxBus.getInstance().unsubscribe(mGetCouponsDetailDataSubscription);
    }
}
