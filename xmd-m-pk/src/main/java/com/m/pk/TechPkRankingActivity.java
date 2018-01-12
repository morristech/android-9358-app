package com.m.pk;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.m.pk.bean.PkItemBean;
import com.m.pk.databinding.ActivityTechPkRankingBinding;
import com.m.pk.event.DateChangedEvent;
import com.xmd.app.BaseActivity;
import com.xmd.app.PageFragmentAdapter;
import com.xmd.app.utils.DateUtil;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.widget.ViewPagerTabIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 18-1-9.
 */

public class TechPkRankingActivity extends BaseActivity {

    private final long DAY_MILLISECOND = 24 * 3600 * 1000;//24小时毫秒值
    private ActivityTechPkRankingBinding mBinding;
    private PageFragmentAdapter mPageFragmentAdapter;
    private long mCurrentMillisecond;  //当前显示日期毫秒日
    private long mTodayMillisecond;   //当天毫秒值
    private long mStartMillisecond;
    private String mCurrentTime;//MM月dd日，显示中间时间
    private String mLastTime;//MM月dd日，显示前一天时间
    private String mNextTime;//MM月dd日，显示后一天时间
    private String mActivityId;//当前活动Id
    private String mActivityStatus;//当前活动状态
    private String mStartDate;//当前查询开始时间
    private String mEndDate;//当前查询结束时间
    private String mActivityStartDate;//当前活动开始时间
    private String mActivityEndDate;//当前活动结束时间
    private List<PkItemBean> itemList;
    public ObservableBoolean mIsOnline = new ObservableBoolean();
    public ObservableBoolean sShowTomorrow = new ObservableBoolean();
    public ObservableBoolean sShowToday = new ObservableBoolean();
    public ObservableBoolean sShowTotal = new ObservableBoolean();
    public ObservableBoolean sShowYesterday = new ObservableBoolean();
    public ObservableField<String> mToday = new ObservableField<>();
    public ObservableField<String> mTodayDetail = new ObservableField<>();
    public ObservableField<String> mTomorrow = new ObservableField<>();
    public ObservableField<String> mYesterday = new ObservableField<>();
    public ObservableField<String> mTimeType = new ObservableField<>();

    public static void startTechPkRankingActivity(Activity activity, String itemId, String itemStatus, String startDate, String endDate,
                                                  String appType, List<PkItemBean> pkList) {
        Intent intent = new Intent(activity, TechPkRankingActivity.class);
        intent.putExtra(Constant.PK_ITEM_ID, itemId);
        intent.putExtra(Constant.PK_ITEM_STATUS, itemStatus);
        intent.putExtra(Constant.PK_ITEM_START_DATE, startDate);
        intent.putExtra(Constant.PK_ITEM_END_DATE, endDate);
        intent.putExtra(Constant.INTENT_KEY_APP_TYPE, appType);
        intent.putParcelableArrayListExtra(Constant.PK_ACTIVITY_ITEM, (ArrayList<? extends Parcelable>) pkList);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tech_pk_ranking);
        mBinding.setVariable(com.m.pk.BR.rankingData, this);
        setStatusBarColor();
        getIntentData();
        initViewPagerView();
        initTimeFilterView();
    }

    public void getIntentData() {
        mActivityId = getIntent().getStringExtra(Constant.PK_ITEM_ID);
        mActivityStatus = getIntent().getStringExtra(Constant.PK_ITEM_STATUS);
        mActivityStartDate = getIntent().getStringExtra(Constant.PK_ITEM_START_DATE);
        mActivityEndDate = getIntent().getStringExtra(Constant.PK_ITEM_END_DATE);
        itemList = getIntent().getParcelableArrayListExtra(Constant.PK_ACTIVITY_ITEM);
        mIsOnline.set(mActivityStatus.equals(Constant.PK_RANKING_STATUS_ONLINE) ? true : false);
        mTimeType.set(ResourceUtils.getString(R.string.date_type_total));
        sShowTotal.set(false);
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#F98263"));
        }
    }


    private void initViewPagerView() {
        String[] tabTexts = new String[itemList.size()];
        mPageFragmentAdapter = new PageFragmentAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < itemList.size(); i++) {
            tabTexts[i] = itemList.get(i).value;
            Bundle args = new Bundle();
            args.putSerializable(TechPKRankingListFragment.BIZ_TYPE, itemList.get(i).key);
            args.putSerializable(TechPKRankingListFragment.PK_ACTIVITY_ID, mActivityId);
            mPageFragmentAdapter.addFragment(TechPKRankingListFragment.class.getName(), args);
        }
        mBinding.viewPager.setOffscreenPageLimit(itemList.size());
        mBinding.viewPager.setAdapter(mPageFragmentAdapter);
        mBinding.tabIndicator.setTabTexts(tabTexts);
        mBinding.tabIndicator.setWithIndicator(true);
        mBinding.tabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        mBinding.tabIndicator.setViewPager(mBinding.viewPager);
        mBinding.tabIndicator.setWithDivider(false);
        mBinding.tabIndicator.setup();

    }

    public void timeSwitchFilter(View view) {
        PopupMenu menu = new PopupMenu(this, mBinding.rlPkTimeFilter);
        menu.inflate(R.menu.date_filter);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_time_by_day) {
                    sShowTotal.set(false);
                    mTimeType.set(ResourceUtils.getString(R.string.date_type_day));
                } else {
                    sShowTotal.set(true);
                    mTimeType.set(ResourceUtils.getString(R.string.date_type_total));
                    mStartDate = Constant.PK_RANKING_DEFAULT_START_TIME;
                    mEndDate = DateUtil.getDate(System.currentTimeMillis());
                    postTimeChanged();
                }
                return true;
            }
        });
        menu.show();
    }

    public void finishSelf(View v) {
        this.finish();
    }

    public void timeYesterdayClicked(View v) {
        getTime(false);
    }

    public void timeTomorrowClicked(View v) {
        getTime(true);
    }

    private void getTime(boolean isTomorrow) {
        if (isTomorrow) {
            mCurrentMillisecond += DAY_MILLISECOND;
        } else {
            mCurrentMillisecond -= DAY_MILLISECOND;
        }
        mCurrentTime = DateUtil.getCurrentDate(mCurrentMillisecond);
        mLastTime = DateUtil.getLastDate(mCurrentMillisecond);
        mNextTime = DateUtil.getNextDate(mCurrentMillisecond);
        mToday.set(mCurrentTime);
        mTomorrow.set(mNextTime);
        mYesterday.set(mLastTime);
        if (mActivityStatus.equals(Constant.PK_RANKING_STATUS_ONLINE)) {//进行中
            if (mTodayMillisecond == mCurrentMillisecond) {
                mToday.set(ResourceUtils.getString(R.string.current_date_type_day));
                sShowToday.set(true);
                mTodayDetail.set(mCurrentTime);
                sShowTomorrow.set(false);
            } else if (mActivityStartDate.equals(DateUtil.getCurrentDate(mCurrentMillisecond, Constant.FORMAT_YEAR))) {
                mToday.set(ResourceUtils.getString(R.string.current_date_type_first_day));
                sShowToday.set(true);
                sShowYesterday.set(false);
            } else {
                mToday.set(mCurrentTime);
                sShowTomorrow.set(true);
                sShowYesterday.set(true);
                sShowTotal.set(false);
            }
        } else {//已结束
            if (mActivityStartDate.equals(mActivityEndDate)) {//只有一天
                mToday.set(mCurrentTime);
                sShowToday.set(false);
                sShowYesterday.set(false);
                sShowTomorrow.set(false);
            } else {
                if (mCurrentMillisecond == mTodayMillisecond) {
                    mToday.set(mCurrentTime);
                    sShowTomorrow.set(false);
                    sShowYesterday.set(true);
                    sShowToday.set(false);
                } else if (mCurrentMillisecond == mStartMillisecond) {
                    mToday.set(ResourceUtils.getString(R.string.current_date_type_first_day));
                    sShowToday.set(true);
                    sShowYesterday.set(false);
                    sShowTomorrow.set(true);
                } else {
                    mToday.set(mCurrentTime);
                    sShowToday.set(false);
                    sShowYesterday.set(true);
                    sShowTomorrow.set(true);
                }
            }
        }
        mStartDate = DateUtil.getDate(mCurrentMillisecond);
        mEndDate = DateUtil.getDate(mCurrentMillisecond);
        postTimeChanged();
    }

    private void postTimeChanged() {
        EventBus.getDefault().post(new DateChangedEvent(mStartDate, mEndDate));
    }

    private void initTimeFilterView() {
        if (mActivityStatus.equals(Constant.PK_RANKING_STATUS_ONLINE)) {
            mCurrentMillisecond = System.currentTimeMillis();
            mToday.set(ResourceUtils.getString(R.string.current_date_type_day));
            sShowTomorrow.set(false);
            sShowToday.set(true);
            mYesterday.set(DateUtil.getCurrentDate(mCurrentMillisecond - DAY_MILLISECOND));
            mTodayDetail.set(DateUtil.getCurrentDate(mCurrentMillisecond));
            if (mActivityStartDate.equals(DateUtil.getCurrentDate(mCurrentMillisecond, Constant.FORMAT_YEAR))) {
                sShowYesterday.set(false);
            } else {
                sShowYesterday.set(true);
            }
            mStartDate = DateUtil.getCurrentDate(mCurrentMillisecond, Constant.FORMAT_YEAR);
            mEndDate = mStartDate;
        } else {
            mCurrentMillisecond = DateUtil.dateToLong(mActivityEndDate);
            if (mActivityStartDate.equals(mActivityEndDate)) {
                mToday.set(DateUtil.getCurrentDate(mCurrentMillisecond));
                sShowTomorrow.set(false);
                sShowYesterday.set(false);
                sShowToday.set(false);
            } else {
                mToday.set(DateUtil.getCurrentDate(mCurrentMillisecond));
                sShowTomorrow.set(false);
                sShowYesterday.set(true);
                sShowTotal.set(false);
                mYesterday.set(DateUtil.getCurrentDate(mCurrentMillisecond - DAY_MILLISECOND));
            }
            mStartDate = mActivityEndDate;
            mEndDate = mActivityEndDate;
        }
        mStartMillisecond = DateUtil.dateToLong(mActivityStartDate);
        mTodayMillisecond = mCurrentMillisecond;
    }
}
