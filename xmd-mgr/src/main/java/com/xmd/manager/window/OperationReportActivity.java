package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.utils.Utils;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperationReportActivity extends BaseActivity {

    @BindView(R.id.tab_indicator)
    ViewPagerTabIndicator tabIndicator;
    @BindView(R.id.operation_view_pager)
    ViewPager operationViewPager;
    @BindView(R.id.toolbar_right_image)
    ImageView toolbarRightImage;

    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
    private String[] tabTexts;
    private String mOperateStartTime;
    private String mOperateEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_report);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.operate_title));
        setRightVisible(true, R.drawable.mainpage_imgleft_selected, null);
        tabTexts = new String[]{ResourceUtils.getString(R.string.operate_date_by_day), ResourceUtils.getString(R.string.operate_date_by_month), ResourceUtils.getString(R.string.operate_date_by_user)};
        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), OperationReportActivity.this);
        mPageFragmentPagerAdapter.addFragment(new OperateDateByDayFragment());
        mPageFragmentPagerAdapter.addFragment(new OperateDateByMonthFragment());
        mPageFragmentPagerAdapter.addFragment(new OperateDateByUserFragment());
        operationViewPager.setAdapter(mPageFragmentPagerAdapter);
        operationViewPager.setOffscreenPageLimit(3);
        tabIndicator.setTabTexts(tabTexts);
        tabIndicator.setWithIndicator(false);
        tabIndicator.setTextSize(Utils.dip2px(OperationReportActivity.this, 16));
        tabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        tabIndicator.setViewPager(operationViewPager);
        tabIndicator.setup();
    }


    @OnClick(R.id.toolbar_right_image)
    public void onViewClicked() {
        Intent intent = new Intent(OperationReportActivity.this, OperateCycleSettingActivity.class);
        intent.putExtra(Constant.OPERATE_START_TIME, mOperateStartTime);
        intent.putExtra(Constant.OPERATE_END_TIME, mOperateEndTime);
        startActivity(intent);
    }
}