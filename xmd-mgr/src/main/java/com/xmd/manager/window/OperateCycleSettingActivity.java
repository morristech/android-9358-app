package com.xmd.manager.window;

import android.os.Bundle;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.manager.R;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.ReportSettingResult;
import com.xmd.manager.widget.TimePickDialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-9-13.
 */

public class OperateCycleSettingActivity extends BaseActivity {

    @BindView(R.id.tv_today_start_time)
    TextView tvTodayStartTime;
    @BindView(R.id.tv_tomorrow_end_time)
    TextView tvTomorrowEndTime;

    private String mCyclicTime;

    Subscription mGetCycleTimeSubscription;
    Subscription mSettingCyclicTimeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_cycle);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        // mCycleTime
        setTitle(ResourceUtils.getString(R.string.operate_cycle_setting_title));
        mGetCycleTimeSubscription = RxBus.getInstance().toObservable(ReportSettingResult.class).subscribe(
                result -> handleReportSettingResult(result)
        );
        mSettingCyclicTimeSubscription = RxBus.getInstance().toObservable(ReportSettingResult.class).subscribe(
                result -> handleGetReportSettingResult(result)
        );
    }

    private void handleGetReportSettingResult(ReportSettingResult result) {

    }

    private void handleReportSettingResult(ReportSettingResult result) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetCycleTimeSubscription, mSettingCyclicTimeSubscription);
    }

    @OnClick(R.id.tv_today_start_time)
    public void onTvTodayStartTimeClicked() {
        TimePickDialogUtil timeDialog = new TimePickDialogUtil(OperateCycleSettingActivity.this, Utils.isEmpty(tvTodayStartTime.getText().toString()) ? "00:00" : tvTodayStartTime.getText().toString());
        timeDialog.dateTimePicKDialog(tvTodayStartTime);
        tvTomorrowEndTime.setText(tvTodayStartTime.getText().toString());
    }


    @OnClick(R.id.btn_create_operate)
    public void onBtnCreateOperateClicked() {

    }
}
