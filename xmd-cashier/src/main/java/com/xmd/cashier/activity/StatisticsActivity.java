package com.xmd.cashier.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xmd.app.PageFragmentAdapter;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.widget.ViewPagerTabIndicator;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;

/**
 * Created by zr on 17-9-19.
 * 对账统计
 */

public class StatisticsActivity extends BaseActivity {
    public String[] STATISTICS_FILTER_TIME = new String[]{"天", "周", "月", "累计", "自定义"};
    public Drawable[] STATISTICS_FILTER_ICON = new Drawable[]{null, null, null, null, ResourceUtils.getDrawable(R.drawable.selector_tab_indicator)};

    private ViewPagerTabIndicator mIndicator;
    private ViewPager mPager;
    private PageFragmentAdapter mPageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        showToolbar(R.id.toolbar, "对账单");

        mIndicator = (ViewPagerTabIndicator) findViewById(R.id.tab_indicator);
        mPager = (ViewPager) findViewById(R.id.vp_fragment);

        mPageAdapter = new PageFragmentAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < STATISTICS_FILTER_TIME.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(AppConstants.EXTRA_BIZ_TYPE, i);
            mPageAdapter.addFragment(StatisticsDetailFragment.class.getName(), args);
        }
        mPager.setAdapter(mPageAdapter);
        mPager.setOffscreenPageLimit(4);
        mIndicator.setTextColor(R.color.selector_text_tab_indicator);
        mIndicator.setPaintColor(R.color.colorAccent);
        mIndicator.setWithDivider(false);
        mIndicator.setTabTexts(STATISTICS_FILTER_TIME);
        mIndicator.setTabIcons(STATISTICS_FILTER_ICON);
        mIndicator.setDrawbleDirection(ViewPagerTabIndicator.DRAWABLE_TOP);
        mIndicator.setWithIndicator(true);
        mIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        mIndicator.setViewPager(mPager);
        mIndicator.setup();
    }

    public void onStatisticsSetting(View view) {
        UiNavigation.gotoStatisticsSettingActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
