package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lhj on 2016/6/27.
 */
public class OrdersDetailActivity extends BaseActivity {

    @BindView(R.id.startTime)
    TextView startTime;
    @BindView(R.id.endTime)
    TextView endTime;
    @BindView(R.id.tabIndicator)
    ViewPagerTabIndicator tabIndicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
    private String initStartDateTime;
    private String initEndDateTime;
    String sT;
    String eT;
    private boolean isTotal;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initStartDateTime = DateUtil.getCurrentDate();
        if (Utils.isNotEmpty(SharedPreferenceHelper.getCurrentClubCreateTime())) {
            initStartDateTime = SharedPreferenceHelper.getCurrentClubCreateTime();
        } else {
            initStartDateTime = "2015-01-01";
        }
        initEndDateTime = DateUtil.getCurrentDate();
        setContentView(R.layout.activity_orders_detail);
        ButterKnife.bind(this);
        setRightVisible(false, 0, null);
        setTitle("订单数");
        startTime.setText(initStartDateTime);
        endTime.setText(initEndDateTime);
        String[] tabTexts = new String[]{
                ResourceUtils.getString(R.string.today),
                ResourceUtils.getString(R.string.current_week),
                ResourceUtils.getString(R.string.current_month),
                ResourceUtils.getString(R.string.accumulate)};

        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(OrderDetailFragment.BIZ_TYPE, i);
            mPageFragmentPagerAdapter.addFragment(OrderDetailFragment.class.getName(), args);
        }
        viewPager.setAdapter(mPageFragmentPagerAdapter);
        tabIndicator.setTabTexts(tabTexts);
        tabIndicator.setWithIndicator(true);
        tabIndicator.setWithDivider(true);
        tabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        tabIndicator.setViewPager(viewPager);
        tabIndicator.setup();
        tabIndicator.setOnPageChangeListener(position -> refreshTimeText(position));
        tabIndicator.setOnTabclickListener(position -> refreshTimeText(position));
        tabIndicator.updateSelectedPosition(getIntent().getIntExtra(Constant.PARAM_RANGE, 0));
        viewPager.setCurrentItem(3);
        getData(initStartDateTime, DateUtil.getCurrentDate());
    }

    @OnClick({R.id.startTime, R.id.endTime, R.id.btnSubmit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTime:
                DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(this, startTime.getText().toString());
                dataPickDialogStr.dateTimePicKDialog(startTime);
                break;
            case R.id.endTime:
                DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog(this, endTime.getText().toString());
                dataPickDialogEnd.dateTimePicKDialog(endTime);
                break;
            case R.id.btnSubmit:
                sT = startTime.getText().toString();
                eT = endTime.getText().toString();
                int str = Utils.dateToInt(sT);
                int end = Utils.dateToInt(eT);
                if (end >= str) {
                    getData(sT, eT);
                    if (!isTotal) {
                        viewPager.setCurrentItem(3);
                    }
                    startTime.setText(sT);
                    endTime.setText(eT);
                    isTotal = true;
                } else {
                    ToastUtils.showToastShort(OrdersDetailActivity.this, ResourceUtils.getString(R.string.time_select_alert));
                    return;
                }

                break;
        }

    }

    private void refreshTimeText(int range) {
        switch (range) {
            case 0:
                isTotal = false;
                initStartDateTime = DateUtil.getCurrentDate();
                startTime.setText(DateUtil.getCurrentDate());
                if (isFirst) {
                    getData(DateUtil.getCurrentDate(), DateUtil.getCurrentDate());
                    isFirst = false;
                }

                break;
            case 1:
                isTotal = false;
                initStartDateTime = DateUtil.getMondayOfWeek();
                if (isFirst) {
                    getData(DateUtil.getMondayOfWeek(), DateUtil.getCurrentDate());
                    isFirst = false;
                }
                break;
            case 2:
                isTotal = false;
                initStartDateTime = DateUtil.getFirstDayOfMonth();
                break;
            case 3:
                isTotal = true;
                if (Utils.isNotEmpty(SharedPreferenceHelper.getCurrentClubCreateTime())) {
                    initStartDateTime = SharedPreferenceHelper.getCurrentClubCreateTime();
                } else {
                    initStartDateTime = "2015-01-01";
                }
                break;

        }
        startTime.setText(initStartDateTime);
        initEndDateTime = DateUtil.getCurrentDate();
        endTime.setText(initEndDateTime);
    }

    private void getData(String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TYPE, String.valueOf(viewPager.getCurrentItem()));
        params.put(RequestConstant.KEY_START_DATE, startDate);
        params.put(RequestConstant.KEY_END_DATE, endDate);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ORDER_DETAIL, params);
    }

}
