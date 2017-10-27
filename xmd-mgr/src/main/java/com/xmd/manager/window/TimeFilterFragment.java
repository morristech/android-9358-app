package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.event.DateChangedEvent;
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.EmptyView;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-9-18.
 */

public class TimeFilterFragment extends BaseFragment {

    @BindView(R.id.rl_time_reduce)
    RelativeLayout rlTimeReduce;
    @BindView(R.id.tv_time_day_or_month)
    TextView tvTimeDayOrMonth;
    @BindView(R.id.ll_time_by_day_or_month)
    LinearLayout llTimeByDayOrMonth;
    @BindView(R.id.tv_time_week)
    TextView tvTimeWeek;
    @BindView(R.id.ll_time_by_week)
    LinearLayout llTimeByWeek;
    @BindView(R.id.rl_time_add)
    RelativeLayout rlTimeAdd;
    @BindView(R.id.ll_time_filter)
    LinearLayout llTimeFilter;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.ll_time_customer)
    LinearLayout llTimeCustomer;
    Unbinder unbinder;

    public static final long WEEK_LONG_DIFFERENCE = 7 * 24 * 60 * 60 * 1000l;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private String mCurrentDateYesterdayTime;
    private String mCurrentDateWeekStartTime;
    private String mCurrentDateWeekEndTime;
    private String mCurrentDateMonthStartTime;
    private String mCurrentDateMonthEndTime;
    private String mCurrentDateTotalTime;
    private String mCurrentDateCustomizedStartTime;
    private String mCurrentDateCustomizedEndTime;
    private int mCurrentType;
    private String mCurrentStartTime;
    private String mCurrentEndTime;

    private long mCurrentWeekStartLongTime;
    private boolean firstLoading = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_filter, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mCurrentDateYesterdayTime = DateUtil.getCurrentDate();
        mCurrentDateWeekStartTime = DateUtil.getFirstDayOfWeek(new Date(), DATE_FORMAT);
        mCurrentDateWeekEndTime = DateUtil.getLastDayOfWeek(new Date(), DATE_FORMAT);
        mCurrentDateMonthStartTime = DateUtil.getFirstDayOfMonth(DATE_FORMAT);
        mCurrentDateMonthEndTime = DateUtil.getLastDayOfMonth(DateUtil.getCurrentDate(), DATE_FORMAT);
        mCurrentDateTotalTime = SharedPreferenceHelper.getCurrentClubCreateTime() + "～" + DateUtil.getCurrentDate();
        mCurrentDateCustomizedStartTime = "";
        mCurrentDateCustomizedEndTime = "";
        mCurrentWeekStartLongTime = DateUtil.stringDateToLong(DateUtil.getCurrentDate());
        setCurrentTimeFilterType(ReserveDataActivity.TIME_FILTER_TYPE_MONTH);
    }

    public void setCurrentTimeFilterType(int timeFilterType) {
        mCurrentType = timeFilterType;
        switch (timeFilterType) {
            case ReserveDataActivity.TIME_FILTER_TYPE_DAY:
                llTimeCustomer.setVisibility(View.GONE);
                llTimeFilter.setVisibility(View.VISIBLE);
                llTimeByDayOrMonth.setVisibility(View.VISIBLE);
                llTimeByWeek.setVisibility(View.GONE);
                rlTimeAdd.setVisibility(View.VISIBLE);
                rlTimeReduce.setVisibility(View.VISIBLE);
                tvTimeDayOrMonth.setText(mCurrentDateYesterdayTime);
                mCurrentStartTime = mCurrentDateYesterdayTime;
                mCurrentEndTime = mCurrentStartTime;
                break;
            case ReserveDataActivity.TIME_FILTER_TYPE_WEEK:
                llTimeCustomer.setVisibility(View.GONE);
                llTimeFilter.setVisibility(View.VISIBLE);
                llTimeByDayOrMonth.setVisibility(View.GONE);
                llTimeByWeek.setVisibility(View.VISIBLE);
                rlTimeAdd.setVisibility(View.VISIBLE);
                rlTimeReduce.setVisibility(View.VISIBLE);
                tvTimeWeek.setText(String.format("%s～%s", mCurrentDateWeekStartTime, mCurrentDateWeekEndTime));
                mCurrentStartTime = mCurrentDateWeekStartTime;
                mCurrentEndTime = mCurrentDateWeekEndTime;
                break;
            case ReserveDataActivity.TIME_FILTER_TYPE_MONTH:
                llTimeCustomer.setVisibility(View.GONE);
                llTimeFilter.setVisibility(View.VISIBLE);
                llTimeByDayOrMonth.setVisibility(View.VISIBLE);
                llTimeByWeek.setVisibility(View.GONE);
                rlTimeAdd.setVisibility(View.VISIBLE);
                rlTimeReduce.setVisibility(View.VISIBLE);
                tvTimeDayOrMonth.setText(mCurrentDateMonthStartTime.substring(0, 7));
                mCurrentStartTime = mCurrentDateMonthStartTime;
                mCurrentEndTime = mCurrentDateMonthEndTime;
                break;
            case ReserveDataActivity.TIME_FILTER_TYPE_TOTAL:
                llTimeCustomer.setVisibility(View.GONE);
                llTimeFilter.setVisibility(View.VISIBLE);
                llTimeByDayOrMonth.setVisibility(View.GONE);
                llTimeByWeek.setVisibility(View.VISIBLE);
                rlTimeAdd.setVisibility(View.INVISIBLE);
                rlTimeReduce.setVisibility(View.INVISIBLE);
                tvTimeWeek.setText(mCurrentDateTotalTime);
                mCurrentStartTime = SharedPreferenceHelper.getCurrentClubCreateTime();
                mCurrentEndTime = DateUtil.getCurrentDate();
                break;
            case ReserveDataActivity.TIME_FILTER_TYPE_CUSTOMIZED:
                mCurrentDateCustomizedStartTime = mCurrentStartTime;
                mCurrentDateCustomizedEndTime = mCurrentEndTime;
                llTimeCustomer.setVisibility(View.VISIBLE);
                llTimeFilter.setVisibility(View.GONE);
                tvStartTime.setText(mCurrentDateCustomizedStartTime);
                tvEndTime.setText(mCurrentDateCustomizedEndTime);
                break;
        }
        if (!firstLoading) {
            EventBus.getDefault().post(new DateChangedEvent(mCurrentStartTime, mCurrentEndTime));
        }
        firstLoading = false;

    }

    @OnClick({R.id.rl_time_reduce, R.id.rl_time_add, R.id.tv_start_time, R.id.tv_end_time, R.id.btn_time_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_time_reduce:
                switch (mCurrentType) {
                    case ReserveDataActivity.TIME_FILTER_TYPE_DAY:
                        mCurrentStartTime = DateUtil.getLastDate(DateUtil.stringDateToLong(mCurrentDateYesterdayTime), DATE_FORMAT);
                        mCurrentDateYesterdayTime = mCurrentStartTime;
                        mCurrentEndTime = mCurrentStartTime;
                        tvTimeDayOrMonth.setText(mCurrentDateYesterdayTime);
                        notifyDateChanged(mCurrentStartTime, mCurrentEndTime);
                        break;
                    case ReserveDataActivity.TIME_FILTER_TYPE_WEEK:
                        mCurrentWeekStartLongTime -= WEEK_LONG_DIFFERENCE;
                        mCurrentStartTime = DateUtil.getFirstDayOfWeek(DateUtil.stringToDate(DateUtil.getDate(mCurrentWeekStartLongTime)), DATE_FORMAT);
                        mCurrentEndTime = DateUtil.getLastDayOfWeek(DateUtil.stringToDate(DateUtil.getDate(mCurrentWeekStartLongTime)), DATE_FORMAT);
                        mCurrentDateWeekStartTime = mCurrentStartTime;
                        mCurrentDateWeekEndTime = mCurrentEndTime;
                        tvTimeWeek.setText(String.format("%s～%s", mCurrentDateWeekStartTime, mCurrentDateWeekEndTime));
                        notifyDateChanged(mCurrentStartTime, mCurrentEndTime);
                        break;
                    case ReserveDataActivity.TIME_FILTER_TYPE_MONTH:
                        mCurrentStartTime = DateUtil.getFirstDayOfLastMonth(mCurrentStartTime, DATE_FORMAT);
                        mCurrentEndTime = DateUtil.getLastDayOfMonth(mCurrentStartTime, DATE_FORMAT);
                        mCurrentDateMonthStartTime = mCurrentStartTime;
                        mCurrentDateMonthEndTime = mCurrentEndTime;
                        tvTimeDayOrMonth.setText(mCurrentStartTime.substring(0, 7));
                        notifyDateChanged(mCurrentStartTime, mCurrentEndTime);
                        break;
                }
                break;
            case R.id.rl_time_add:
                switch (mCurrentType) {
                    case ReserveDataActivity.TIME_FILTER_TYPE_DAY:
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
                    case ReserveDataActivity.TIME_FILTER_TYPE_WEEK:
                        if (mCurrentStartTime.equals(DateUtil.getFirstDayOfWeek(new Date(), DATE_FORMAT))) {
                            XToast.show("已无更多数据");
                        } else {
                            mCurrentWeekStartLongTime += WEEK_LONG_DIFFERENCE;
                            mCurrentStartTime = DateUtil.getFirstDayOfWeek(DateUtil.stringToDate(DateUtil.getDate(mCurrentWeekStartLongTime)), DATE_FORMAT);
                            mCurrentEndTime = DateUtil.getLastDayOfWeek(DateUtil.stringToDate(DateUtil.getDate(mCurrentWeekStartLongTime)), DATE_FORMAT);
                            mCurrentDateWeekStartTime = mCurrentStartTime;
                            mCurrentDateWeekEndTime = mCurrentEndTime;
                            tvTimeWeek.setText(String.format("%s～%s", mCurrentStartTime, mCurrentEndTime));
                            notifyDateChanged(mCurrentStartTime, mCurrentEndTime);
                        }
                        break;
                    case ReserveDataActivity.TIME_FILTER_TYPE_MONTH:
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
                break;
            case R.id.tv_start_time:
                DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(getActivity(), tvStartTime.getText().toString());
                dataPickDialogStr.dateTimePicKDialog(tvStartTime);
                break;
            case R.id.tv_end_time:
                DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog(getActivity(), tvEndTime.getText().toString());
                dataPickDialogEnd.dateTimePicKDialog(tvEndTime);
                break;
            case R.id.btn_time_search:
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
                int str = Utils.dateToInt(sT);
                int end = Utils.dateToInt(eT);
                if (end >= str) {
                    mCurrentDateCustomizedStartTime = sT;
                    mCurrentDateCustomizedEndTime = eT;
                    notifyDateChanged(sT, eT);
                } else {
                    XToast.show(ResourceUtils.getString(R.string.time_select_alert));
                    return;
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void notifyDateChanged(String st, String ed) {
        EventBus.getDefault().post(new DateChangedEvent(st, ed));
    }

    public String getStartTime() {
        return mCurrentStartTime;
    }

    public String getEndTime() {
        return mCurrentEndTime;
    }
}
