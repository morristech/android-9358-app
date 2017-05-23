package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.OnlinePayBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.OnlinePayListResult;
import com.xmd.manager.widget.DateTimePickDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-4-27.
 */

public class OnlinePayListFragment extends BaseListFragment<OnlinePayBean> {

    @Bind(R.id.startTime)
    TextView mStartTime;
    @Bind(R.id.endTime)
    TextView mEndTime;
    @Bind(R.id.btnSubmit)
    Button btnSubmit;
    @Bind(R.id.tv_online_pay_income_total)
    TextView tvOnlinePayTotal;

    private Map<String, String> params;

    String startTime;
    String endTime;
    private View view;


    private Subscription mOnlinePaySubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_online_pay, container, false);

        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void dispatchRequest() {
        if (params == null) {
            params = new HashMap<>();
        } else {
            params.clear();
        }
        params.put(RequestConstant.KEY_START_DATE, startTime);
        params.put(RequestConstant.KEY_END_DATE, endTime);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        params.put(RequestConstant.KEY_STATUS, Constant.ONLINE_PAY_STATUS);
        params.put(RequestConstant.KEY_ONLINE_PAY_TECH_NAME, "");
        params.put(RequestConstant.KEY_IS_SEARCH, "0");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_FAST_PAY_ORDER_LIST, params);
    }

    @Override
    protected void initView() {

        if (Utils.isNotEmpty(SharedPreferenceHelper.getCurrentClubCreateTime())) {
            startTime = SharedPreferenceHelper.getCurrentClubCreateTime();
        } else {
            startTime = "2015-01-01";
        }
        endTime = DateUtil.getCurrentDate();
        mStartTime.setText(startTime);
        mEndTime.setText(endTime);
        mOnlinePaySubscription = RxBus.getInstance().toObservable(OnlinePayListResult.class).subscribe(
                listResult -> handlerMarketingIncomeList(listResult)
        );
    }

    private void handlerMarketingIncomeList(OnlinePayListResult listResult) {
        if (listResult.statusCode == 200) {
            if (listResult.respData == null || listResult.isSearch.equals("1")) {
                return;
            }
            String incomeTotal = String.format("%1.2f", listResult.respData.payAmountSum / 100f);
            Spannable ss = new SpannableString(incomeTotal);
            ss.setSpan(new TextAppearanceSpan(getActivity(), R.style.text_income), incomeTotal.length() - 2, incomeTotal.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvOnlinePayTotal.setText(ss);
            onGetListSucceeded(listResult.pageCount, listResult.respData.fastPayOrderList);

        } else {
            onGetListFailed(listResult.msg);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mOnlinePaySubscription);
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.startTime)
    public void onStartTimeClicked() {
        DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(getActivity(), mStartTime.getText().toString());
        dataPickDialogStr.dateTimePicKDialog(mStartTime);
    }

    @OnClick(R.id.endTime)
    public void onEndTimeClicked() {
        DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog(getActivity(), mEndTime.getText().toString());
        dataPickDialogEnd.dateTimePicKDialog(mEndTime);
    }

    @OnClick(R.id.btnSubmit)
    public void onBtnSubmitClicked() {
        mPages = 0;
        String sT = mStartTime.getText().toString();
        String eT = mEndTime.getText().toString();
        int str = Utils.dateToInt(sT);
        int end = Utils.dateToInt(eT);
        if (end >= str) {
            startTime = sT;
            endTime = eT;
            onRefresh();
        } else {
            ToastUtils.showToastShort(getActivity(), ResourceUtils.getString(R.string.time_select_alert));
        }
    }
}
