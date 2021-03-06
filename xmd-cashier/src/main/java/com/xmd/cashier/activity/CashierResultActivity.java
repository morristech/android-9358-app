package com.xmd.cashier.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.contract.CashierResultContract;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.presenter.CashierResultPresenter;

/**
 * Created by zr on 17-5-16.
 */

public class CashierResultActivity extends BaseActivity implements CashierResultContract.View {
    private CashierResultContract.Presenter mPresenter;

    private TextView mStatusText;
    private ImageView mStatusImage;
    private Button mPayConfirmBtn;
    private TextView mStatusErrorText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_result);
        mPresenter = new CashierResultPresenter(this, this);
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
        showToolbar(R.id.toolbar, TradeManager.getInstance().getCurrentTrade().currentChannelName, TOOL_BAR_NAV_NONE);
        mStatusText = (TextView) findViewById(R.id.tv_status);
        mStatusImage = (ImageView) findViewById(R.id.img_status);
        mPayConfirmBtn = (Button) findViewById(R.id.btn_pay_confirm);
        mStatusErrorText = (TextView) findViewById(R.id.tv_status_error);

        mPayConfirmBtn.setOnClickListener(new View.OnClickListener() {
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
    public boolean onKeyEventBack() {
        mPresenter.onConfirm();
        return true;
    }

    @Override
    public void setPresenter(CashierResultContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void statusSuccess(String desc) {
        mStatusText.setText("交易成功");
        mStatusImage.setImageResource(R.drawable.ic_scan_ok);
        mStatusErrorText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
        mStatusErrorText.setText(desc);
    }

    @Override
    public void statusError(String error) {
        mStatusText.setText("交易失败");
        mStatusImage.setImageResource(R.drawable.ic_off);
        mStatusErrorText.setTextColor(ResourceUtils.getColor(R.color.colorRed));
        mStatusErrorText.setText(error);
    }

    @Override
    public void statusException() {
        mStatusText.setText("网络异常");
        mStatusImage.setImageResource(R.drawable.ic_off);
        mStatusErrorText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
        mStatusErrorText.setText("请前往小摩豆买单列表确认订单支付状态");
    }
}
