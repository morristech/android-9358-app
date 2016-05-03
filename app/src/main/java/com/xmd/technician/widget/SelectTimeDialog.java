package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xmd.technician.R;
import com.xmd.technician.bean.TimeInfo;
import com.xmd.technician.wheel.OnWheelChangedListener;
import com.xmd.technician.wheel.WheelView;
import com.xmd.technician.wheel.adapters.ArrayWheelAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by SD_ZR on 15-6-16.
 */
public abstract class SelectTimeDialog extends Dialog {

    private static final int WHEEL_VISIBLE_COUNT = 7;

    private Context context;
    private TimeInfo mTimeInfo;

    // begin从Json解析的集合
    private List<String> mBeginTimeRange;
    // 对应的数组
    private String[] mBeginTimeArray;
    private String initBeginTime;

    // end从Json解析的集合
    private List<String> mEndTimeRange;
    // 对应的数组
    private String[] mEndTimeArray;
    // 变化量缓存
    private String[] mCacheEndTimeArray;
    private String initEndTime;

    public String[] arr_day = {"当日", "次日"};
    private String initDay;

    private WheelView mSelectBegin;
    private WheelView mSelectEnd;
    private WheelView mSelectDay;

    // 当前显示的内容
    public String mCurrentBegin;
    public String mCurrentDay;
    public String mCurrentEnd;

    private Button mSelectCancel;
    private Button mSelectConfirm;

    public SelectTimeDialog(Context context) {
        super(context);
        this.context = context;
    }

    public SelectTimeDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public SelectTimeDialog(Context context, int theme, TimeInfo info, String beginTime, String endTime, String day) {
        this(context, theme);
        mTimeInfo = info;
        initBeginTime = beginTime;
        initEndTime = endTime;
        initDay = day;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_time);
        mBeginTimeRange = mTimeInfo.beginTimeRange;
        mEndTimeRange = mTimeInfo.endTimeRange;

        mBeginTimeArray = new String[mBeginTimeRange.size()];
        mEndTimeArray = new String[mEndTimeRange.size()];

        mBeginTimeRange.toArray(mBeginTimeArray);
        mEndTimeRange.toArray(mEndTimeArray);

        mSelectBegin = (WheelView) findViewById(R.id.select_begin_time);
        mSelectBegin.setViewAdapter(new ArrayWheelAdapter<String>(context, mBeginTimeArray));
        mSelectBegin.setVisibleItems(WHEEL_VISIBLE_COUNT);
        mSelectBegin.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mCurrentBegin = mBeginTimeRange.get(newValue);
                if ("当日".equals(arr_day[mSelectDay.getCurrentItem()])) {
                    mCacheEndTimeArray = Arrays.copyOfRange(mEndTimeArray, newValue + 1, mEndTimeArray.length);
                    mSelectEnd.setViewAdapter(new ArrayWheelAdapter<String>(context, mCacheEndTimeArray));
                    mSelectEnd.setCurrentItem(0);
                    if (mCacheEndTimeArray.length == 0) {
                        // 变成次日
                        mSelectDay.setCurrentItem(1);
                    } else {
                        mCurrentEnd = mCacheEndTimeArray[0];
                    }
                } else {
                    mCacheEndTimeArray = Arrays.copyOfRange(mEndTimeArray, 0, newValue + 1);
                    mSelectEnd.setViewAdapter(new ArrayWheelAdapter<String>(context, mCacheEndTimeArray));
                    mSelectEnd.setCurrentItem(0);
                    mCurrentEnd = mCacheEndTimeArray[0];
                }
            }
        });

        mSelectDay = (WheelView) findViewById(R.id.select_day);
        mSelectDay.setViewAdapter(new ArrayWheelAdapter<String>(context, arr_day));
        mSelectDay.setVisibleItems(WHEEL_VISIBLE_COUNT);
        mSelectDay.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mCurrentDay = arr_day[newValue];
                int index = mSelectBegin.getCurrentItem();
                if ("当日".equals(arr_day[wheel.getCurrentItem()])) {
                    mCacheEndTimeArray = Arrays.copyOfRange(mEndTimeArray, index + 1, mEndTimeArray.length);
                    mSelectEnd.setViewAdapter(new ArrayWheelAdapter<String>(context, mCacheEndTimeArray));
                    mSelectEnd.setCurrentItem(0);
                    if (mCacheEndTimeArray.length == 0) {
                        // 变成次日
                        mSelectDay.setCurrentItem(1);
                    } else {
                        mCurrentEnd = mCacheEndTimeArray[0];
                    }
                } else {
                    mCacheEndTimeArray = Arrays.copyOfRange(mEndTimeArray, 0, index + 1);
                    mSelectEnd.setViewAdapter(new ArrayWheelAdapter<String>(context, mCacheEndTimeArray));
                    mSelectEnd.setCurrentItem(0);
                    mCurrentEnd = mCacheEndTimeArray[0];
                }
            }
        });

        mSelectEnd = (WheelView) findViewById(R.id.select_end_time);
        mSelectEnd.setViewAdapter(new ArrayWheelAdapter<String>(context, mEndTimeArray));
        mSelectEnd.setVisibleItems(WHEEL_VISIBLE_COUNT);
        mSelectEnd.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if(mCacheEndTimeArray != null) {
                    mCurrentEnd = mCacheEndTimeArray[newValue];
                }
            }
        });

        // 初始化显示的开始时间
        mSelectBegin.setCurrentItem(mBeginTimeRange.indexOf(initBeginTime));
        mCurrentBegin = mBeginTimeArray[mBeginTimeRange.indexOf(initBeginTime)];

        // 初始化显示次日/当日
        mSelectDay.setCurrentItem(("次日".equals(initDay) ? 1 : 0));
        mCurrentDay = initDay;

        // 初始化显示的结束时间
        if (mCacheEndTimeArray != null) {
            int initIndex = Arrays.asList(mCacheEndTimeArray).indexOf(initEndTime);
            mSelectEnd.setCurrentItem(initIndex);
            mCurrentEnd = mCacheEndTimeArray[initIndex];
        } else {
            mSelectEnd.setCurrentItem(mBeginTimeRange.indexOf(initEndTime));
            mCurrentEnd = mEndTimeArray[mEndTimeRange.indexOf(initEndTime)];
        }

        mSelectCancel = (Button) findViewById(R.id.select_cancel_button);
        mSelectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mSelectConfirm = (Button) findViewById(R.id.select_confirm_button);
        mSelectConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectConfirmMethod();
            }
        });
    }

    public abstract void onSelectConfirmMethod();
}
