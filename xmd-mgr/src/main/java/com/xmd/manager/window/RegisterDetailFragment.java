package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.RegisterStatisticsResult;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by lhj on 2016/6/28.
 */
public class RegisterDetailFragment extends BaseFragment {
    public static final String BIZ_TYPE = "type";
    public static final int TAB_TODAY = 0;
    public static final int TAB_CURRENT_WEEK = 1;
    public static final int TAB_CURRENT_MONTH = 2;
    public static final int TAB_ACCUMULATE = 3;
    @Bind(R.id.totalRegister)
    TextView totalRegister;

    @Bind(R.id.tv_weixin_count)
    TextView weixinCount;
    @Bind(R.id.tv_temp_count)
    TextView tempCount;
    @Bind(R.id.tv_user_count)
    TextView userCount;

    private int mRange;
    private Subscription mGetRegisterDetailDataSubscription;

    private String startDate;
    private String endDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mRange = mArguments.getInt(BIZ_TYPE);

        mGetRegisterDetailDataSubscription = RxBus.getInstance().toObservable(RegisterStatisticsResult.class).subscribe(result -> {
            if (!TextUtils.isEmpty(result.type)) {
                int type = Integer.parseInt(result.type);
                if (type != mRange) {
                    return;
                }
            }

            if (result.statusCode != RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
                totalRegister.setText(String.valueOf(result.respData));
                totalRegister.setText(String.valueOf(result.respData.userTotal));
                weixinCount.setText(String.valueOf(result.respData.weixinCount));
                tempCount.setText(String.valueOf(result.respData.tempUserCount));
                userCount.setText(String.valueOf(result.respData.userCount));
            }
        });

        startDate = getActivity().getIntent().getStringExtra(RequestConstant.KEY_SELECTED_START_TIME);
        endDate = getActivity().getIntent().getStringExtra(RequestConstant.KEY_SELECTED_END_TIME);

        switch (mRange) {
            case TAB_TODAY:
                startDate = DateUtil.getCurrentDate();
                endDate = DateUtil.getCurrentDate();
                break;
            case TAB_CURRENT_WEEK:
                startDate = DateUtil.getMondayOfWeek();
                endDate = DateUtil.getCurrentDate();
                break;
            case TAB_CURRENT_MONTH:
                startDate = DateUtil.getFirstDayOfMonth();
                endDate = DateUtil.getCurrentDate();
                break;
            case TAB_ACCUMULATE:

                break;


        }

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TYPE, String.valueOf(mRange));
        params.put(RequestConstant.KEY_START_DATE, startDate);
        params.put(RequestConstant.KEY_END_DATE, endDate);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_GET_REGISTER_DETAIL, params);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        RxBus.getInstance().unsubscribe(mGetRegisterDetailDataSubscription);
    }
}
