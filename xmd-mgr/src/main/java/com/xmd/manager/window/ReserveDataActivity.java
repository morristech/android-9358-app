package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.app.widget.StationaryScrollView;
import com.xmd.manager.R;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.event.DateChangedEvent;
import com.xmd.manager.service.RequestConstant;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-9-18.
 */

public class ReserveDataActivity extends BaseActivity {


    public static final int TIME_FILTER_TYPE_DAY = 0; //昨天
    public static final int TIME_FILTER_TYPE_WEEK = 1; //周
    public static final int TIME_FILTER_TYPE_MONTH = 2; //月
    public static final int TIME_FILTER_TYPE_TOTAL = 3; //累计
    public static final int TIME_FILTER_TYPE_CUSTOMIZED = 4; //自定义


    @BindView(R.id.tv_table_yesterday)
    TextView tvTableYesterday;
    @BindView(R.id.table_yesterday_line)
    View tableYesterdayLine;
    @BindView(R.id.tv_table_week)
    TextView tvTableWeek;
    @BindView(R.id.table_week_line)
    View tableWeekLine;
    @BindView(R.id.tv_table_month)
    TextView tvTableMonth;
    @BindView(R.id.table_month_line)
    View tableMonthLine;
    @BindView(R.id.tv_table_total)
    TextView tvTableTotal;
    @BindView(R.id.table_total_line)
    View tableTotalLine;
    @BindView(R.id.img_table_customized)
    ImageView imgTableCustomized;
    @BindView(R.id.tv_table_customized)
    TextView tvTableCustomized;
    @BindView(R.id.table_customized_line)
    View tableCustomizedLine;
    @BindView(R.id.fragment_time_filter)
    FrameLayout fragmentTimeFilter;
    @BindView(R.id.fragment_summary_data)
    FrameLayout fragmentSummaryData;
    @BindView(R.id.fragment_staff_data)
    FrameLayout fragmentStaffData;
    @BindView(R.id.stationary_scroll_view)
    StationaryScrollView stationaryScrollView;


    private String mStartTime;
    private String mEndTime;
    private List<View> tableLines;
    private List<TextView> tableTextViews;
    private TimeFilterFragment mTimeFilterFragment;
    private SummaryDataFragment mSummaryDataFragment;
    private StaffDataFragment mStaffDataFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_data);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mStartTime = DateUtil.getFirstDayOfMonth();
        mEndTime = DateUtil.getCurrentDate();
        setTitle(ResourceUtils.getString(R.string.reserve_data_activity_title));
        setRightVisible(true, ResourceUtils.getString(R.string.reserve_data_order), orderClickedListener);
        initTableView();
        initTimeFilterFragment();
        initSummaryDataFragment();
        initStaffDataFragment();
        final View view = stationaryScrollView;
        view.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams containerParams = stationaryScrollView.getLayoutParams();
                containerParams.height = stationaryScrollView.getHeight();
                fragmentStaffData.setLayoutParams(containerParams);
            }
        });
    }

    private void initTimeFilterFragment() {
        mTimeFilterFragment = new TimeFilterFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_time_filter, mTimeFilterFragment);
        ft.commit();
    }

    private void initSummaryDataFragment() {
        mSummaryDataFragment = new SummaryDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RequestConstant.KEY_START_DATE, mStartTime);
        bundle.putString(RequestConstant.KEY_END_DATE, mEndTime);
        mSummaryDataFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_summary_data, mSummaryDataFragment);
        ft.commit();
    }

    private void initStaffDataFragment() {
        mStaffDataFragment = new StaffDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RequestConstant.KEY_START_DATE, mStartTime);
        bundle.putString(RequestConstant.KEY_END_DATE, mEndTime);
        mStaffDataFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_staff_data, mStaffDataFragment);
        ft.commit();
    }

    private void initTableView() {
        tableLines = new ArrayList<>();
        tableLines.add(tableYesterdayLine);
        tableLines.add(tableWeekLine);
        tableLines.add(tableMonthLine);
        tableLines.add(tableTotalLine);
        tableLines.add(tableCustomizedLine);
        tableTextViews = new ArrayList<>();
        tableTextViews.add(tvTableYesterday);
        tableTextViews.add(tvTableWeek);
        tableTextViews.add(tvTableMonth);
        tableTextViews.add(tvTableTotal);
        tableTextViews.add(tvTableCustomized);
        setViewState(tableMonthLine, tvTableMonth);
    }


    @OnClick({R.id.ll_table_yesterday, R.id.ll_table_week, R.id.ll_table_month, R.id.ll_table_total, R.id.ll_table_customized})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_table_yesterday:
                mTimeFilterFragment.setCurrentTimeFilterType(TIME_FILTER_TYPE_DAY);
                setViewState(tableYesterdayLine, tvTableYesterday);
                break;
            case R.id.ll_table_week:
                mTimeFilterFragment.setCurrentTimeFilterType(TIME_FILTER_TYPE_WEEK);
                setViewState(tableWeekLine, tvTableWeek);
                break;
            case R.id.ll_table_month:
                mTimeFilterFragment.setCurrentTimeFilterType(TIME_FILTER_TYPE_MONTH);
                setViewState(tableMonthLine, tvTableMonth);
                break;
            case R.id.ll_table_total:
                mTimeFilterFragment.setCurrentTimeFilterType(TIME_FILTER_TYPE_TOTAL);
                setViewState(tableTotalLine, tvTableTotal);
                break;
            case R.id.ll_table_customized:
                mTimeFilterFragment.setCurrentTimeFilterType(TIME_FILTER_TYPE_CUSTOMIZED);
                setViewState(tableCustomizedLine, tvTableCustomized);
                break;
        }
    }

    private void setViewState(View lineView, TextView textView) {
        for (View view : tableLines) {
            view.setBackgroundColor(0xFFFFFFFF);
        }
        for (TextView tv : tableTextViews) {
            tv.setSelected(false);
        }
        lineView.setBackgroundColor(0xFFFF6666);
        textView.setSelected(true);
        if (textView.getId() == R.id.tv_table_customized) {
            imgTableCustomized.setSelected(true);
        } else {
            imgTableCustomized.setSelected(false);
        }
    }

    private View.OnClickListener orderClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //右上角进入订单页面
            OnlineOrderActivity.startOnlineOrderActivity(ReserveDataActivity.this, mStartTime, mEndTime, "");
        }
    };

    @Subscribe
    public void DateChangedSubscribe(DateChangedEvent event) {
        mStartTime = event.startTime;
        mEndTime = event.endTime;
        mStaffDataFragment.setDateChanged(event.startTime, event.endTime);
        mSummaryDataFragment.onRefreshDate(event.startTime, event.endTime);
    }
}
