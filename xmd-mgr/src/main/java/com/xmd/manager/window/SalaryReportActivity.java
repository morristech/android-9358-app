package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.xmd.manager.R;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zr on 17-11-21.
 * 技师工资报表
 */

public class SalaryReportActivity extends BaseActivity {
    @BindView(R.id.tab_indicator_salary)
    ViewPagerTabIndicator mTabIndicator;
    @BindView(R.id.vp_salary)
    ViewPager mPager;

    private PageFragmentPagerAdapter mPageFragmentAdapter;
    private String[] mTabTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_report);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.salary_title));
        setRightVisible(true, R.drawable.ic_report_intr, null);

        mTabTexts = new String[]{ResourceUtils.getString(R.string.report_tab_day), ResourceUtils.getString(R.string.report_tab_month), ResourceUtils.getString(R.string.report_tab_user)};
        mPageFragmentAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), SalaryReportActivity.this);
        mPageFragmentAdapter.addFragment(new SalaryReportByDayFragment());
        mPageFragmentAdapter.addFragment(new SalaryReportByMonthFragment());
        mPageFragmentAdapter.addFragment(new SalaryReportByUserFragment());
        mPager.setAdapter(mPageFragmentAdapter);
        mPager.setOffscreenPageLimit(2);
        mTabIndicator.setTabTexts(mTabTexts);
        mTabIndicator.setWithIndicator(false);
        mTabIndicator.setTextSize(Utils.dip2px(SalaryReportActivity.this, 16));
        mTabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        mTabIndicator.setViewPager(mPager);
        mTabIndicator.setup();
        // 初始显示按月报表
        mPager.setCurrentItem(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.toolbar_right_image)
    public void onImageClick() {
        Intent intent = new Intent(SalaryReportActivity.this, ReportExplainDialogActivity.class);
        intent.putExtra(ReportExplainDialogActivity.EXTRA_REPORT_TYPE, ReportExplainDialogActivity.REPORT_TYPE_SALARY);
        startActivity(intent);
    }
}
