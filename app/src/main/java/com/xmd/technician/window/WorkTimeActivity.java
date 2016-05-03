package com.xmd.technician.window;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.UpdateWorkStatusResult;
import com.xmd.technician.http.gson.UpdateWorkTimeResult;
import com.xmd.technician.http.gson.WorkTimeResult;
import com.xmd.technician.bean.DayInfo;
import com.xmd.technician.bean.TimeInfo;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.MultiCheckDialog;
import com.xmd.technician.widget.SelectTimeDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class WorkTimeActivity extends BaseActivity {

    @Bind(R.id.work_time) TextView mWorkTime;
    @Bind(R.id.work_day) TextView mWorkDay;
    @Bind(R.id.status_free) RadioButton mFreeStatus;
    @Bind(R.id.status_busy) RadioButton mBusyStatus;

    private Subscription mWorkTimeSubscription;
    private Subscription mUpdateWorkTimeSubscription;
    private Subscription mUpdateWorkStatusSubscription;

    private String mDayRange;
    private String mNetDayRange;
    private String mCalendarId = "-1";
    private String mBeginTime;
    private String mEndTime;
    private String mDay;
    private String mStatus;
    private String mEndDay;
    private TimeInfo mTimeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_time);
        ButterKnife.bind(this);

        setTitle(R.string.personal_fragment_layout_work_time);
        setBackVisible(true);
        setRightVisible(true,R.string.save);

        mFreeStatus.setChecked(true);

        mWorkTimeSubscription = RxBus.getInstance().toObservable(WorkTimeResult.class).subscribe(
                workTimeResult -> getWorkTimeResult(workTimeResult));

        mUpdateWorkTimeSubscription = RxBus.getInstance().toObservable(UpdateWorkTimeResult.class).subscribe(
                updateWorkTimeResult ->  updateWorkTimeResult());

        mUpdateWorkStatusSubscription = RxBus.getInstance().toObservable(UpdateWorkStatusResult.class).subscribe(
                updateWorkStatusResult -> {dismissProgressDialogIfShowing(); finish();}
        );

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_WORK_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mWorkTimeSubscription);
    }

    @OnClick(R.id.work_time_layout)
    public void timeSetting(){
        if(mTimeInfo == null){
            makeShortToast(getResources().getString(R.string.operation_invalid));
            return;
        }

        Dialog dialog = new SelectTimeDialog(WorkTimeActivity.this, R.style.default_dialog_style, mTimeInfo, mBeginTime, mEndTime, mDay) {
            @Override
            public void onSelectConfirmMethod() {
                dismiss();
                // beginTime和endTime以及day更新
                mBeginTime = mCurrentBegin;
                mDay = mCurrentDay;
                mEndTime = mCurrentEnd;
                mEndDay = mDay.equals("当日") ? "0" : "1";

                if (mBeginTime.equals(mEndTime)) {
                    mWorkTime.setText("24小时");
                } else {
                    mWorkTime.setText(mBeginTime + "-" + (mDay.equals("当日") ? "" : mDay) + mEndTime);
                }
            }
        };
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @OnClick(R.id.work_day_layout)
    public void daySetting(){
        if(TextUtils.isEmpty(mDayRange)){
            makeShortToast(getResources().getString(R.string.operation_invalid));
            return;
        }

        Dialog dialog = new MultiCheckDialog(WorkTimeActivity.this, R.style.default_dialog_style, mDayRange) {
            @Override
            public void onSelectDaysConfirmMethod() {
                dismiss();
                // 选中确认
                String dayName = onGetDayNameResult();
                if (dayName.length() == 0) {
                    dayName = mNetDayRange;
                } else if ("星期一,星期二,星期三,星期四,星期五,星期六,星期日".equals(dayName)) {
                    dayName = "每天";
                } else if ("星期一,星期二,星期三,星期四,星期五,星期六".equals(dayName)) {
                    dayName = "周一到周六";
                } else if ("星期一,星期二,星期三,星期四,星期五".equals(dayName)) {
                    dayName = "工作日";
                }
                mWorkDay.setText(dayName);
                // dayRange更新
                mDayRange = onGetDayIdResult();
            }
        };
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @OnClick(R.id.toolbar_right)
    public void updateWorkTime(){
        if(mTimeInfo == null){
            makeShortToast(getResources().getString(R.string.operation_invalid));
            return;
        }

        showProgressDialog(getString(R.string.save_waiting));
        if(mFreeStatus.isChecked()){
            mStatus = "free";
        }else {
            mStatus = "busy";
        }

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_DAY_RANGE, mDayRange);
        params.put(RequestConstant.KEY_BEGIN_TIME, mBeginTime);
        params.put(RequestConstant.KEY_END_TIME, mEndTime);
        params.put(RequestConstant.KEY_ID, mCalendarId);

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_WORK_TIME, params);
    }

    private void getWorkTimeResult(WorkTimeResult workTimeResult){
        if(workTimeResult.respData != null){
            // id初始化
            mCalendarId = workTimeResult.respData.id;
            // beginTime初始化
            mBeginTime = workTimeResult.respData.times.beginTime;
            // endTime初始化
            mEndTime = workTimeResult.respData.times.endTime;
            mStatus = workTimeResult.respData.techStatus;
            mTimeInfo = workTimeResult.respData.times;
            mNetDayRange = workTimeResult.respData.workDayRange;


            // begin>=end  ---  1(次日)    /   其他:0(当日)
            mEndDay = workTimeResult.respData.times.endDay;
            if (mBeginTime == null || mEndTime == null) {
                mWorkTime.setText(workTimeResult.respData.workTimeRange);
            } else {
                if ("1".equals(mEndDay)) {
                    mDay = "次日";
                    if (mBeginTime.equals(mEndTime)) {
                        mWorkTime.setText("24小时");
                    } else {
                        mWorkTime.setText(mBeginTime + "-次日" + mEndTime);
                    }
                } else {
                    mDay = "当日";
                    mWorkTime.setText(mBeginTime + "-" + mEndTime);
                }
            }

            List<DayInfo> days = workTimeResult.respData.days;
            StringBuilder sbDayName = new StringBuilder();
            StringBuilder sbDayId = new StringBuilder();
            for (DayInfo info : days) {
                if (info.isSelected) {
                    sbDayName.append(info.dayName + ",");
                    sbDayId.append(info.dayId + ",");
                }
            }
            // dayRange 初始化
            mDayRange = (sbDayId.length() == 0) ? sbDayId.toString() : sbDayId.substring(0, sbDayId.length() - 1);
            String result = (sbDayName.length() == 0) ? sbDayName.toString() : sbDayName.toString().substring(0, sbDayName.length() - 1);
            if (result.length() == 0) {
                result = workTimeResult.respData.workDayRange;
            } else if ("星期一,星期二,星期三,星期四,星期五,星期六,星期日".equals(result)) {
                result = "每天";
            } else if ("星期一,星期二,星期三,星期四,星期五,星期六".equals(result)) {
                result = "周一到周六";
            } else if ("星期一,星期二,星期三,星期四,星期五".equals(result)) {
                result = "工作日";
            }
            mWorkDay.setText(result);

            if(!TextUtils.isEmpty(mStatus) && mStatus.equals("busy")){
                mBusyStatus.setChecked(true);
            }else {
                mFreeStatus.setChecked(true);
            }
        }
    }

    private void updateWorkTimeResult(){
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_STATUS, mStatus);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_WORK_STATUS, params);
    }
}
