package com.m.pk;



import com.xmd.app.BaseActivity;

/**
 * Created by Lhj on 17-4-7.
 */

public class TechPKRankingListDetailActivity extends BaseActivity {

//    private final long DAY_MILLISECOND = 24 * 3600 * 1000;//24小时毫秒值
//    @BindView(R.id.toolbar_back)
//    ImageView toolbarBack;
//    @BindView(R.id.toolbar_title)
//    TextView toolbarTitle;
//    @BindView(R.id.pk_detail_time_filter)
//    TextView pkDetailTimeFilter;
//    @BindView(R.id.rl_toolbar)
//    RelativeLayout rlToolbar;
//    @BindView(R.id.pk_active_status)
//    TextView pkActiveStatus;
//    @BindView(R.id.time_filter_yesterday)
//    TextView timeFilterYesterday;
//    @BindView(R.id.time_filter_today)
//    TextView timeFilterToday;
//    @BindView(R.id.time_filter_tomorrow)
//    TextView timeFilterTomorrow;
//    @BindView(R.id.tabIndicator)
//    ViewPagerTabIndicator tabIndicator;
//    @BindView(R.id.viewPager)
//    ViewPager viewPager;
//    @BindView(R.id.ll_filter_selected_total)
//    LinearLayout llFilterSelectedTotal;
//    @BindView(R.id.rl_pk_detail_time)
//    RelativeLayout rlPkDetailTime;
//    @BindView(R.id.ll_filter_by_day)
//    LinearLayout llFilterByDay;
//    @BindView(R.id.time_today)
//    TextView timeToday;
//    @BindView(R.id.empty_view_widget)
//    EmptyView emptyView;
//    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
//    private ArrayBottomPopupWindow<String> mTimeFilterPopupWindow;
//    private long mCurrentMillisecond;  //当前显示日期毫秒日
//    private long mTodayMillisecond;   //当天毫秒值
//    private long mStartMillisecond;
//    private String mCurrentTime;//MM月dd日，显示中间时间
//    private String mLastTime;//MM月dd日，显示前一天时间
//    private String mNextTime;//MM月dd日，显示后一天时间
//    private String mActivityId;//当前活动Id
//    private String mActivityStatus;//当前活动状态
//    private String mStartDate;//当前查询开始时间
//    private String mEndDate;//当前查询结束时间
//    private String mActivityStartDate;//当前活动开始时间
//    private String mActivityEndDate;//当前活动结束时间
//    private List<PKItemBean> itemList;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tech_pk_ranking_detail);
//        ButterKnife.bind(this);
//        getIntentData();
//        initViewPagerView();
//        initTimeFilterPopupWindowView();
//    }
//
//    private void getIntentData() {
//        mActivityId = getIntent().getStringExtra(TechPKActiveActivity2.PK_ITEM_ID);
//        mActivityStatus = getIntent().getStringExtra(TechPKActiveActivity2.PK_ITEM_STATUS);
//        mActivityStartDate = getIntent().getStringExtra(TechPKActiveActivity2.PK_ITEM_START_DATE);
//        mActivityEndDate = getIntent().getStringExtra(TechPKActiveActivity2.PK_ITEM_END_DATE);
//        itemList = getIntent().getParcelableArrayListExtra(TechPKActiveActivity2.PK_ACTIVITY_ITEM);
//
//        if (mActivityStatus.equals("4")) {
//            pkActiveStatus.setText("进行中");
//            pkActiveStatus.setEnabled(true);
//            Drawable leftDrawable = getResources().getDrawable(R.drawable.icon_underway);
//            leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
//            pkActiveStatus.setCompoundDrawables(leftDrawable, null, null, null);
//        } else {
//            pkActiveStatus.setText("已结束");
//            pkActiveStatus.setEnabled(false);
//            Drawable leftDrawable = getResources().getDrawable(R.drawable.icon_completed);
//            leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
//            pkActiveStatus.setCompoundDrawables(leftDrawable, null, null, null);
//        }
//        initTimeFilterView(mActivityStatus);
//
//    }
//
//    private void initTimeFilterView(String activityStatus) {
//        if (activityStatus.equals("4")) {
//            mCurrentMillisecond = System.currentTimeMillis();
//            timeToday.setText("今天");
//            timeFilterTomorrow.setVisibility(View.INVISIBLE);
//            timeFilterToday.setVisibility(View.VISIBLE);
//            timeFilterYesterday.setText(DateUtil.getCurrentDate(mCurrentMillisecond - DAY_MILLISECOND));
//            timeFilterToday.setText(DateUtil.getCurrentDate(mCurrentMillisecond));
//            if (mActivityStartDate.equals(DateUtil.getCurrentDate(mCurrentMillisecond, "yyyy-MM-dd"))) {
//                timeFilterYesterday.setVisibility(View.INVISIBLE);
//            } else {
//                timeFilterYesterday.setVisibility(View.VISIBLE);
//            }
//            mStartDate = DateUtil.getCurrentDate(mCurrentMillisecond, "yyyy-MM-dd");
//            mEndDate = mStartDate;
//        } else {
//            mCurrentMillisecond = DateUtil.dateToLong(mActivityEndDate);
//            if (mActivityStartDate.equals(mActivityEndDate)) {
//                timeToday.setText(DateUtil.getCurrentDate(mCurrentMillisecond));
//                timeFilterTomorrow.setVisibility(View.INVISIBLE);
//                timeFilterYesterday.setVisibility(View.INVISIBLE);
//                timeFilterToday.setVisibility(View.GONE);
//            } else {
//                timeToday.setText(DateUtil.getCurrentDate(mCurrentMillisecond));
//                timeFilterYesterday.setText(DateUtil.getCurrentDate(mCurrentMillisecond - DAY_MILLISECOND));
//                timeFilterTomorrow.setVisibility(View.INVISIBLE);
//                timeFilterYesterday.setVisibility(View.VISIBLE);
//                timeFilterToday.setVisibility(View.GONE);
//
//            }
//            mStartDate = mActivityEndDate;
//            mEndDate = mActivityEndDate;
//        }
//        mStartMillisecond = DateUtil.dateToLong(mActivityStartDate);
//        mTodayMillisecond = mCurrentMillisecond;
//    }
//
//
//    private void initViewPagerView() {
//        String[] tabTexts = new String[itemList.size()];
//        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), this);
//        for (int i = 0; i < itemList.size(); i++) {
//            tabTexts[i] = itemList.get(i).value;
//            Bundle args = new Bundle();
//            args.putSerializable(TechPkDetailFragment.BIZ_TYPE, itemList.get(i).key);
//            args.putSerializable(TechPkDetailFragment.PK_ACTIVITY_ID, mActivityId);
//            mPageFragmentPagerAdapter.addFragment(TechPkDetailFragment.class.getName(), args);
//        }
//        viewPager.setOffscreenPageLimit(itemList.size());
//        viewPager.setAdapter(mPageFragmentPagerAdapter);
//        tabIndicator.setTabTexts(tabTexts);
//        tabIndicator.setTabTextSize(16);
//        tabIndicator.setWithIndicator(true);
//        tabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
//        tabIndicator.setViewPager(viewPager);
//        tabIndicator.setWithDivider(false);
//        tabIndicator.setup();
//      //  tabIndicator.setOnPageChangeListener(position -> refreshView(position));
//      //  tabIndicator.setOnTabclickListener(position -> refreshView(position));
//    }
//
//    private void initTimeFilterPopupWindowView() {
//        pkDetailTimeFilter.setText("累计");
//        mTimeFilterPopupWindow = new ArrayBottomPopupWindow<>(rlPkDetailTime, null, ResourceUtils.getDimenInt(R.dimen.time_filter_item_width));
//        mTimeFilterPopupWindow.setDataSet(new ArrayList<>(Constant.TIME_FILTER_LABELS.keySet()), pkDetailTimeFilter.getText().toString());
//        mTimeFilterPopupWindow.setItemClickListener((parent, view, position, id) -> {
//            String sTitle = (String) parent.getAdapter().getItem(position);
//            pkDetailTimeFilter.setText(sTitle);
//            mTimeFilterPopupWindow.setDataSet(new ArrayList<>(Constant.TIME_FILTER_LABELS.keySet()), pkDetailTimeFilter.getText().toString());
//            if (sTitle.equals("累计")) {
//                llFilterSelectedTotal.setVisibility(View.VISIBLE);
//                llFilterByDay.setVisibility(View.GONE);
//                mStartDate = "2017-03-30";
//                mEndDate = DateUtil.getDate(System.currentTimeMillis());
//                RxBus.getInstance().post(new DateChangedResult("2017-03-30", DateUtil.getDate(System.currentTimeMillis())));
//            } else {
//                initTimeFilterView(mActivityStatus);
//                RxBus.getInstance().post(new DateChangedResult(mStartDate, mEndDate));
//                llFilterSelectedTotal.setVisibility(View.GONE);
//                llFilterByDay.setVisibility(View.VISIBLE);
//            }
//        });
//    }
//
//    @OnClick({R.id.toolbar_back, R.id.rl_pk_detail_time, R.id.time_filter_yesterday, R.id.time_filter_tomorrow})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.toolbar_back:
//                this.finish();
//                break;
//            case R.id.rl_pk_detail_time:
//                mTimeFilterPopupWindow.showAsDownCenter(true);
//                break;
//            case R.id.time_filter_yesterday:
//                getTime(false);
//                break;
//            case R.id.time_filter_tomorrow:
//                getTime(true);
//                break;
//        }
//    }
//
//    private void refreshView(int range) {
//        switch (range) {
//            case 0:
//
//                break;
//            case 1:
//
//                break;
//            case 2:
//
//                break;
//        }
//    }
//
//    private void getTime(boolean isTomorrow) {
//        if (isTomorrow) {
//            mCurrentMillisecond += DAY_MILLISECOND;
//        } else {
//            mCurrentMillisecond -= DAY_MILLISECOND;
//        }
//        mCurrentTime = DateUtil.getCurrentDate(mCurrentMillisecond);
//        mLastTime = DateUtil.getLastDate(mCurrentMillisecond);
//        mNextTime = DateUtil.getNextDate(mCurrentMillisecond);
//        timeFilterToday.setText(mCurrentTime);
//        timeFilterTomorrow.setText(mNextTime);
//        timeFilterYesterday.setText(mLastTime);
//        if (mActivityStatus.equals("4")) {//进行中
//            if (mTodayMillisecond == mCurrentMillisecond) {
//                timeToday.setText("今天");
//                timeFilterToday.setVisibility(View.VISIBLE);
//                timeFilterToday.setText(mCurrentTime);
//                timeFilterTomorrow.setVisibility(View.INVISIBLE);
//            } else if (mActivityStartDate.equals(DateUtil.getCurrentDate(mCurrentMillisecond, "yyyy-MM-dd"))) {
//                timeToday.setText("第一天");
//                timeFilterToday.setVisibility(View.VISIBLE);
//                timeFilterYesterday.setVisibility(View.INVISIBLE);
//            } else {
//                timeToday.setText(mCurrentTime);
//                timeFilterTomorrow.setVisibility(View.VISIBLE);
//                timeFilterYesterday.setVisibility(View.VISIBLE);
//                timeFilterToday.setVisibility(View.GONE);
//            }
//        } else {//已结束
//            if (mActivityStartDate.equals(mActivityEndDate)) {//只有一天
//                timeToday.setText(mCurrentTime);
//                timeFilterToday.setVisibility(View.GONE);
//                timeFilterYesterday.setVisibility(View.INVISIBLE);
//                timeFilterTomorrow.setVisibility(View.INVISIBLE);
//            } else {
//                if (mCurrentMillisecond == mTodayMillisecond) {
//                    timeToday.setText(mCurrentTime);
//                    timeFilterTomorrow.setVisibility(View.INVISIBLE);
//                    timeFilterYesterday.setVisibility(View.VISIBLE);
//                    timeFilterToday.setVisibility(View.GONE);
//                } else if (mCurrentMillisecond == mStartMillisecond) {
//                    timeToday.setText("第一天");
//                    timeFilterToday.setVisibility(View.VISIBLE);
//                    timeFilterYesterday.setVisibility(View.INVISIBLE);
//                    timeFilterTomorrow.setVisibility(View.VISIBLE);
//                } else {
//                    timeToday.setText(mCurrentTime);
//                    timeFilterToday.setVisibility(View.GONE);
//                    timeFilterYesterday.setVisibility(View.VISIBLE);
//                    timeFilterTomorrow.setVisibility(View.VISIBLE);
//                }
//            }
//        }
//        mStartDate = DateUtil.getDate(mCurrentMillisecond);
//        mEndDate = DateUtil.getDate(mCurrentMillisecond);
//        emptyView.setStatus(EmptyView.Status.Loading);
//        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
//            @Override
//            public void run() {
//                emptyView.setStatus(EmptyView.Status.Gone);
//                RxBus.getInstance().post(new DateChangedResult(mStartDate, mEndDate));
//            }
//        }, 1000);
//    }


}
