package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.event.CouponFilterEvent;
import com.xmd.manager.widget.ArrayBottomPopupWindow;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-10-19.
 * 营销->优惠券
 */

public class OperateCouponListActivity extends BaseActivity {

    @BindView(R.id.coupon_view_pager_indicator)
    ViewPagerTabIndicator couponViewPagerIndicator;
    @BindView(R.id.coupon_view_pager)
    ViewPager couponViewPager;
    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;

    private PageFragmentPagerAdapter mPageFragmentAdapter;
    private ArrayBottomPopupWindow<String> mTypeFilterPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_coupon_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        toolbarTitle.setText(ResourceUtils.getString(R.string.coupon_activity_title));
        initViewPagerView();
        mTypeFilterPopupWindow = new ArrayBottomPopupWindow<String>(toolbarRight, null, ResourceUtils.getDimenInt(R.dimen.time_filter_item_width), true);
        mTypeFilterPopupWindow.setDataSet(new ArrayList<>(Constant.COUPON_TYPE_FILTER_LABELS.keySet()), toolbarRight.getText().toString());
        mTypeFilterPopupWindow.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sTitle = (String) parent.getAdapter().getItem(position);
                toolbarRight.setText(sTitle);
                mTypeFilterPopupWindow.setDataSet(new ArrayList<>(Constant.COUPON_TYPE_FILTER_LABELS.keySet()), toolbarRight.getText().toString());
                EventBus.getDefault().post(new CouponFilterEvent(Constant.COUPON_TYPE_FILTER_LABELS.get(toolbarRight.getText().toString())));
            }
        });
    }

    private void initViewPagerView() {
        String[] tabTexts = new String[]{ResourceUtils.getString(R.string.coupon_operate_usable), ResourceUtils.getString(R.string.coupon_operate_unusable)};
        mPageFragmentAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), OperateCouponListActivity.this);
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(OperateCouponListFragment.COUPON_LIST_STATUS_TYPE, i == 0 ? OperateCouponListFragment.COUPON_LIST_TYPE_ONLINE : OperateCouponListFragment.COUPON_LIST_TYPE_OFFLINE);
            mPageFragmentAdapter.addFragment(OperateCouponListFragment.class.getName(), args);
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

    @OnClick({R.id.toolbar_back, R.id.toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right:
                toolbarRight.setSelected(toolbarRight.isSelected() ? false : true);
                mTypeFilterPopupWindow.showAsDownCenter(true);
                break;
        }
    }
}
