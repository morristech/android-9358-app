package com.xmd.appointment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Rect;
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
import com.xmd.app.BaseDialogFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.appointment.beans.AppointmentSetting;
import com.xmd.appointment.databinding.FragmentTimeSelectBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by heyangya on 17-5-24.
 * 时间选择界面
 */

public class TimeSelectFragment extends BaseDialogFragment {
    private final static String EXTRA_SELECTED_DATA = "extra_selected_data";
    private final static String EXTRA_DATA = "extra_data";

    public static TimeSelectFragment newInstance(AppointmentSetting.TimeSection selected, AppointmentSetting ext) {
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

    private AppointmentSetting.TimeSection mArgumentTimeSection;
    private AppointmentSetting mArgumentSetting;

    private AppointmentSetting.TimeSection mSelectedTime;

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
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, ScreenUtils.dpToPx(1));
            }
        });
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
                            updateHourData(mDayData.get(mDayPosition - 1).getValidHourList());
                        }
                    } else if (recyclerView == mBinding.hourRecyclerView) {
                        if (mHourPosition != position) {
                            mHourPosition = position;
                            updateMinuteData(mDayData.get(mHourPosition - 1).getValidMinuteList(mHourData.get(0).getHour()));
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

    private void updateHourData(List<AppointmentSetting.TimeSection> list) {
        mHourPosition = 1;
        mHourData = list;
        mHourAdapter.setData(R.layout.list_item_time_hour, BR.data, mHourData);
        mHourAdapter.notifyDataSetChanged();

        updateMinuteData(mDayData.get(mDayPosition - 1).getValidMinuteList(mHourData.get(0).getHour()));
    }

    private void updateMinuteData(List<AppointmentSetting.TimeSection> list) {
        mMinutePosition = 1;
        mMinuteData = list;
        mMinuteAdapter.setData(R.layout.list_item_time_minute, BR.data, mMinuteData);
        mMinuteAdapter.notifyDataSetChanged();
        updateSelectedData();
    }

    private void updateSelectedData() {
        if (mMinuteData == null) {
            return;
        }
        mSelectedTime = mMinuteData.get(mMinutePosition - 1);
        selectedTime.set(DateUtils.getSdf("yyyy-MM-dd ").format(AppointmentSetting.getTime(mDayData.get(mDayPosition - 1))) + mSelectedTime.getTimeStr());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setTitle("选择技师");

        mArgumentSetting = (AppointmentSetting) getArguments().getSerializable(EXTRA_DATA);
        mArgumentTimeSection = (AppointmentSetting.TimeSection) getArguments().getSerializable(EXTRA_SELECTED_DATA);

        mDayData = new ArrayList<>();
        mDayData.addAll(mArgumentSetting.getTimeList());
        filterDayData(mDayData);
        mDayAdapter.setData(R.layout.list_item_time_day, BR.data, mDayData);
        mDayAdapter.notifyDataSetChanged();

        updateHourData(mDayData.get(0).getValidHourList());
    }

    private void filterDayData(List<AppointmentSetting.TimeInfo> list) {
        Iterator<AppointmentSetting.TimeInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            List<AppointmentSetting.TimeSection> hourData = iterator.next().getTime();
            if (hourData == null || hourData.size() == 0) {
                iterator.remove();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ScreenUtils.getScreenWidth() * 4 / 5;
            lp.height = ScreenUtils.getScreenHeight() * 2 / 5;
            window.setAttributes(lp);
        }
    }

    public void onClickOK() {
        getDialog().dismiss();
    }

    public void onClickCancel() {
        getDialog().dismiss();
    }

    public interface Listener {
        void onSelectTime(AppointmentSetting.TimeSection timeSection);
    }
}
