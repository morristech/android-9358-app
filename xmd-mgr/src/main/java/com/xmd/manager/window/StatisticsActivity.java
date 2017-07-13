package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import butterknife.BindView;

/**
 * Created by linms@xiaomodo.com on 16-5-26.
 */
public class StatisticsActivity extends BaseActivity {

    @BindView(R.id.tab_indicator)
    ViewPagerTabIndicator mViewPagerTabIndicator;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        String[] tabTexts = new String[]{
                ResourceUtils.getString(R.string.today),
                ResourceUtils.getString(R.string.current_week),
                ResourceUtils.getString(R.string.current_month),
                ResourceUtils.getString(R.string.accumulate)};

        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(Constant.PARAM_RANGE, i);
            mPageFragmentPagerAdapter.addFragment(StatisticsFragment.class.getName(), args);
        }
        mViewPager.setAdapter(mPageFragmentPagerAdapter);

        mViewPagerTabIndicator.setTabTexts(tabTexts);
        mViewPagerTabIndicator.setWithIndicator(true);
        mViewPagerTabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        mViewPagerTabIndicator.setViewPager(mViewPager);
        mViewPagerTabIndicator.setup();
    }
}
