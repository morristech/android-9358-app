package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.McardPhoneContract;
import com.xmd.cashier.dal.event.CardFinishEvent;
import com.xmd.cashier.presenter.McardPhonePresenter;
import com.xmd.cashier.widget.ClearableEditText;
import com.xmd.cashier.widget.StepView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zr on 17-7-11.
 * 开卡验证手机
 */

public class McardPhoneActivity extends BaseActivity implements McardPhoneContract.View {
    private McardPhoneContract.Presenter mPresenter;
    private ClearableEditText mPhoneInput;
    private Button mConfirm;
    private StepView mStepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcard_phone);
        mPresenter = new McardPhonePresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "会员开卡");
        mPhoneInput = (ClearableEditText) findViewById(R.id.et_phone_input);
        mConfirm = (Button) findViewById(R.id.btn_phone_confirm);
        mStepView = (StepView) findViewById(R.id.sv_step_phone);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onConfirm();
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
    public void setPresenter(McardPhoneContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public String getPhone() {
        return mPhoneInput.getText().toString();
    }

    @Override
    public void showEnterAnim() {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }

    @Override
    public void showExitAnim() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    @Override
    public void showStepView(int cardModel) {
        switch (cardModel) {
            case AppConstants.MEMBER_CARD_MODEL_NORMAL:
                mStepView.setSteps(AppConstants.MEMBER_CARD_STEPS_NORMAL);
                mStepView.selectedStep(1);
                break;
            default:
                mStepView.setSteps(AppConstants.MEMBER_CARD_STEPS_WITHOUT);
                mStepView.selectedStep(1);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CardFinishEvent event) {
        finishSelf();
    }
}
