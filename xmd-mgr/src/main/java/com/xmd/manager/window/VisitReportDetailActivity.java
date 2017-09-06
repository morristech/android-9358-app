package com.xmd.manager.window;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.VisitInfo;
import com.xmd.manager.beans.VisitListResult;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.DateTimePickDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lhj on 2016/9/22.
 */
public class VisitReportDetailActivity extends BaseListActivity<VisitInfo, VisitListResult> implements View.OnClickListener {


    TextView mStartTime;
    TextView mEndTime;
    Button mSubmitBtn;

    private String initStartDateTime;
    private String initEndDateTime;


    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_START_DATE, initStartDateTime);
        params.put(RequestConstant.KEY_END_DATE, initEndDateTime);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_VISIT_LIST, params);
    }


    @Override
    protected void setContentViewLayout() {
        super.setContentViewLayout();
        setContentView(R.layout.activity_visit_report_detail);
    }

    @Override
    protected void initOtherViews() {
        super.initOtherViews();
        setRightVisible(false, -1, null);
        setTitle(ResourceUtils.getString(R.string.right_btn_visit_detail));
        initView();
    }

    private void initView() {
        mStartTime = (TextView) findViewById(R.id.startTime);
        mEndTime = (TextView) findViewById(R.id.endTime);
        mSubmitBtn = (Button) findViewById(R.id.btnSubmit);
        mStartTime.setOnClickListener(this);
        mEndTime.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
        initStartDateTime = getIntent().getStringExtra(RequestConstant.KEY_SELECTED_START_TIME);
        initEndDateTime = getIntent().getStringExtra(RequestConstant.KEY_SELECTED_END_TIME);
        mStartTime.setText(initStartDateTime);
        mEndTime.setText(initEndDateTime);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTime:
                DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(this, mStartTime.getText().toString());
                dataPickDialogStr.dateTimePicKDialog(mStartTime);
                break;
            case R.id.endTime:
                DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog(this, mEndTime.getText().toString());
                dataPickDialogEnd.dateTimePicKDialog(mEndTime);
                break;
            case R.id.btnSubmit:
                mPages = 0;
                initStartDateTime = mStartTime.getText().toString();
                initEndDateTime = mEndTime.getText().toString();
                String sT = mStartTime.getText().toString();
                String eT = mEndTime.getText().toString();
                int str = Utils.dateToInt(sT);
                int end = Utils.dateToInt(eT);
                if (end >= str) {
                    onRefresh();
                } else {
                    ToastUtils.showToastShort(this, ResourceUtils.getString(R.string.time_select_alert));
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

