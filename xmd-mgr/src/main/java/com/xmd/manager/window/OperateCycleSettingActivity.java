package com.xmd.manager.window;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.common.Utils;
import com.xmd.manager.widget.TimePickDialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-9-13.
 */

public class OperateCycleSettingActivity extends BaseActivity {

    @BindView(R.id.tv_today_start_time)
    TextView tvTodayStartTime;
    @BindView(R.id.tv_tomorrow_end_time)
    TextView tvTomorrowEndTime;
    private String mCycleTodayTime;
    private String mCycleTomorrowTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_cycle);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mCycleTodayTime = getIntent().getStringExtra(Constant.OPERATE_START_TIME);
        mCycleTomorrowTime = getIntent().getStringExtra(Constant.OPERATE_END_TIME);
        setTitle(ResourceUtils.getString(R.string.operate_cycle_setting_title));
    }

    @OnClick(R.id.tv_today_start_time)
    public void onTvTodayStartTimeClicked() {
        TimePickDialogUtil timeDialog = new TimePickDialogUtil(OperateCycleSettingActivity.this, Utils.isEmpty(tvTodayStartTime.getText().toString())?"00:00":tvTodayStartTime.getText().toString());
        timeDialog.dateTimePicKDialog(tvTodayStartTime);
    }

    @OnClick(R.id.tv_tomorrow_end_time)
    public void onTvTomorrowEndTimeClicked() {
        TimePickDialogUtil timeDialog = new TimePickDialogUtil(OperateCycleSettingActivity.this,Utils.isEmpty(tvTomorrowEndTime.getText().toString())?"00:00":tvTomorrowEndTime.getText().toString());
        timeDialog.dateTimePicKDialog(tvTomorrowEndTime);
    }

    @OnClick(R.id.btn_create_operate)
    public void onBtnCreateOperateClicked() {

    }
}
