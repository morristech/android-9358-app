package com.xmd.cashier.presenter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.contract.AccountStatisticsSettingContract;
import com.xmd.cashier.dal.sp.SPManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zr on 17-9-27.
 */

public class AccountStatisticsSettingPresenter implements AccountStatisticsSettingContract.Presenter {
    private Context mContext;
    private AccountStatisticsSettingContract.View mView;
    private TimePickerView mPickerView;
    private String mStart;
    private String mEnd;

    public AccountStatisticsSettingPresenter(Context context, AccountStatisticsSettingContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mStart = SPManager.getInstance().getStatisticsStart();
        mEnd = SPManager.getInstance().getStatisticsEnd();
        mView.showStart(mStart);
        mView.showEnd(mEnd);
        initTimePicker();
    }

    private void initTimePicker() {
        mPickerView = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String time = DateUtils.doDate2String(date, DateUtils.DF_HOUR_MIN_SEC);
                switch (v.getId()) {
                    case R.id.tv_setting_start: //开始时间
                        mView.showStart(time);
                        mStart = time;
                        break;
                    case R.id.tv_setting_end:   //截止时间
                        mView.showEnd(time);
                        mEnd = time;
                        break;
                    default:
                        break;
                }
            }
        })
                .setLayoutRes(R.layout.layout_picker_view, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView tvChoice = (TextView) v.findViewById(R.id.tv_picker_choice);
                        tvChoice.setText("返回");
                        TextView tvFinish = (TextView) v.findViewById(R.id.tv_picker_finish);
                        tvChoice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPickerView.dismiss();
                            }
                        });

                        tvFinish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPickerView.dismiss();
                                mPickerView.returnData();
                            }
                        });
                    }
                })
                .setType(new boolean[]{false, false, false, true, true, true})
                .isCenterLabel(false)
                .setTextColorCenter(ResourceUtils.getColor(R.color.colorPink))
                .setDividerColor(ResourceUtils.getColor(R.color.colorPink))
                .build();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onStartPick(View view) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(DateUtils.doString2Date(mStart, DateUtils.DF_HOUR_MIN_SEC));
        mPickerView.setDate(startTime);
        mPickerView.show(view);
    }

    @Override
    public void onEndPick(View view) {
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(DateUtils.doString2Date(mEnd, DateUtils.DF_HOUR_MIN_SEC));
        mPickerView.setDate(endTime);
        mPickerView.show(view);
    }

    @Override
    public void onConfirm() {
        SPManager.getInstance().setStatisticsStart(mStart);
        SPManager.getInstance().setStatisticsEnd(mEnd);
        mView.showToast("设置成功");
        if (SPManager.getInstance().getFirstStatistic()) {
            UiNavigation.gotoAccountStatisticsActivity(mContext);
            SPManager.getInstance().setFirstStatistic(false);
        }
        mView.finishSelf();
    }
}
