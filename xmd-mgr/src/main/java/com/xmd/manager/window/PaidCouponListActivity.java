package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.xmd.manager.R;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-11-13.
 */

public class PaidCouponListActivity extends BaseActivity {
    @BindView(R.id.coupon_view_pager_indicator)
    ViewPagerTabIndicator couponViewPagerIndicator;
    @BindView(R.id.coupon_view_pager)
    ViewPager couponViewPager;

    private PageFragmentPagerAdapter mPageFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_coupon);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.club_paid_coupon));
        initViewPagerView();
    }

    private void initViewPagerView() {
        String[] tabTexts = new String[]{ResourceUtils.getString(R.string.coupon_operate_usable), ResourceUtils.getString(R.string.coupon_operate_unusable)};
        mPageFragmentAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), PaidCouponListActivity.this);
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(PaidCouponListFragment.COUPON_LIST_STATUS_TYPE, i == 0 ? PaidCouponListFragment.COUPON_LIST_TYPE_ONLINE : PaidCouponListFragment.COUPON_LIST_TYPE_OFFLINE);
            mPageFragmentAdapter.addFragment(PaidCouponListFragment.class.getName(), args);
        }
        couponViewPager.setOffscreenPageLimit(2);
        couponViewPager.setAdapter(mPageFragmentAdapter);
        couponViewPagerIndicator.setTabTexts(tabTexts);
        couponViewPagerIndicator.setWithIndicator(true);
        couponViewPagerIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        couponViewPagerIndicator.setViewPager(couponViewPager);
        couponViewPagerIndicator.setWithDivider(false);
        couponViewPagerIndicator.setTextSize(42);
        couponViewPagerIndicator.setup();
    }
}
