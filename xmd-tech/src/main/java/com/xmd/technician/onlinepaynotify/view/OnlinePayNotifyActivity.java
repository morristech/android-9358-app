package com.xmd.technician.onlinepaynotify.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

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
    private int status = PayNotifyInfo.STATUS_ALL;

    private ActivityOnlinePayNotifyBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_online_pay_notify);
        setSupportActionBar(mBinding.titleBar);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(null);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            mBinding.titleBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        //初始化时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        startTime = calendar.getTimeInMillis() - (29L * DateUtils.DAY_MILLIS_SECOND);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        endTime = calendar.getTimeInMillis();

        if (savedInstanceState != null) {
            mNotifyListFragment = (OnlinePayNotifyFragment) getSupportFragmentManager().findFragmentByTag("online_pay_notify_fragment");
            mNotifyListFragment.setFilter(startTime, endTime, status, false);
        } else {
            mNotifyListFragment = OnlinePayNotifyFragment.newInstance(startTime, endTime, status, false);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragmentContainer, mNotifyListFragment, "online_pay_notify_fragment");
            ft.commit();
        }

        mBinding.startTime.setText(DateUtils.getSdf("yyyy-MM-dd").format(startTime));
        mBinding.endTime.setText(DateUtils.getSdf("yyyy-MM-dd").format(endTime));

        mBinding.refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.refreshView.setRefreshing(false);
                mNotifyListFragment.loadData(true);
            }
        });
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
                calendar.set(year, monthOfYear, dayOfMonth, 23, 59, 59);
                endTime = calendar.getTimeInMillis();
                mBinding.endTime.setText(DateUtils.getSdf("yyyy-MM-dd").format(endTime));
            }
        });
    }

    public void onClickFilter(View view) {
        if (startTime > endTime) {
            Toast.makeText(this, "开始时间大于结束时间，请重新设置！", Toast.LENGTH_SHORT).show();
        } else {
            mNotifyListFragment.setFilter(startTime, endTime, status, false);
            mNotifyListFragment.loadData(false);
        }
    }

    public void onClickFilterMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, mBinding.filterMenu);
        popupMenu.inflate(R.menu.pay_notify_filter);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.status_all:
                        status = PayNotifyInfo.STATUS_ALL;
                        break;
                    case R.id.status_unverified:
                        status = PayNotifyInfo.STATUS_UNVERIFIED;
                        break;
                    case R.id.status_accept:
                        status = PayNotifyInfo.STATUS_ACCEPTED;
                        break;
                    case R.id.status_reject:
                        status = PayNotifyInfo.STATUS_REJECTED;
                        break;
                }
                mBinding.filterMenu.setText(item.getTitle());
                mNotifyListFragment.setFilter(startTime, endTime, status, false);
                mNotifyListFragment.loadData(false);
                return true;
            }
        });
        popupMenu.show();
    }
}
