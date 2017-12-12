package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xmd.app.PageFragmentAdapter;
import com.xmd.app.widget.ViewPagerTabIndicator;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;

/**
 * Created by zr on 17-12-11.
 */

public class ItemStatisticsActivity extends BaseActivity {
    private String[] STATISTICS_FILTER_TIME = new String[]{"日", "月", "自定义"};

    private ViewPagerTabIndicator mIndicator;
    private ViewPager mPager;
    private PageFragmentAdapter mPageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_statistics);
        showToolbar(R.id.toolbar, "项目汇总");

        mIndicator = (ViewPagerTabIndicator) findViewById(R.id.tab_indicator);
        mPager = (ViewPager) findViewById(R.id.vp_fragment);

        mPageAdapter = new PageFragmentAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < STATISTICS_FILTER_TIME.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(AppConstants.EXTRA_BIZ_TYPE, i);
            mPageAdapter.addFragment(ItemStatisticsDetailFragment.class.getName(), args);
        }
        mPager.setAdapter(mPageAdapter);
        mPager.setOffscreenPageLimit(2);
        mIndicator.setTextColor(R.color.selector_text_tab_indicator);
        mIndicator.setPaintColor(R.color.colorAccent);
        mIndicator.setWithDivider(false);
        mIndicator.setTabTexts(STATISTICS_FILTER_TIME);
        mIndicator.setDrawbleDirection(ViewPagerTabIndicator.DRAWABLE_TOP);
        mIndicator.setWithIndicator(true);
        mIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        mIndicator.setViewPager(mPager);
        mIndicator.setup();
    }

    public void onStatisticsExplain(View view) {
        UiNavigation.gotoItemStatisticsExplainActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
