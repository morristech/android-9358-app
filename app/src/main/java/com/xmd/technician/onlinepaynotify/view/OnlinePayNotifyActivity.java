package com.xmd.technician.onlinepaynotify.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.DatePicker;

import com.xmd.technician.R;
import com.xmd.technician.common.CustomDatePicker;
import com.xmd.technician.common.DateUtils;
import com.xmd.technician.databinding.ActivityOnlinePayNotifyBinding;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfo;
import com.xmd.technician.window.BaseActivity;
import com.xmd.technician.window.BaseFragment;

import java.util.Calendar;

public class OnlinePayNotifyActivity extends BaseActivity implements BaseFragment.IFragmentCallback {
    private OnlinePayNotifyFragment mNotifyListFragment;

    private long startTime;
    private long endTime;

    private ActivityOnlinePayNotifyBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_online_pay_notify);

        setTitle(R.string.online_pay_notify_title);

        //初始化时间
        endTime = System.currentTimeMillis();
        endTime = endTime - endTime % (DateUtils.DAY_MILLIS_SECOND) + DateUtils.DAY_MILLIS_SECOND;
        startTime = endTime - (30L * DateUtils.DAY_MILLIS_SECOND);

        if (savedInstanceState != null) {
            mNotifyListFragment = (OnlinePayNotifyFragment) getSupportFragmentManager().findFragmentByTag("online_pay_notify_fragment");
        } else {
            mNotifyListFragment = OnlinePayNotifyFragment.newInstance(startTime, endTime, PayNotifyInfo.STATUS_ALL);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, mNotifyListFragment, "online_pay_notify_fragment");
        ft.commit();

        mBinding.startTime.setText(DateUtils.getSdf("yyyy-MM-dd").format(startTime));
        mBinding.endTime.setText(DateUtils.getSdf("yyyy-MM-dd").format(endTime));
    }


    public void onClickStartTime(View view) {
        CustomDatePicker.showDatePickerView(this, startTime, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                startTime = calendar.getTimeInMillis();
                mBinding.startTime.setText(DateUtils.getSdf("yyyy-MM-dd").format(startTime));
            }
        });
    }

    public void onClickEndTime(View view) {
        CustomDatePicker.showDatePickerView(this, endTime, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                endTime = calendar.getTimeInMillis();
                mBinding.endTime.setText(DateUtils.getSdf("yyyy-MM-dd").format(endTime));
            }
        });
    }

    public void onClickFilter(View view) {
        mNotifyListFragment.setFilter(startTime, endTime, PayNotifyInfo.STATUS_ALL);
    }


}
