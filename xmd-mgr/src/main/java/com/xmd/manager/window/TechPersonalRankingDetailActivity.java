package com.xmd.manager.window;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.beans.DateChangedResult;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.widget.ArrayBottomPopupWindow;
import com.xmd.manager.widget.EmptyView;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-3-9.
 */

public class TechPersonalRankingDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.pk_detail_time_filter)
    TextView pkDetailTimeFilter;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.pk_active_status)
    TextView pkActiveStatus;
    @BindView(R.id.time_filter_before)
    TextView timeFilterBefore;
    @BindView(R.id.time_filter_today)
    TextView timeFilterToday;
    @BindView(R.id.time_filter_next)
    TextView timeFilterNext;
    @BindView(R.id.tabIndicator)
    ViewPagerTabIndicator tabIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.ll_filter_selected_total)
    LinearLayout llFilterSelectedTotal;
    @BindView(R.id.rl_pk_detail_time)
    RelativeLayout rlPkDetailTime;
    @BindView(R.id.ll_filter_by_day)
    LinearLayout llFilterByDay;
    @BindView(R.id.time_today)
    TextView timeToday;
    @BindView(R.id.empty_view_widget)
    EmptyView emptyView;


    public static final String FORMAT_YEAR = "yyyy-MM-dd";
    public static final String FORMAT_MONTH = "MM月dd日";

    public int mTimeFilterType = 1;
    private final long DAY_MILLISECOND = 24 * 3600 * 1000l;//24小时毫秒值
    private final long WEEK_MILLISECOND = 7 * 24 * 3600 * 1000l;//7*24小时
    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
    private ArrayBottomPopupWindow<String> mTimeFilterPopupWindow;
    private long mCurrentMillisecond = 0;  //当前显示日期毫秒日
    private long mFirstTodayMillisecond;   //当前时间方式第一天毫秒值
    private long mFirstWeekMillisecond;//按周排序
    private long mFirstMonthMillisecond;//按月排序第一天
    private int mQuarterTime;//按季度
    private int mYearTime;//按年排序
    private String mStartDate;//当前查询开始时间
    private String mEndDate;//当前查询结束时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_personal_ranking_detail);
        ButterKnife.bind(this);
        mCurrentMillisecond = System.currentTimeMillis();
        initViewPagerView();
        initTimeFilterPopupWindowView();
        setStatusBarColor();
    }


    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#F36B5A"));
        }

    }

    private void initViewPagerView() {
        getTime(1);
        String[] tabTexts = new String[]{ResourceUtils.getString(R.string.personal_ranking_toker), ResourceUtils.getString(R.string.personal_ranking_sale), ResourceUtils.getString(R.string.personal_ranking_service)};
        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < tabTexts.length; i++) {

            Bundle args = new Bundle();
            args.putSerializable(TechPersonalRankingDetailFragment.BIZ_TYPE, i);
            mPageFragmentPagerAdapter.addFragment(TechPersonalRankingDetailFragment.class.getName(), args);
        }
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mPageFragmentPagerAdapter);
        tabIndicator.setTabTexts(tabTexts);
        tabIndicator.setWithIndicator(true);
        tabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        tabIndicator.setViewPager(viewPager);
        tabIndicator.setWithDivider(false);
        tabIndicator.setup();
        tabIndicator.setOnPageChangeListener(position -> refreshView(position));
        tabIndicator.setOnTabclickListener(position -> refreshView(position));
    }

    private void initTimeFilterPopupWindowView() {
        pkDetailTimeFilter.setText("周");
        mTimeFilterPopupWindow = new ArrayBottomPopupWindow<>(rlPkDetailTime, null, ResourceUtils.getDimenInt(R.dimen.time_filter_item_width));
        mTimeFilterPopupWindow.setDataSet(new ArrayList<>(Constant.RANKING_TIME_FILTER_LABELS.keySet()), pkDetailTimeFilter.getText().toString());
        mTimeFilterPopupWindow.setItemClickListener((parent, view, position, id) -> {
            String sTitle = (String) parent.getAdapter().getItem(position);
            pkDetailTimeFilter.setText(sTitle);
            mTimeFilterPopupWindow.setDataSet(new ArrayList<>(Constant.RANKING_TIME_FILTER_LABELS.keySet()), pkDetailTimeFilter.getText().toString());

            if (pkDetailTimeFilter.getText().toString().equals("天")) {
                llFilterSelectedTotal.setVisibility(View.GONE);
                llFilterByDay.setVisibility(View.VISIBLE);
                mTimeFilterType = 0;
            } else if (pkDetailTimeFilter.getText().toString().equals("周")) {
                llFilterSelectedTotal.setVisibility(View.GONE);
                llFilterByDay.setVisibility(View.VISIBLE);
                mTimeFilterType = 1;
            } else if (pkDetailTimeFilter.getText().toString().equals("月")) {
                llFilterSelectedTotal.setVisibility(View.GONE);
                llFilterByDay.setVisibility(View.VISIBLE);
                mTimeFilterType = 2;
            } else if (pkDetailTimeFilter.getText().toString().equals("季")) {
                llFilterSelectedTotal.setVisibility(View.GONE);
                llFilterByDay.setVisibility(View.VISIBLE);
                mTimeFilterType = 3;
            } else if (pkDetailTimeFilter.getText().toString().equals("年")) {
                llFilterSelectedTotal.setVisibility(View.GONE);
                llFilterByDay.setVisibility(View.VISIBLE);
                mTimeFilterType = 4;
            } else {
                mTimeFilterType = 5;
                llFilterSelectedTotal.setVisibility(View.VISIBLE);
                llFilterByDay.setVisibility(View.GONE);

            }
            getTime(mTimeFilterType);
        });
    }

    @OnClick({R.id.toolbar_back, R.id.rl_pk_detail_time, R.id.time_filter_before, R.id.time_filter_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                this.finish();
                break;
            case R.id.rl_pk_detail_time:
                mTimeFilterPopupWindow.showAsDownCenter(true);
                break;
            case R.id.time_filter_before:
                initTimeFilterView(mTimeFilterType, true);
                break;
            case R.id.time_filter_next:
                initTimeFilterView(mTimeFilterType, false);
                break;
        }
    }

    private void initTimeFilterView(int timeFilterType, Boolean isBefore) {
        switch (timeFilterType) {
            case 0://天
                if (isBefore) {
                    mCurrentMillisecond -= DAY_MILLISECOND;
                } else {
                    mCurrentMillisecond += DAY_MILLISECOND;
                }
                if (mFirstTodayMillisecond == mCurrentMillisecond) {
                    timeFilterNext.setVisibility(View.INVISIBLE);
                    timeToday.setText("今天");
                    timeFilterToday.setVisibility(View.VISIBLE);
                    timeFilterToday.setText(DateUtil.getCurrentDate(mCurrentMillisecond));
                } else {
                    timeFilterToday.setVisibility(View.GONE);
                    timeFilterNext.setVisibility(View.VISIBLE);
                    timeToday.setText(DateUtil.getCurrentDate(mCurrentMillisecond));
                }
                timeFilterBefore.setText(DateUtil.getCurrentDate(mCurrentMillisecond - DAY_MILLISECOND));
                timeFilterNext.setText(DateUtil.getCurrentDate(mCurrentMillisecond + DAY_MILLISECOND));
                mStartDate = DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_YEAR);
                mEndDate = mStartDate;
                break;
            case 1://周
                if (isBefore) {
                    mFirstWeekMillisecond -= WEEK_MILLISECOND;
                } else {
                    mFirstWeekMillisecond += WEEK_MILLISECOND;
                }
                timeFilterToday.setVisibility(View.VISIBLE);

                if (mFirstWeekMillisecond == DateUtil.stringDateToLong(DateUtil.getFirstDayOfWeek(new Date(), "yyyy-MM-dd"))) {
                    timeToday.setText("本周");
                    timeToday.setVisibility(View.VISIBLE);
                    timeFilterBefore.setText("上周");
                    timeFilterNext.setVisibility(View.INVISIBLE);
                    timeFilterToday.setText(String.format("%s-%s", DateUtil.getFirstDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), "MM月dd日"),
                            DateUtil.getLastDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mCurrentMillisecond, "")), "MM月dd日")));
                } else {
                    timeFilterToday.setText(String.format("%s-%s", DateUtil.getFirstDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), "MM月dd日"),
                            DateUtil.getLastDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), "MM月dd日")));
                    timeToday.setText("本周");
                    timeFilterNext.setText("上周");
                    timeFilterNext.setVisibility(View.VISIBLE);
                    timeFilterNext.setText("下周");

                }
                mStartDate = DateUtil.getFirstDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), "yyyy-MM-dd");
                mEndDate = DateUtil.getLastDayOfWeek(DateUtil.stringToDate(DateUtil.long2Date(mFirstWeekMillisecond, "")), "yyyy-MM-dd");
                break;
            case 2://月
                if (isBefore) {
                    mFirstMonthMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfLastMonth(DateUtil.long2Date(mFirstMonthMillisecond, ""), FORMAT_YEAR));
                } else {
                    mFirstMonthMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfNextMonth(DateUtil.long2Date(mFirstMonthMillisecond, ""), FORMAT_YEAR));
                }
                timeFilterToday.setVisibility(View.VISIBLE);
                if (mFirstMonthMillisecond == DateUtil.stringDateToLong(DateUtil.getFirstDayOfMonth())) {
                    timeToday.setText("本月");
                    timeToday.setVisibility(View.VISIBLE);
                    timeFilterBefore.setText("上月");
                    timeFilterNext.setVisibility(View.INVISIBLE);
                    timeFilterToday.setText(String.format("%s-%s", DateUtil.getFirstDayOfMonth(FORMAT_MONTH), DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_MONTH)));
                } else {
                    String st = DateUtil.getFirstDayOfMonth(DateUtil.long2Date(mFirstMonthMillisecond, "yyyy年MM月dd日"), "yyyy年MM月dd日");
                    String end = DateUtil.getLastDayOfMonth(DateUtil.long2Date(mFirstMonthMillisecond, "yyyy年MM月dd日"), "yyyy年MM月dd日");
                    if (DateUtil.getCurrentYear() == Integer.parseInt(st.substring(0, 4))) {
                        timeFilterToday.setText(String.format("%s-%s", st.substring(5, st.length()), end.substring(5, st.length())));
                    } else {
                        timeFilterToday.setText(String.format("%s-%s", st, end));
                    }
                    timeToday.setText("本月");
                    timeFilterBefore.setText("上月");
                    timeFilterNext.setVisibility(View.VISIBLE);
                    timeFilterNext.setText("下月");
                }
                mStartDate = DateUtil.getFirstDayOfMonth(DateUtil.long2Date(mFirstMonthMillisecond, ""), FORMAT_YEAR);
                mEndDate = DateUtil.getLastDayOfMonth(DateUtil.long2Date(mFirstMonthMillisecond, ""), FORMAT_YEAR);
                break;
            case 3://季
                if (isBefore) {
                    mQuarterTime -= 1;
                } else {
                    mQuarterTime += 1;
                }
                timeFilterToday.setVisibility(View.GONE);
                if (mQuarterTime == (DateUtil.getCurrentYear() - 2015) * 4 + DateUtil.getCurrentQuarter()) {
                    timeToday.setVisibility(View.VISIBLE);
                    timeToday.setText(String.format("%s第%s季度", DateUtil.getCurrentYear(), DateUtil.getCurrentQuarter()));
                    timeFilterBefore.setText("上季");
                    timeFilterNext.setVisibility(View.INVISIBLE);
                    mFirstTodayMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfMonth(FORMAT_YEAR));
                } else {
                    if (mQuarterTime == 1) {
                        timeFilterBefore.setVisibility(View.INVISIBLE);
                    } else {
                        timeFilterBefore.setVisibility(View.VISIBLE);
                    }
                    timeToday.setText(String.format("%s第%s季度", (2015 + (mQuarterTime - 1) / 4), mQuarterTime % 4 == 0 ? 4 : mQuarterTime % 4));
                    timeFilterNext.setVisibility(View.VISIBLE);
                    timeFilterBefore.setText("上季");
                    timeFilterNext.setText("下季");
                }
                mStartDate = getQuarterStartOrEnd(mQuarterTime, false);
                mEndDate = getQuarterStartOrEnd(mQuarterTime, true);
                break;
            case 4://年
                if (isBefore) {
                    mYearTime -= 1;
                } else {
                    mYearTime += 1;
                }
                timeFilterToday.setVisibility(View.GONE);
                timeToday.setVisibility(View.VISIBLE);
                if (mYearTime == DateUtil.getCurrentYear()) {
                    timeToday.setText("今年");
                    timeFilterBefore.setText(String.valueOf(DateUtil.getCurrentYear() - 1));
                    timeFilterNext.setVisibility(View.INVISIBLE);
                    mStartDate = String.format("%s-12-31", mYearTime);
                    mEndDate = DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_YEAR);
                } else {
                    if (mYearTime == 2015) {
                        timeFilterBefore.setVisibility(View.INVISIBLE);
                    } else {
                        timeFilterBefore.setVisibility(View.VISIBLE);
                    }
                    timeFilterNext.setVisibility(View.VISIBLE);
                    timeToday.setText(String.valueOf(mYearTime));
                    timeFilterNext.setText(String.valueOf(mYearTime + 1));
                    timeFilterBefore.setText(String.valueOf(mYearTime - 1));
                    mStartDate = String.format("%s-01-01", mYearTime);
                    mEndDate = String.format("%s-12-31", mYearTime);
                }
                break;
            case 5:

                break;
        }
        timeChangedPost(mStartDate, mEndDate);
    }

    private void refreshView(int range) {
        switch (range) {
            case 0:

                break;
            case 1:

                break;
            case 2:

                break;
        }
    }

    private void getTime(int type) {
        switch (type) {
            case 0:
                timeFilterToday.setVisibility(View.VISIBLE);
                timeToday.setText("今天");
                timeFilterToday.setText(DateUtil.getCurrentDate(mCurrentMillisecond));
                timeFilterBefore.setText(DateUtil.getCurrentDate(mCurrentMillisecond - DAY_MILLISECOND));
                timeFilterNext.setVisibility(View.INVISIBLE);
                mFirstTodayMillisecond = mCurrentMillisecond;
                mStartDate = DateUtil.getCurrentDate(mFirstTodayMillisecond, FORMAT_YEAR);
                mEndDate = mStartDate;
                break;
            case 1:
                mFirstWeekMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfWeek(new Date(), "yyyy-MM-dd"));
                timeToday.setText("本周");
                timeToday.setVisibility(View.VISIBLE);
                timeFilterToday.setText(String.format("%s-%s", DateUtil.getFirstDayOfWeek(new Date(), "MM月dd日"), DateUtil.getLastDayOfWeek(new Date(), "MM月dd日")));
                timeFilterBefore.setText("上周");
                timeFilterNext.setVisibility(View.INVISIBLE);
                mStartDate = DateUtil.getFirstDayOfWeek(new Date(), FORMAT_YEAR);
                mEndDate = DateUtil.getLastDayOfWeek(new Date(), FORMAT_YEAR);
                break;
            case 2:
                mFirstMonthMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfMonth());
                timeToday.setText("本月");
                timeToday.setVisibility(View.VISIBLE);
                timeFilterToday.setText(String.format("%s-%s", DateUtil.getFirstDayOfMonth(FORMAT_MONTH), DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_MONTH)));
                timeFilterBefore.setText("上月");
                timeFilterNext.setVisibility(View.INVISIBLE);
                mStartDate = DateUtil.getFirstDayOfMonth(FORMAT_YEAR);
                mEndDate = DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_YEAR);
                break;
            case 3:
                mQuarterTime = (DateUtil.getCurrentYear() - 2015) * 4 + DateUtil.getCurrentQuarter();
                timeToday.setText("本季");
                timeToday.setVisibility(View.VISIBLE);
                timeFilterToday.setText(String.format("%s第%s季度", DateUtil.getCurrentYear(), DateUtil.getCurrentQuarter()));
                timeFilterBefore.setText("上季");
                timeFilterNext.setVisibility(View.INVISIBLE);
                mFirstTodayMillisecond = DateUtil.stringDateToLong(DateUtil.getFirstDayOfMonth(FORMAT_YEAR));
                mStartDate = getQuarterStartOrEnd(mQuarterTime, false);
                mEndDate = getQuarterStartOrEnd(mQuarterTime, true);
                break;
            case 4:
                mYearTime = DateUtil.getCurrentYear();
                timeToday.setText("今年");
                timeToday.setVisibility(View.VISIBLE);
                timeFilterBefore.setText(String.valueOf(DateUtil.getCurrentYear() - 1));
                timeFilterNext.setVisibility(View.INVISIBLE);
                timeFilterToday.setVisibility(View.GONE);
                mStartDate = String.format("%s-01-01", mYearTime);
                mEndDate = DateUtil.getCurrentDate(mCurrentMillisecond, FORMAT_YEAR);
                break;
            case 5:
                mStartDate = "2015-01-01";
                mEndDate = DateUtil.getDate(System.currentTimeMillis());
                break;
            default:
                mStartDate = "2015-01-01";
                mEndDate = DateUtil.getDate(System.currentTimeMillis());
                break;

        }
        timeChangedPost(mStartDate, mEndDate);

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
        emptyView.setStatus(EmptyView.Status.Loading);
        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                emptyView.setStatus(EmptyView.Status.Gone);
                RxBus.getInstance().post(new DateChangedResult(str, end));
            }
        }, 1000);


    }

}
