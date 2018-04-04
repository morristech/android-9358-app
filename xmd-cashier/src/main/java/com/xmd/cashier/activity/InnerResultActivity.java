package com.xmd.cashier.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerResultContract;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.presenter.InnerResultPresenter;
import com.xmd.cashier.widget.StepView;

/**
 * Created by zr on 17-11-4.
 */

public class InnerResultActivity extends BaseActivity implements InnerResultContract.View {
    private InnerResultContract.Presenter mPresenter;

    private LinearLayout mStatusLayout;
    private ImageView mStatusImg;
    private TextView mStatusText;
    private Button mDetailBtn;
    private Button mOtherBtn;
    private Button mContinueBtn;
    private Button mCancelBtn;
    private TextView mStatusErrorDesc;

    private StepView mStepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_result);
        mPresenter = new InnerResultPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, TradeManager.getInstance().getCurrentTrade().currentChannelName);
        mStepView = (StepView) findViewById(R.id.sv_step_result);
        mStatusLayout = (LinearLayout) findViewById(R.id.layout_order_status);
        mStatusImg = (ImageView) findViewById(R.id.img_order_status);
        mStatusText = (TextView) findViewById(R.id.tv_order_status);
        mDetailBtn = (Button) findViewById(R.id.btn_view_order);
        mOtherBtn = (Button) findViewById(R.id.btn_view_other);
        mContinueBtn = (Button) findViewById(R.id.btn_view_continue);
        mCancelBtn = (Button) findViewById(R.id.btn_view_cancel);
        mStatusErrorDesc = (TextView) findViewById(R.id.tv_order_status_error);
        mDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onDetail();
            }
        });

        mOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onDone();
            }
        });

        mContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onContinue();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClose();
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
    public void setPresenter(InnerResultContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void statusSuccess(String desc) {
        mStatusLayout.setBackgroundResource(R.drawable.ic_bg_circle_green);
        mStatusImg.setVisibility(View.VISIBLE);
        mStatusText.setText("支付成功");
        mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorWhite));
        mStatusErrorDesc.setVisibility(View.VISIBLE);
        mStatusErrorDesc.setTextColor(ResourceUtils.getColor(R.color.colorText2));
        mStatusErrorDesc.setText(desc);
        // 显示详情和完成
        mDetailBtn.setVisibility(View.VISIBLE);
        mOtherBtn.setVisibility(View.VISIBLE);
        mCancelBtn.setVisibility(View.GONE);
        mContinueBtn.setVisibility(View.GONE);
    }

    @Override
    public void statusError(String error) {
        mStatusLayout.setBackgroundResource(R.drawable.ic_bg_circle_gray);
        mStatusImg.setVisibility(View.GONE);
        mStatusText.setText("支付失败");
        mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
        mStatusErrorDesc.setVisibility(View.VISIBLE);
        mStatusErrorDesc.setTextColor(ResourceUtils.getColor(R.color.colorRed));
        mStatusErrorDesc.setText(error);
        // 显示取消和继续
        mDetailBtn.setVisibility(View.GONE);
        mOtherBtn.setVisibility(View.GONE);
        mCancelBtn.setVisibility(View.VISIBLE);
        mContinueBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void statusException() {
        mStatusLayout.setBackgroundResource(R.drawable.ic_bg_circle_gray);
        mStatusImg.setVisibility(View.GONE);
        mStatusText.setText("网络异常");
        mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
        mStatusErrorDesc.setVisibility(View.VISIBLE);
        mStatusErrorDesc.setTextColor(ResourceUtils.getColor(R.color.colorRed));
        mStatusErrorDesc.setText("请前往小摩豆买单列表确认订单支付状态");
        mDetailBtn.setVisibility(View.GONE);
        mOtherBtn.setVisibility(View.VISIBLE);
        mCancelBtn.setVisibility(View.GONE);
        mContinueBtn.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onEventBack();
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

    @Override
    public void showStepView() {
        mStepView.setSteps(AppConstants.INNER_PAY_STEPS);
        mStepView.selectedStep(3);
    }
}
