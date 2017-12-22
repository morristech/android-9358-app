package com.xmd.salary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.app.BaseFragment;
import com.xmd.app.event.EventClickTechAvatar;
import com.xmd.app.event.UserInfoChangedEvent;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.app.utils.DateUtil;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.widget.RoundImageView;
import com.xmd.salary.event.DateChangedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-11-20.
 */

public class TechSalaryFragment extends BaseFragment {

    @BindView(R2.id.img_toolbar_left)
    RoundImageView imgToolbarLeft;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_table_day)
    TextView tvTableDay;
    @BindView(R2.id.table_day_line)
    View tableDayLine;
    @BindView(R2.id.tv_table_month)
    TextView tvTableMonth;
    @BindView(R2.id.table_month_line)
    View tableMonthLine;
    @BindView(R2.id.tv_table_customer)
    TextView tvTableCustomer;
    @BindView(R2.id.table_customer_line)
    View tableCustomerLine;

    public static final int TIME_FILTER_TYPE_DAY = 0; //天
    public static final int TIME_FILTER_TYPE_MONTH = 1; //月
    public static final int TIME_FILTER_TYPE_CUSTOMIZED = 2; //自定义


    private String mStartTime;
    private String mEndTime;
    private String mFilterType;
    private List<View> tableLines;
    private List<TextView> tableTextViews;
    private SalaryTimeSelectorFragment mSalaryTimeSelectorFragment;
    private SalaryTotalDataFragment mTotalDataFragment;
    private SalaryListDataFragment mSalaryListDataFragment;
    Unbinder unbinder;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tech_salary, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    private void initView() {
        tvTitle.setText(ResourceUtils.getString(R.string.salary_title));
        User user = UserInfoServiceImpl.getInstance().getCurrentUser();
        if (user != null) {
            Glide.with(getActivity()).load(user.getAvatar()).error(R.drawable.img_default_avatar).into(imgToolbarLeft);
        }
        imgToolbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventClickTechAvatar());
            }
        });
        initTableView();
        initializationTime();
        initTimeFilterFragment();
        initTotalDataFragment();
        initListDataFragment();
    }

    @Subscribe
    public void userInfoChangedEvent(UserInfoChangedEvent event) {
      try {
          if(!TextUtils.isEmpty(event.userHeadUrl)){
              Glide.with(getActivity()).load(event.userHeadUrl).error(R.drawable.img_default_avatar).into(imgToolbarLeft);
          }
      }catch (Exception e){
          e.toString();
      }
    }

    private void initializationTime() {
        mStartTime = DateUtil.getFirstDayOfMonth();
        mEndTime = DateUtil.getCurrentDate();
        mFilterType = ConstantResource.SALARY_TYPE_ALL;
    }

    private void initTableView() {
        tableLines = new ArrayList<>();
        tableLines.add(tableDayLine);
        tableLines.add(tableMonthLine);
        tableLines.add(tableCustomerLine);
        tableTextViews = new ArrayList<>();
        tableTextViews.add(tvTableDay);
        tableTextViews.add(tvTableMonth);
        tableTextViews.add(tvTableCustomer);
        setViewState(tableMonthLine, tvTableMonth);
    }

    private void setViewState(View lineView, TextView textView) {
        for (View view : tableLines) {
            view.setBackgroundColor(0xFFFFFFFF);
        }
        for (TextView tv : tableTextViews) {
            tv.setSelected(false);
        }
        lineView.setBackgroundColor(0xFFFF6666);
        textView.setSelected(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.rl_toolbar_right)
    public void onRlToolbarRightClicked() {
        startActivity(new Intent(getActivity(), SalaryIntroduceActivity.class));
    }

    @OnClick(R2.id.ll_table_day)
    public void onLlTableDayClicked() {
        setViewState(tableDayLine, tvTableDay);
        mSalaryTimeSelectorFragment.setCurrentTimeFilterType(TIME_FILTER_TYPE_DAY);
    }

    @OnClick(R2.id.ll_table_month)
    public void onLlTableMonthClicked() {
        setViewState(tableMonthLine, tvTableMonth);
        mSalaryTimeSelectorFragment.setCurrentTimeFilterType(TIME_FILTER_TYPE_MONTH);
    }

    @OnClick(R2.id.ll_table_customer)
    public void onLlTableCustomerClicked() {
        setViewState(tableCustomerLine, tvTableCustomer);
        mSalaryTimeSelectorFragment.setCurrentTimeFilterType(TIME_FILTER_TYPE_CUSTOMIZED);
    }

    private void initTimeFilterFragment() {
        mSalaryTimeSelectorFragment = new SalaryTimeSelectorFragment();
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_time_view, mSalaryTimeSelectorFragment);
        ft.commit();
    }

    private void initTotalDataFragment() {
        mTotalDataFragment = new SalaryTotalDataFragment();
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_salary_data, mTotalDataFragment);
        ft.commit();
    }


    private void initListDataFragment() {
        mSalaryListDataFragment = new SalaryListDataFragment();
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_salary_list_data, mSalaryListDataFragment);
        ft.commit();
    }

    @Subscribe
    public void DateChangedSubscribe(DateChangedEvent event) {
        mStartTime = TextUtils.isEmpty(event.startTime) ? mStartTime : event.startTime;
        mEndTime = TextUtils.isEmpty(event.endTime) ? mEndTime : event.endTime;
        mFilterType = TextUtils.isEmpty(event.selectedType) ? mFilterType : event.selectedType;
        mTotalDataFragment.getSalarySumAmount(mStartTime, mEndTime);
        if (mStartTime.equals(mEndTime)) {
            mSalaryListDataFragment.refreshCommissionDetail(mStartTime, mFilterType);
        } else {
            mSalaryListDataFragment.getCommissionList(mStartTime, mEndTime, mFilterType);
        }

    }

}
