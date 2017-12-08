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
 * 买单收银报表
 */

public class CashierReportActivity extends BaseActivity {
    @BindView(R.id.tab_indicator_cashier)
    ViewPagerTabIndicator mTabIndicator;
    @BindView(R.id.vp_cashier)
    ViewPager mPager;

    private PageFragmentPagerAdapter mPageFragmentAdapter;
    private String[] mTabTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_report);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.cashier_title));
        setRightVisible(true, R.drawable.ic_report_intr, null);

        mTabTexts = new String[]{ResourceUtils.getString(R.string.report_tab_day), ResourceUtils.getString(R.string.report_tab_month), ResourceUtils.getString(R.string.report_tab_user)};
        mPageFragmentAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), CashierReportActivity.this);
        mPageFragmentAdapter.addFragment(new CashierReportByDayFragment());
        mPageFragmentAdapter.addFragment(new CashierReportByMonthFragment());
        mPageFragmentAdapter.addFragment(new CashierReportByUserFragment());
        mPager.setAdapter(mPageFragmentAdapter);
        mPager.setOffscreenPageLimit(2);
        mTabIndicator.setTabTexts(mTabTexts);
        mTabIndicator.setWithIndicator(false);
        mTabIndicator.setTextSize(Utils.dip2px(CashierReportActivity.this, 16));
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
        Intent intent = new Intent(CashierReportActivity.this, ReportExplainDialogActivity.class);
        intent.putExtra(ReportExplainDialogActivity.EXTRA_REPORT_TYPE, ReportExplainDialogActivity.REPORT_TYPE_CASHIER);
        startActivity(intent);
    }
}
