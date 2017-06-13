package com.xmd.appointment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseDialogFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.appointment.beans.AppointmentSetting;
import com.xmd.appointment.databinding.FragmentTimeSelectBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by heyangya on 17-5-24.
 * 时间选择界面
 */

public class TimeSelectFragment extends BaseDialogFragment {
    private final static String EXTRA_SELECTED_DATA = "extra_selected_data";
    private final static String EXTRA_DATA = "extra_data";

    public static TimeSelectFragment newInstance(Date selected, AppointmentSetting ext) {
        TimeSelectFragment fragment = new TimeSelectFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SELECTED_DATA, selected);
        args.putSerializable(EXTRA_DATA, ext);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentTimeSelectBinding mBinding;

    private CommonRecyclerViewAdapter<AppointmentSetting.TimeInfo> mDayAdapter;
    private CommonRecyclerViewAdapter<AppointmentSetting.TimeSection> mHourAdapter;
    private CommonRecyclerViewAdapter<AppointmentSetting.TimeSection> mMinuteAdapter;

    public ObservableField<String> selectedTime = new ObservableField<>("请选择");
    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableField<String> loadingError = new ObservableField<>();

    private Date mArgumentTime;
    private AppointmentSetting mArgumentSetting;

    private Date mSelectedTime;

    private List<AppointmentSetting.TimeInfo> mDayData;
    private List<AppointmentSetting.TimeSection> mHourData;
    private List<AppointmentSetting.TimeSection> mMinuteData;

    private int mDayPosition = 1;
    private int mHourPosition = 1;
    private int mMinutePosition = 1;

    private static final int TIME_ITEM_HEIGHT_DP = 48;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Listener)) {
            throw new RuntimeException("activity must implement interface Listener!");
        }
        ScreenUtils.initScreenSize(getActivity().getWindowManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_select, container, false);

        mDayAdapter = new CommonRecyclerViewAdapter<>();
        initRecyclerView(mBinding.dayRecyclerView, mDayAdapter);
        mHourAdapter = new CommonRecyclerViewAdapter<>();
        initRecyclerView(mBinding.hourRecyclerView, mHourAdapter);
        mMinuteAdapter = new CommonRecyclerViewAdapter<>();
        initRecyclerView(mBinding.minuteRecyclerView, mMinuteAdapter);
        mBinding.layoutTime.getLayoutParams().height = ScreenUtils.dpToPx(TIME_ITEM_HEIGHT_DP * 3);

        mBinding.setHandler(this);
        return mBinding.getRoot();
    }

    private void initRecyclerView(final RecyclerView recyclerView, CommonRecyclerViewAdapter adapter) {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setHandler(BR.handler, this);
        adapter.setHeader(R.layout.list_item_time_day, BR.data, null);
        adapter.setFooter(R.layout.list_item_time_day, BR.data, null);
        adapter.setViewInflatedListener(new CommonRecyclerViewAdapter.ViewInflatedListener() {
            @Override
            public void onViewInflated(int viewType, View view) {
                view.getLayoutParams().height = ScreenUtils.dpToPx(TIME_ITEM_HEIGHT_DP);
            }
        });
        recyclerView.setAdapter(adapter);
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() + 1;
                XLogger.i("true position:" + position);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (recyclerView == mBinding.dayRecyclerView) {
                        if (mDayPosition != position) {
                            mDayPosition = position;
                            updateHourData();
                        }
                    } else if (recyclerView == mBinding.hourRecyclerView) {
                        if (mHourPosition != position) {
                            mHourPosition = position;
                            updateMinuteData();
                        }
                    } else if (recyclerView == mBinding.minuteRecyclerView) {
                        if (mMinutePosition != position) {
                            mMinutePosition = position;
                            updateSelectedData();
                        }
                    }
                }
            }
        });
    }

    private void updateHourData() {
        int prevLength = mHourData == null ? -1 : mHourData.size();
        mHourData = mDayData.get(mDayPosition - 1).getValidHourList();
        mHourAdapter.setData(R.layout.list_item_time_hour, BR.data, mHourData);
        mHourAdapter.notifyDataSetChanged();
        if (mHourData.size() < mHourPosition || (prevLength > 0 && prevLength != mHourData.size())) {
            mHourPosition = 1;
            mBinding.hourRecyclerView.scrollToPosition(0);
        }
        updateMinuteData();
    }

    private void updateMinuteData() {
        int prevLength = mMinuteData == null ? -1 : mMinuteData.size();
        mMinuteData = mDayData.get(mDayPosition - 1).getValidMinuteList(mHourData.get(mHourPosition - 1).getHour());
        mMinuteAdapter.setData(R.layout.list_item_time_minute, BR.data, mMinuteData);
        mMinuteAdapter.notifyDataSetChanged();
        if (mMinuteData.size() < mMinutePosition || (prevLength > 0 && prevLength != mMinuteData.size())) {
            mMinutePosition = 1;
            mBinding.minuteRecyclerView.scrollToPosition(0);
        }
        updateSelectedData();
    }

    private void updateSelectedData() {
        mSelectedTime = new Date(AppointmentSetting.getDayMillis(mDayData.get(mDayPosition - 1)) + mMinuteData.get(mMinutePosition - 1).getMillisTime());
        selectedTime.set(DateUtils.getSdf("yyyy-MM-dd HH:mm").format(mSelectedTime));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setTitle("选择技师");

        mArgumentSetting = (AppointmentSetting) getArguments().getSerializable(EXTRA_DATA);
        mArgumentTime = (Date) getArguments().getSerializable(EXTRA_SELECTED_DATA);

        mDayData = new ArrayList<>();
        for (AppointmentSetting.TimeInfo timeInfo : mArgumentSetting.getTimeList()) {
            if (timeInfo.getValidHourList().size() == 0) {
                continue;
            }
            mDayData.add(timeInfo);
        }
        if (mDayData.size() == 0) {
            XToast.show("没有可预约的时间！");
            onClickCancel();
            return;
        }
        mDayAdapter.setData(R.layout.list_item_time_day, BR.data, mDayData);
        mDayAdapter.notifyDataSetChanged();

        mDayPosition = 1;
        mHourPosition = 1;
        mMinutePosition = 1;
        //根据参数滚动到正确位置
        if (mArgumentTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mArgumentTime.getTime());
            //计算天列表滚动位置
            mDayPosition = 1;
            for (int i = 0; i < mDayData.size(); i++) {
                long dayTime = AppointmentSetting.getDayMillis(mDayData.get(i));
                if (calendar.getTimeInMillis() >= dayTime && calendar.getTimeInMillis() < dayTime + DateUtils.DAY_TIME_MS) {
                    mDayPosition = i + 1;
                    break;
                }
            }
            mBinding.dayRecyclerView.scrollToPosition(mDayPosition - 1);
            //更新小时列表数据
            updateHourData();
            //计算小时列表滚动位置
            int newHourPosition = -1;
            for (int i = 0; i < mHourData.size(); i++) {
                if (calendar.get(Calendar.HOUR_OF_DAY) == mHourData.get(i).getHour()) {
                    newHourPosition = i + 1;
                    break;
                }
            }
            if (newHourPosition > 0 && newHourPosition != mHourPosition) {
                mHourPosition = newHourPosition;
                mBinding.hourRecyclerView.scrollToPosition(mHourPosition - 1);
                updateMinuteData(); //更新分钟列表数据
            }
            //计算分钟列表滚动位置
            int newMinutePosition = -1;
            for (int i = 0; i < mMinuteData.size(); i++) {
                if (calendar.get(Calendar.MINUTE) == mMinuteData.get(i).getMinute()) {
                    newMinutePosition = i + 1;
                    break;
                }
            }
            if (newMinutePosition > 0 && newMinutePosition != mMinutePosition) {
                mMinutePosition = newMinutePosition;
                mBinding.minuteRecyclerView.scrollToPosition(mMinutePosition - 1);
            }
        } else {
            updateHourData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ScreenUtils.getScreenWidth() * 4 / 5;
            window.setAttributes(lp);
        }
    }

    public void onClickOK() {
        getDialog().dismiss();
        ((Listener) getActivity()).onSelectTime(mSelectedTime);
    }

    public void onClickCancel() {
        getDialog().dismiss();
    }

    public interface Listener {
        void onSelectTime(Date time);
    }
}
