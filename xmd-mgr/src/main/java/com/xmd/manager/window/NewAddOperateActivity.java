package com.xmd.manager.window;

import android.os.Bundle;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.manager.R;
import com.xmd.manager.widget.ClearableEditText;
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.DateTimePickDialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_operate);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.new_add_operate_title));
        mInitStartTime = "2017-09-11 18：58:00";
        mInitEndTime = "2017-09-13 18：58:00";
    }

    @OnClick(R.id.tv_start_time)
    public void onTvStartTimeClicked() {
        DateTimePickDialogUtil timePickDialog = new DateTimePickDialogUtil(NewAddOperateActivity.this,mInitStartTime);
        timePickDialog.dateTimePicKDialog(tvStartTime);
    }

    @OnClick(R.id.tv_end_time)
    public void onTvEndTimeClicked() {
        DateTimePickDialogUtil timePickDialog = new DateTimePickDialogUtil(NewAddOperateActivity.this,mInitEndTime);
        timePickDialog.dateTimePicKDialog(tvEndTime);
    }

    @OnClick(R.id.btn_create_operate)
    public void onBtnCreateOperateClicked() {

    }
}
