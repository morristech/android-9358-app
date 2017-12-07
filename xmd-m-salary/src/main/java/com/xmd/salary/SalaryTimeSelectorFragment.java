package com.xmd.salary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.utils.DateUtil;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.widget.DateTimePickDialog;
import com.xmd.salary.event.DateChangedEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-11-20.
 */

public class SalaryTimeSelectorFragment extends BaseFragment {

    @BindView(R2.id.rl_time_reduce)
    RelativeLayout rlTimeReduce;
    @BindView(R2.id.tv_time_day_or_month)
    TextView tvTimeDayOrMonth;
    @BindView(R2.id.ll_time_by_day_or_month)
    LinearLayout llTimeByDayOrMonth;
    @BindView(R2.id.rl_time_add)
    RelativeLayout rlTimeAdd;
    @BindView(R2.id.ll_time_filter)
    LinearLayout llTimeFilter;
    @BindView(R2.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R2.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R2.id.ll_time_customer)
    LinearLayout llTimeCustomer;
    Unbinder unbinder;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private String mCurrentDateYesterdayTime;
    private String mCurrentDateMonthStartTime;
    private String mCurrentDateMonthEndTime;
    private String mCurrentDateCustomizedStartTime;
    private String mCurrentDateCustomizedEndTime;
    private int mCurrentType;
    private String mCurrentStartTime;
    private String mCurrentEndTime;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_salary_time_selector, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mCurrentDateYesterdayTime = DateUtil.getCurrentDate();
        mCurrentDateMonthStartTime = DateUtil.getFirstDayOfMonth(DATE_FORMAT);
        mCurrentDateMonthEndTime = DateUtil.getLastDayOfMonth(DateUtil.getCurrentDate(), DATE_FORMAT);
        mCurrentDateCustomizedStartTime = "";
        mCurrentDateCustomizedEndTime = "";
        setCurrentTimeFilterType(TechSalaryFragment.TIME_FILTER_TYPE_MONTH);
    }

    public void setCurrentTimeFilterType(int timeFilterType) {
        mCurrentType = timeFilterType;
        switch (timeFilterType) {
            case TechSalaryFragment.TIME_FILTER_TYPE_DAY:
                llTimeCustomer.setVisibility(View.GONE);
                llTimeFilter.setVisibility(View.VISIBLE);
                llTimeByDayOrMonth.setVisibility(View.VISIBLE);
                rlTimeAdd.setVisibility(View.VISIBLE);
                rlTimeReduce.setVisibility(View.VISIBLE);
                tvTimeDayOrMonth.setText(mCurrentDateYesterdayTime);
                mCurrentStartTime = mCurrentDateYesterdayTime;
                mCurrentEndTime = mCurrentStartTime;
                EventBus.getDefault().post(new DateChangedEvent("",mCurrentStartTime,mCurrentEndTime));
                break;
            case TechSalaryFragment.TIME_FILTER_TYPE_MONTH:
                llTimeCustomer.setVisibility(View.GONE);
                llTimeFilter.setVisibility(View.VISIBLE);
                llTimeByDayOrMonth.setVisibility(View.VISIBLE);
                rlTimeAdd.setVisibility(View.VISIBLE);
                rlTimeReduce.setVisibility(View.VISIBLE);
                tvTimeDayOrMonth.setText(mCurrentDateMonthStartTime.substring(0, 7));
                mCurrentStartTime = mCurrentDateMonthStartTime;
                mCurrentEndTime = mCurrentDateMonthEndTime;
                EventBus.getDefault().post(new DateChangedEvent("",mCurrentStartTime,mCurrentEndTime));
                break;
            case TechSalaryFragment.TIME_FILTER_TYPE_CUSTOMIZED:
                mCurrentDateCustomizedStartTime = mCurrentStartTime;
                mCurrentDateCustomizedEndTime = mCurrentEndTime;
                llTimeCustomer.setVisibility(View.VISIBLE);
                llTimeFilter.setVisibility(View.GONE);
                tvStartTime.setText(mCurrentDateCustomizedStartTime);
                tvEndTime.setText(mCurrentDateCustomizedEndTime);
                EventBus.getDefault().post(new DateChangedEvent("",mCurrentDateCustomizedStartTime,mCurrentDateCustomizedEndTime));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.rl_time_reduce)
    public void onRlTimeReduceClicked() {

        switch (mCurrentType) {
            case TechSalaryFragment.TIME_FILTER_TYPE_DAY:
                mCurrentStartTime = DateUtil.getLastDate(DateUtil.stringDateToLong(mCurrentDateYesterdayTime), DATE_FORMAT);
                mCurrentDateYesterdayTime = mCurrentStartTime;
                mCurrentEndTime = mCurrentStartTime;
                tvTimeDayOrMonth.setText(mCurrentDateYesterdayTime);
                notifyDateChanged(mCurrentStartTime, mCurrentEndTime);
                break;
            case TechSalaryFragment.TIME_FILTER_TYPE_MONTH:
                mCurrentStartTime = DateUtil.getFirstDayOfLastMonth(mCurrentStartTime, DATE_FORMAT);
                mCurrentEndTime = DateUtil.getLastDayOfMonth(mCurrentStartTime, DATE_FORMAT);
                mCurrentDateMonthStartTime = mCurrentStartTime;
                mCurrentDateMonthEndTime = mCurrentEndTime;
                tvTimeDayOrMonth.setText(mCurrentStartTime.substring(0, 7));
                notifyDateChanged(mCurrentStartTime, mCurrentEndTime);
                break;
        }

    }

    @OnClick(R2.id.rl_time_add)
    public void onRlTimeAddClicked() {
        switch (mCurrentType) {
            case TechSalaryFragment.TIME_FILTER_TYPE_DAY:
                if (mCurrentStartTime.equals(DateUtil.getCurrentDate())) {
                    XToast.show("已无更多数据");
                } else {
                    mCurrentStartTime = DateUtil.getNextDate(DateUtil.stringDateToLong(mCurrentDateYesterdayTime), DATE_FORMAT);
                    mCurrentDateYesterdayTime = mCurrentStartTime;
                    mCurrentEndTime = mCurrentStartTime;
                    tvTimeDayOrMonth.setText(mCurrentDateYesterdayTime);
                    notifyDateChanged(mCurrentStartTime, mCurrentEndTime);
                }

                break;
            case TechSalaryFragment.TIME_FILTER_TYPE_MONTH:
                if (mCurrentStartTime.equals(DateUtil.getFirstDayOfMonth(DATE_FORMAT))) {
                    XToast.show("已无更多数据");
                } else {
                    mCurrentStartTime = DateUtil.getFirstDayOfNextMonth(mCurrentStartTime, DATE_FORMAT);
                    mCurrentEndTime = DateUtil.getLastDayOfMonth(mCurrentStartTime, DATE_FORMAT);
                    mCurrentDateMonthStartTime = mCurrentStartTime;
                    mCurrentDateMonthEndTime = mCurrentEndTime;
                    tvTimeDayOrMonth.setText(mCurrentStartTime.substring(0, 7));
                    notifyDateChanged(mCurrentStartTime, mCurrentEndTime);
                }
                break;
        }
    }

    @OnClick(R2.id.tv_start_time)
    public void onTvStartTimeClicked() {
        DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(getActivity(), tvStartTime.getText().toString());
        dataPickDialogStr.dateTimePicKDialog(tvStartTime);
    }

    @OnClick(R2.id.tv_end_time)
    public void onTvEndTimeClicked() {
        DateTimePickDialog dataPickDialogEnd= new DateTimePickDialog(getActivity(), tvEndTime.getText().toString());
        dataPickDialogEnd.dateTimePicKDialog(tvEndTime);
    }

    @OnClick(R2.id.btn_time_search)
    public void onBtnTimeSearchClicked() {
        String sT = tvStartTime.getText().toString();
        String eT = tvEndTime.getText().toString();
        if (TextUtils.isEmpty(sT)) {
            XToast.show("请输入开始时间");
            return;
        }
        if (TextUtils.isEmpty(eT)) {
            XToast.show("请输入结束时间");
            return;
        }
        int str = DateUtil.dateToInt(sT);
        int end = DateUtil.dateToInt(eT);
        if (end >= str) {
            mCurrentDateCustomizedStartTime = sT;
            mCurrentDateCustomizedEndTime = eT;
            notifyDateChanged(sT, eT);
        } else {
            XToast.show(ResourceUtils.getString(R.string.time_select_alert));
            return;
        }

    }

    public void notifyDateChanged(String st, String ed) {
        EventBus.getDefault().post(new DateChangedEvent("", st, ed));
    }

    public String getStartTime() {
        return mCurrentStartTime;
    }

    public String getEndTime() {
        return mCurrentEndTime;
    }

}
