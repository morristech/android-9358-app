package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.manager.R;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ReportCreateResult;
import com.xmd.manager.widget.ClearableEditText;
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.DateTimePickDialogUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-9-12.
 */

public class NewAddOperateActivity extends BaseActivity {
    @BindView(R.id.edit_operate_name)
    ClearableEditText editOperateName;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;

    private String mOperateName;
    private String mOperateStartTime;
    private String mOperateEndTime;
    private String mInitStartTime;
    private String mInitEndTime;
    private Subscription mCrateOperateSubscription;

    Map<String, String> mParams = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_operate);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.new_add_operate_title));
        mInitStartTime = DateUtil.getFirstDayOfMonthTime();
        mInitEndTime = DateUtil.getCurrentTime();
        mCrateOperateSubscription = RxBus.getInstance().toObservable(ReportCreateResult.class).subscribe(
                reportCreateResult -> {
                    handlerReportCreateResult(reportCreateResult);
                }
        );
    }

    private void handlerReportCreateResult(ReportCreateResult reportCreateResult) {
        if (reportCreateResult.statusCode == 200) {
            Intent intent = new Intent();
            this.setResult(RESULT_OK, intent);
            this.finish();
        } else {
            XToast.show(reportCreateResult.msg);
        }
    }

    @OnClick(R.id.tv_start_time)
    public void onTvStartTimeClicked() {
        DateTimePickDialogUtil timePickDialog = new DateTimePickDialogUtil(NewAddOperateActivity.this, mInitStartTime);
        timePickDialog.dateTimePicKDialog(tvStartTime);
    }

    @OnClick(R.id.tv_end_time)
    public void onTvEndTimeClicked() {
        DateTimePickDialogUtil timePickDialog = new DateTimePickDialogUtil(NewAddOperateActivity.this, mInitEndTime);
        timePickDialog.dateTimePicKDialog(tvEndTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mCrateOperateSubscription);
    }

    @OnClick(R.id.btn_create_operate)
    public void onBtnCreateOperateClicked() {
        mOperateName = editOperateName.getText().toString().trim();
        mOperateStartTime = tvStartTime.getText().toString();
        mOperateEndTime = tvEndTime.getText().toString();

        if (TextUtils.isEmpty(mOperateName)) {
            XToast.show("请输入报表名称");
            return;
        }
        if (TextUtils.isEmpty(mOperateStartTime)) {
            XToast.show("请输入报表开始时间");
            return;
        }
        if (TextUtils.isEmpty(mOperateEndTime)) {
            XToast.show("请输入报表结束时间");
            return;
        }
        if(DateUtil.stringDateToLong(mOperateEndTime) < DateUtil.stringDateToLong(mInitStartTime)){
            XToast.show("结束时间要大于开始时间");
            return;
        }
        mParams.clear();
        mParams.put(RequestConstant.KEY_REPORT_CUSTOM_START_TIME, mOperateStartTime + ":00");
        mParams.put(RequestConstant.KEY_REPORT_CUSTOM_NAME, mOperateEndTime + ":59");
        mParams.put(RequestConstant.KEY_REPORT_CUSTOM_NAME, mOperateName);
     //   MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CREATE_REPORT_CUSTOM, mParams);
    }
}
