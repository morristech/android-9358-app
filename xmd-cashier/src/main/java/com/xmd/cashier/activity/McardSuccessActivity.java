package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.McardSuccessContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.presenter.McardSuccessPresenter;
import com.xmd.cashier.widget.StepView;

/**
 * Created by zr on 17-7-11.
 * 开卡完成
 */

public class McardSuccessActivity extends BaseActivity implements McardSuccessContract.View {
    private McardSuccessContract.Presenter mPresenter;
    private TextView mItemName;
    private TextView mItemLevel;
    private TextView mItemPhone;
    private TextView mItemCardNo;
    private Button mConfirmBtn;
    private StepView mStepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcard_success);
        mPresenter = new McardSuccessPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "会员开卡");
        mItemName = (TextView) findViewById(R.id.item_name);
        mItemLevel = (TextView) findViewById(R.id.item_level);
        mItemPhone = (TextView) findViewById(R.id.item_phone);
        mItemCardNo = (TextView) findViewById(R.id.item_card_no);
        mConfirmBtn = (Button) findViewById(R.id.btn_success_confirm);
        mStepView = (StepView) findViewById(R.id.sv_step_success);
        mStepView.setSteps(AppConstants.MEMBER_CARD_STEPS);
        mStepView.selectedStep(4);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onConfirm();
                mPresenter.onFinish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(McardSuccessContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showInfo(MemberInfo info) {
        mItemName.setText(info.name);
        mItemPhone.setText(info.phoneNum);
        mItemLevel.setText(info.memberTypeName);
        mItemCardNo.setText(info.cardNo);
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onFinish();
        showExitAnim();
        return true;
    }

    @Override
    public void showEnterAnim() {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }

    @Override
    public void showExitAnim() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }
}
