package com.xmd.cashier.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberReadContract;
import com.xmd.cashier.dal.event.CardFinishEvent;
import com.xmd.cashier.dal.event.MagneticReaderEvent;
import com.xmd.cashier.presenter.MemberReadPresenter;
import com.xmd.cashier.widget.ClearableEditText;
import com.xmd.cashier.widget.StepView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zr on 17-7-11.
 * 刷会员卡 扫二维码 输入卡号 获取会员信息
 */

public class MemberReadActivity extends BaseActivity implements MemberReadContract.View {
    private MemberReadContract.Presenter mPresenter;
    private String mReadType;
    private Button mReadConfirm;
    private ImageView mScanImage;
    private ClearableEditText mMemberInput;

    private StepView mStepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_read);
        mPresenter = new MemberReadPresenter(this, this);
        mReadType = getIntent().getStringExtra(AppConstants.EXTRA_MEMBER_BUSINESS_TYPE);
        initView();
        mPresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mPresenter.onNewIntent(intent);
        super.onNewIntent(intent);
    }

    private void initView() {
        String title = Utils.getStringFromResource(R.string.app_name);
        mMemberInput = (ClearableEditText) findViewById(R.id.et_member_input);
        mReadConfirm = (Button) findViewById(R.id.btn_member_read_confirm);
        mStepView = (StepView) findViewById(R.id.sv_step_read);
        mScanImage = (ImageView) findViewById(R.id.img_scan);
        switch (mReadType) {
            case AppConstants.MEMBER_BUSINESS_TYPE_PAYMENT:
                title = "会员消费";
                mStepView.setVisibility(View.GONE);
                break;
            case AppConstants.MEMBER_BUSINESS_TYPE_RECHARGE:
                title = "会员充值";
                mScanImage.setVisibility(View.GONE);
                mStepView.setVisibility(View.GONE);
                break;
            case AppConstants.MEMBER_BUSINESS_TYPE_CARD:
                title = "会员开卡";
                mMemberInput.setHint("输入会员卡号");
                mScanImage.setVisibility(View.GONE);
                mStepView.setVisibility(View.VISIBLE);
                mStepView.setSteps(AppConstants.MEMBER_CARD_STEPS);
                mStepView.selectedStep(3);
                break;
            default:
                break;
        }
        showToolbar(R.id.toolbar, title);

        mReadConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onConfirm(mReadType);
            }
        });
    }

    public void onClickScan(View view) {
        mPresenter.onClickScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
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
    public void setPresenter(MemberReadContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void setInputContent(String content) {
        mMemberInput.setText(content);
    }

    @Override
    public String getInputContent() {
        return mMemberInput.getText().toString().replace(" ", "");
    }

    @Override
    public boolean onKeyEventBack() {
        finishSelf();
        if (mReadType.equals(AppConstants.MEMBER_BUSINESS_TYPE_CARD)) {
            showExitAnim();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MagneticReaderEvent event) {
        mMemberInput.setText(event.getResult());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CardFinishEvent event) {
        finishSelf();
    }
}
