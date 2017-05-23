package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/6/27.
 */
public class RegisterDetailActivity extends BaseActivity {
    @Bind(R.id.startTime)
    TextView startTime;
    @Bind(R.id.endTime)
    TextView endTime;
    @Bind(R.id.btnSubmit)
    Button btnSubmit;
    @Bind(R.id.tabIndicator)
    ViewPagerTabIndicator tabIndicator;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
    private String initStartDateTime;
    private String initEndDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_detail);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        setRightVisible(false, 0, null);
        setTitle(ResourceUtils.getString(R.string.statistics_fragment_register_users));
        initStartDateTime = getIntent().getStringExtra(RequestConstant.KEY_SELECTED_START_TIME);
        initEndDateTime = getIntent().getStringExtra(RequestConstant.KEY_SELECTED_END_TIME);
        String[] tabTexts = new String[]{
                ResourceUtils.getString(R.string.today),
                ResourceUtils.getString(R.string.current_week),
                ResourceUtils.getString(R.string.current_month),
                ResourceUtils.getString(R.string.accumulate)};

        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(OrderDetailFragment.BIZ_TYPE, i);
            mPageFragmentPagerAdapter.addFragment(RegisterDetailFragment.class.getName(), args);
        }
        viewPager.setAdapter(mPageFragmentPagerAdapter);
        tabIndicator.setTabTexts(tabTexts);
        tabIndicator.setWithIndicator(true);
        tabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        tabIndicator.setViewPager(viewPager);
        tabIndicator.setup();
        tabIndicator.setOnPageChangeListener(position -> refreshTimeText(position));
        tabIndicator.setOnTabclickListener(position -> refreshTimeText(position));

        tabIndicator.updateSelectedPosition(getIntent().getIntExtra(Constant.PARAM_RANGE, 3));
        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                refreshTimeText(getIntent().getIntExtra(Constant.PARAM_RANGE, 4));
                btnSubmit.performClick();
            }
        }, 300);

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
                String sT = startTime.getText().toString();
                String eT = endTime.getText().toString();
                int str = Utils.dateToInt(sT);
                int end = Utils.dateToInt(eT);
                if (end >= str) {
                    Map<String, String> params = new HashMap<>();
                    params.put(RequestConstant.KEY_TYPE, String.valueOf(viewPager.getCurrentItem()));
                    params.put(RequestConstant.KEY_START_DATE, sT);
                    params.put(RequestConstant.KEY_END_DATE, eT);
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_GET_REGISTER_DETAIL, params);

                } else {
                    ToastUtils.showToastShort(this, ResourceUtils.getString(R.string.time_select_alert));
                }
                break;
        }
    }

    private void refreshTimeText(int range) {
        switch (range) {
            case 0:
                initStartDateTime = DateUtil.getCurrentDate();
                startTime.setText(DateUtil.getCurrentDate());

                initEndDateTime = DateUtil.getCurrentDate();
                endTime.setText(initEndDateTime);
                break;
            case 1:
                initStartDateTime = DateUtil.getMondayOfWeek();
                startTime.setText(initStartDateTime);
                initEndDateTime = DateUtil.getCurrentDate();
                endTime.setText(initEndDateTime);
                break;
            case 2:
                initStartDateTime = DateUtil.getFirstDayOfMonth();
                startTime.setText(initStartDateTime);
                initEndDateTime = DateUtil.getCurrentDate();
                endTime.setText(initEndDateTime);
                break;
            case 3:
                initStartDateTime = "2015-01-01";
                startTime.setText(initStartDateTime);
                initEndDateTime = DateUtil.getCurrentDate();
                endTime.setText(initEndDateTime);
                btnSubmit.performClick();
                break;
            case 4:
                initStartDateTime = getIntent().getStringExtra(RequestConstant.KEY_SELECTED_START_TIME);
                initEndDateTime = getIntent().getStringExtra(RequestConstant.KEY_SELECTED_END_TIME);
                startTime.setText(initStartDateTime);
                endTime.setText(initEndDateTime);
                break;
        }

    }

}
