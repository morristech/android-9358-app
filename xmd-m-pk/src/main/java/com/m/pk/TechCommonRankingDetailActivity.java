package com.m.pk;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.m.pk.databinding.ActivityTechCommonRankingDetailBinding;
import com.m.pk.event.DateChangedEvent;
import com.xmd.app.BaseActivity;
import com.xmd.app.PageFragmentAdapter;
import com.xmd.app.utils.DateUtil;
import com.xmd.app.utils.ResourceUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by Lhj on 18-1-5.
 */

public class TechCommonRankingDetailActivity extends BaseActivity {

    public static final String FORMAT_YEAR = Constant.FORMAT_YEAR;
    public static final String FORMAT_MONTH = Constant.FORMAT_MONTH;
    private final long DAY_MILLISECOND = 24 * 3600 * 1000l;//24小时毫秒值
    private final long WEEK_MILLISECOND = 7 * 24 * 3600 * 1000l;//7*24小时
    public int mTimeFilterType = 1;
    private PageFragmentAdapter mPageFragmentAdapter;
    private long mCurrentMillisecond = 0;  //当前显示日期毫秒日
    private long mFirstTodayMillisecond;   //当前时间方式第一天毫秒值
    private long mFirstWeekMillisecond;//按周排序
    private long mFirstMonthMillisecond;//按月排序第一天
    private int mQuarterTime;//按季度
    private int mYearTime;//按年排序
    private String mStartDate;//当前查询开始时间
    private String mEndDate;//当前查询结束时间

    public ObservableField<String> dateType = new ObservableField<>();
    public ObservableField<String> timeCurrent = new ObservableField<>();
    public ObservableField<String> timeDetail = new ObservableField<>();
    public ObservableField<String> timeBefore = new ObservableField<>();
    public ObservableField<String> timeNext = new ObservableField<>();
    public ObservableBoolean showTotalView = new ObservableBoolean();
    public ObservableBoolean showTimeToday = new ObservableBoolean();
    public ObservableBoolean showTimeNext = new ObservableBoolean();
    public ObservableBoolean showTimeBefore = new ObservableBoolean();
    private boolean showTotal;
    public ObservableBoolean hasNext = new ObservableBoolean();

    ActivityTechCommonRankingDetailBinding mBinding;

    public static void startTechCommonRankingDetailActivity(Activity activity, String userType) {
        Intent intent = new Intent(activity, TechCommonRankingDetailActivity.class);
        intent.putExtra(Constant.INTENT_KEY_APP_TYPE, userType);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tech_common_ranking_detail);
        mCurrentMillisecond = System.currentTimeMillis();
        initViewPagerView();
        setStatusBarColor();
        mBinding.setData(this);
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#F98263"));
        }
    }

    private void initViewPagerView() {
        showTotalView.set(false);
        String[] tabTexts = new String[]{ResourceUtils.getString(R.string.personal_ranking_toker), ResourceUtils.getString(R.string.personal_ranking_sale), ResourceUtils.getString(R.string.personal_ranking_service)};
        mPageFragmentAdapter = new PageFragmentAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(TechCommonRankingDetailFragment.BIZ_TYPE, i);
            args.putString(TechCommonRankingDetailFragment.USER_TYPE, getIntent().getStringExtra(Constant.INTENT_KEY_APP_TYPE));
            mPageFragmentAdapter.addFragment(TechCommonRankingDetailFragment.class.getName(), args);
        }
        mBinding.viewPager.setOffscreenPageLimit(3);
        mBinding.viewPager.setAdapter(mPageFragmentAdapter);
        mBinding.tabIndicator.setTabTexts(tabTexts);
        mBinding.tabIndicator.setWithIndicator(true);
        mBinding.tabIndicator.setIndicatorGravity(mBinding.tabIndicator.INDICATOR_BOTTOM);
        mBinding.tabIndicator.setViewPager(mBinding.viewPager);
        mBinding.tabIndicator.setWithDivider(false);
        mBinding.tabIndicator.setup();
        dateType.set(ResourceUtils.getString(R.string.date_type_week));
        getTime(Constant.DATE_TYPE_BY_WEEK);
    }

    public void onTimeFilterClickedListener(View v) {
        PopupMenu popupMenu = new PopupMenu(this, mBinding.rlPkDetailTime);
        popupMenu.inflate(R.menu.pk_date_filter);
        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_time_day) {
                    showTotal = false;
                    mTimeFilterType = Constant.DATE_TYPE_BY_DAY;
                    dateType.set(ResourceUtils.getString(R.string.date_type_day));
                } else if (item.getItemId() == R.id.menu_time_week) {
                    showTotal = false;
                    mTimeFilterType = Constant.DATE_TYPE_BY_WEEK;
                    dateType.set(ResourceUtils.getString(R.string.date_type_week));
                } else if (item.getItemId() == R.id.menu_time_month) {
                    showTotal = false;
                    mTimeFilterType = Constant.DATE_TYPE_BY_MONTH;
                    dateType.set(ResourceUtils.getString(R.string.date_type_month));
                } else if (item.getItemId() == R.id.menu_time_quarter) {
                    showTotal = false;
                    mTimeFilterType = Constant.DATE_TYPE_BY_QUARTER;
                    dateType.set(ResourceUtils.getString(R.string.date_type_quarter));
                } else if (item.getItemId() == R.id.menu_time_year) {
                    showTotal = false;
                    mTimeFilterType = Constant.DATE_TYPE_BY_YEAR;
                    dateType.set(ResourceUtils.getString(R.string.date_type_year));
                } else if (item.getItemId() == R.id.menu_time_total) {
                    showTotal = true;
                    mTimeFilterType = Constant.DATE_TYPE_BY_TOTAL;
                    dateType.set(ResourceUtils.getString(R.string.date_type_total));
                }
                showTotalView.set(showTotal);
                getTime(mTimeFilterType);
                return true;
            }
        });
        popupMenu.show();
    }

    private void getTime(int timeFilterType) {
        showTimeToday.set(true);
        showTimeNext.set(false);
        showTimeBefore.set(true);
        switch (timeFilterType) {
            case Constant.DATE_TYPE_BY_DAY:
                timeCurrent.set(ResourceUtils.getString(R.string.current_date_type_day));
                timeBefore.set(DateUtil.getCurrentDate(mCurrentMillisecond - DAY_MILLISECOND));
                timeDetail.set(DateUtil.getCurrentDate(mCurrentMillisecond));
                mFirstTodayMillisecond = mCurrentMillisecond;
                mStartDate = DateUtil.getCurrentDate(mFirstTodayMillisecond, FORMAT_YEAR);
                mEndDate = mStartDate;
                break;
            case Constant.DATE_TYPE_BY_WEEK:
                mFirstWeekMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfWeek(new Date(), Constant.FORMAT_YEAR));
                timeCurrent.set(ResourceUtils.getString(R.string.current_date_type_week));
                timeBefore.set(ResourceUtils.getString(R.string.current_date_type_before_week));
                timeDetail.set(String.format("%s-%s", DateUtil.getFirstDayOfWeek(new Date(), Constant.FORMAT_MONTH), DateUtil.getLastDayOfWeek(new Date(), Constant.FORMAT_MONTH)));
                mStartDate = DateUtil.getFirstDayOfWeek(new Date(), FORMAT_YEAR);
                mEndDate = DateUtil.getLastDayOfWeek(new Date(), FORMAT_YEAR);
                break;
            case Constant.DATE_TYPE_BY_MONTH:
                mFirstMonthMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfMonth());
                timeCurrent.set(ResourceUtils.getString(R.string.current_date_type_month));
                timeDetail.set(String.format("%s-%s", DateUtil.getFirstDayOfMonth(FORMAT_MONTH), DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_MONTH)));
                timeBefore.set(ResourceUtils.getString(R.string.current_date_type_before_month));
                mStartDate = DateUtil.getFirstDayOfMonth(FORMAT_YEAR);
                mEndDate = DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_YEAR);
                break;
            case Constant.DATE_TYPE_BY_QUARTER:
                mQuarterTime = (DateUtil.getCurrentYear() - 2015) * 4 + DateUtil.getCurrentQuarter();
                timeCurrent.set(ResourceUtils.getString(R.string.current_date_type_quarter));
                timeDetail.set(String.format("%s第%s季度", DateUtil.getCurrentYear(), DateUtil.getCurrentQuarter()));
                timeBefore.set(ResourceUtils.getString(R.string.current_date_type_before_quarter));
                mFirstTodayMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfMonth(FORMAT_YEAR));
                mStartDate = getQuarterStartOrEnd(mQuarterTime, false);
                mEndDate = getQuarterStartOrEnd(mQuarterTime, true);
                break;
            case Constant.DATE_TYPE_BY_YEAR:
                mYearTime = DateUtil.getCurrentYear();
                timeCurrent.set(ResourceUtils.getString(R.string.current_date_type_year));
                timeBefore.set(String.valueOf(DateUtil.getCurrentYear() - 1));
                showTimeToday.set(false);
                mStartDate = String.format("%s-01-01", mYearTime);
                mEndDate = DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_YEAR);
                break;
            case Constant.DATE_TYPE_BY_TOTAL:
                mStartDate = "2015-01-01";
                mEndDate = DateUtil.getDate(System.currentTimeMillis());
                break;
            default:
                mStartDate = "2015-01-01";
                mEndDate = DateUtil.getDate(System.currentTimeMillis());

        }
        timeChangedPost(mStartDate, mEndDate);
    }

    public void reduceTimeClicked(View view) {
        initTimeFilterView(mTimeFilterType, true);
    }


    public void addTimeClicked(View view) {
        initTimeFilterView(mTimeFilterType, false);
    }

    private void initTimeFilterView(int timeFilterType, boolean reduce) {
        showTimeNext.set(false);
        showTimeToday.set(true);
        switch (timeFilterType) {
            case Constant.DATE_TYPE_BY_DAY:
                if (reduce) {
                    mCurrentMillisecond -= DAY_MILLISECOND;
                } else {
                    mCurrentMillisecond += DAY_MILLISECOND;
                }
                if (mFirstTodayMillisecond == mCurrentMillisecond) {
                    timeCurrent.set(ResourceUtils.getString(R.string.current_date_type_day));
                    timeDetail.set(DateUtil.getCurrentDate(mCurrentMillisecond));
                } else {
                    showTimeNext.set(true);
                    showTimeToday.set(false);
                    timeCurrent.set(DateUtil.getCurrentDate(mCurrentMillisecond));
                }
                timeBefore.set(DateUtil.getCurrentDate(mCurrentMillisecond - DAY_MILLISECOND));
                timeNext.set(DateUtil.getCurrentDate(mCurrentMillisecond + DAY_MILLISECOND));
                mStartDate = DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_YEAR);
                mEndDate = mStartDate;
                break;
            case Constant.DATE_TYPE_BY_WEEK:
                if (reduce) {
                    mFirstWeekMillisecond -= WEEK_MILLISECOND;
                } else {
                    mFirstWeekMillisecond += WEEK_MILLISECOND;
                }
                timeCurrent.set(ResourceUtils.getString(R.string.current_date_type_week));
                timeBefore.set(ResourceUtils.getString(R.string.current_date_type_before_week));
                timeNext.set(ResourceUtils.getString(R.string.current_date_type_next_week));
                if (mFirstWeekMillisecond == DateUtil.stringDateToLong(DateUtil.getFirstDayOfWeek(new Date(), Constant.FORMAT_YEAR))) {
                    timeDetail.set(String.format("%s-%s", DateUtil.getFirstDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), Constant.FORMAT_MONTH),
                            DateUtil.getLastDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mCurrentMillisecond, "")), Constant.FORMAT_MONTH)));
                } else {
                    timeDetail.set(String.format("%s-%s", DateUtil.getFirstDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), Constant.FORMAT_MONTH),
                            DateUtil.getLastDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), Constant.FORMAT_MONTH)));
                    showTimeNext.set(true);
                }
                mStartDate = DateUtil.getFirstDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), Constant.FORMAT_YEAR);
                mEndDate = DateUtil.getLastDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), Constant.FORMAT_YEAR);
                break;
            case Constant.DATE_TYPE_BY_MONTH:
                if (reduce) {
                    mFirstMonthMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfLastMonth(DateUtil.long2Date(mFirstMonthMillisecond, ""), FORMAT_YEAR));
                } else {
                    mFirstMonthMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfNextMonth(DateUtil.long2Date(mFirstMonthMillisecond, ""), FORMAT_YEAR));
                }
                timeCurrent.set(ResourceUtils.getString(R.string.current_date_type_month));
                timeBefore.set(ResourceUtils.getString(R.string.current_date_type_before_month));
                timeNext.set(ResourceUtils.getString(R.string.current_date_type_next_month));
                if (mFirstMonthMillisecond == DateUtil.stringDateToLong(DateUtil.getFirstDayOfMonth())) {
                    timeDetail.set(String.format("%s-%s", DateUtil.getFirstDayOfMonth(FORMAT_MONTH), DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_MONTH)));
                } else {
                    String st = DateUtil.getFirstDayOfMonth(DateUtil.long2Date(mFirstMonthMillisecond, "yyyy年MM月dd日"), "yyyy年MM月dd日");
                    String end = DateUtil.getLastDayOfMonth(DateUtil.long2Date(mFirstMonthMillisecond, "yyyy年MM月dd日"), "yyyy年MM月dd日");
                    if (DateUtil.getCurrentYear() == Integer.parseInt(st.substring(0, 4))) {
                        timeDetail.set(String.format("%s-%s", st.substring(5, st.length()), end.substring(5, st.length())));
                    } else {
                        timeDetail.set(String.format("%s-%s", st, end));
                    }
                    showTimeNext.set(true);
                }
                mStartDate = DateUtil.getFirstDayOfMonth(DateUtil.long2Date(mFirstMonthMillisecond, ""), FORMAT_YEAR);
                mEndDate = DateUtil.getLastDayOfMonth(DateUtil.long2Date(mFirstMonthMillisecond, ""), FORMAT_YEAR);
                break;
            case Constant.DATE_TYPE_BY_QUARTER:
                if (reduce) {
                    mQuarterTime -= 1;
                } else {
                    mQuarterTime += 1;
                }
                timeBefore.set(ResourceUtils.getString(R.string.current_date_type_before_quarter));
                timeNext.set(ResourceUtils.getString(R.string.current_date_type_next_quarter));
                if (mQuarterTime == (DateUtil.getCurrentYear() - 2015) * 4 + DateUtil.getCurrentQuarter()) {
                    timeDetail.set(String.format("%s第%s季度", DateUtil.getCurrentYear(), DateUtil.getCurrentQuarter()));
                    mFirstTodayMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfMonth(FORMAT_YEAR));
                } else {
                    showTimeBefore.set(mQuarterTime == 1 ? false : true);
                    timeDetail.set(String.format("%s第%s季度", (2015 + (mQuarterTime - 1) / 4), mQuarterTime % 4 == 0 ? 4 : mQuarterTime % 4));
                    showTimeNext.set(true);
                }
                mStartDate = getQuarterStartOrEnd(mQuarterTime, false);
                mEndDate = getQuarterStartOrEnd(mQuarterTime, true);
                break;
            case Constant.DATE_TYPE_BY_YEAR:
                if (reduce) {
                    mYearTime -= 1;
                } else {
                    mYearTime += 1;
                }
                timeDetail.set(null);
                if (mYearTime == DateUtil.getCurrentYear()) {
                    timeCurrent.set(ResourceUtils.getString(R.string.current_date_type_year));
                    showTimeNext.set(false);
                    mStartDate = String.format("%s-12-31", mYearTime);
                    timeBefore.set(String.valueOf(DateUtil.getCurrentYear() - 1));
                    mEndDate = DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_YEAR);
                } else {
                    showTimeBefore.set(mYearTime == 2015 ? false : true);
                    showTimeNext.set(true);
                    timeCurrent.set(String.valueOf(mYearTime));
                    timeNext.set(String.valueOf(mYearTime + 1));
                    timeBefore.set(String.valueOf(mYearTime - 1));
                    mStartDate = String.format("%s-01-01", mYearTime);
                    mEndDate = String.format("%s-12-31", mYearTime);
                }
                break;
        }
        timeChangedPost(mStartDate, mEndDate);
    }

    public void finishClicked(View view) {
        this.finish();
    }


    private String getQuarterStartOrEnd(int quarterTime, boolean isEnd) {
        String startDes = "";
        String endDes = "";
        switch (quarterTime % 4) {
            case 0:
                startDes = String.format("%s-10-01", (2015 + (mQuarterTime - 1) / 4));
                endDes = String.format("%s-12-31", (2015 + (mQuarterTime - 1) / 4));
                break;
            case 1:
                startDes = String.format("%s-01-01", (2015 + (mQuarterTime - 1) / 4));
                endDes = String.format("%s-03-31", (2015 + (mQuarterTime - 1) / 4));
                break;
            case 2:
                startDes = String.format("%s-04-01", (2015 + (mQuarterTime - 1) / 4));
                endDes = String.format("%s-06-30", (2015 + (mQuarterTime - 1) / 4));
                break;
            case 3:
                startDes = String.format("%s-07-01", (2015 + (mQuarterTime - 1) / 4));
                endDes = String.format("%s-9-30", (2015 + (mQuarterTime - 1) / 4));
                break;
        }
        if (isEnd) {
            return endDes;
        } else {
            return startDes;
        }
    }

    private void timeChangedPost(String str, String end) {
        EventBus.getDefault().post(new DateChangedEvent(str,end));
    }
}
