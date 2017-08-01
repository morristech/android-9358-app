package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.MarketingIncomeBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.MarketingIncomeListResult;
import com.xmd.manager.widget.DateTimePickDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-4-27.
 */

public class MarketingIncomeListFragment extends BaseListFragment<MarketingIncomeBean> {

    @BindView(R.id.startTime)
    TextView mStartTime;
    @BindView(R.id.endTime)
    TextView mEndTime;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.tv_marketing_income_total)
    TextView tvMarketingIncomeTotal;

    private Map<String, String> params;

    String startTime;
    String endTime;
    private View view;
    private List<MarketingIncomeBean> incomeList;

    private Subscription mMarketingIncomeSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_marketing_income, container, false);

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
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DATA_STATISTICS_SALE_DATA, params);
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
        mMarketingIncomeSubscription = RxBus.getInstance().toObservable(MarketingIncomeListResult.class).subscribe(
                listResult -> handlerMarketingIncomeList(listResult)
        );
    }

    private void handlerMarketingIncomeList(MarketingIncomeListResult listResult) {
        if (listResult.statusCode == 200) {
            if (listResult.respData == null) {
                return;
            }
            String incomeTotal = String.format("%1.2f", listResult.respData.allAmount / 100f);
            Spannable ss = new SpannableString(incomeTotal);
            ss.setSpan(new TextAppearanceSpan(getActivity(), R.style.text_income), incomeTotal.length() - 2, incomeTotal.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvMarketingIncomeTotal.setText(ss);
            if (incomeList == null) {
                incomeList = new ArrayList<>();
            } else {
                incomeList.clear();
            }
            for (MarketingIncomeBean bean : listResult.respData.saleList) {
                if (listResult.respData.itemCardSwitch.equals("on")) {
                    bean.showItemCard = true;
                } else {
                    bean.showItemCard = false;
                }
                if (Utils.isNotEmpty(listResult.respData.packageCardSwitch) && !listResult.respData.packageCardSwitch.equals("on")) {
                    bean.showPageCard = false;
                } else {
                    bean.showPageCard = true;
                }
                incomeList.add(bean);
            }
            onGetListSucceeded(listResult.pageCount, incomeList);
        } else {
            onGetListFailed(listResult.msg);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mMarketingIncomeSubscription);
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
