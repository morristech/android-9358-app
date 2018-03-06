package com.xmd.cashier.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
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
    private TextView mErrorText;
    private Button mPayConfirmBtn;
    private Button mPayPrintBtn;
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
        mErrorText = (TextView) findViewById(R.id.tv_error);
        mPayConfirmBtn = (Button) findViewById(R.id.btn_pay_confirm);
        mPayPrintBtn = (Button) findViewById(R.id.btn_pay_print);
        mStatusErrorText = (TextView) findViewById(R.id.tv_status_error);

        mPayConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onConfirm();
            }
        });

        mPayPrintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPrint();
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
        return super.onKeyEventBack();
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
    public void showStatus(int status) {
        switch (status) {
            case AppConstants.TRADE_STATUS_SUCCESS:
                mStatusText.setText("交易成功");
                mStatusImage.setImageResource(R.drawable.ic_scan_ok);
                break;
            case AppConstants.TRADE_STATUS_ERROR:
                mStatusText.setText("交易失败");
                mStatusImage.setImageResource(R.drawable.ic_off);
                break;
            default:
                mStatusText.setText("交易状态未知");
                mStatusImage.setImageResource(R.drawable.ic_off);
                break;
        }
    }

    @Override
    public void showStatusError(String error) {
        if (TextUtils.isEmpty(error)) {
            mStatusErrorText.setVisibility(View.GONE);
        } else {
            mStatusErrorText.setVisibility(View.VISIBLE);
            mStatusErrorText.setTextColor(ResourceUtils.getColor(R.color.colorRed));
            mStatusErrorText.setText(error);
        }
    }

    @Override
    public void showStatusSuccess(String desc) {
        mStatusErrorText.setVisibility(View.VISIBLE);
        mStatusErrorText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
        mStatusErrorText.setText(desc);
    }

    @Override
    public void showPrint() {
        mPayConfirmBtn.setVisibility(View.VISIBLE);
        mPayPrintBtn.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText("如果未自动打印小票，可手动点击打印");
    }

    @Override
    public void showConfirm() {
        mPayConfirmBtn.setVisibility(View.VISIBLE);
        mPayPrintBtn.setVisibility(View.GONE);
        mErrorText.setVisibility(View.GONE);
    }
}
