package com.xmd.cashier.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.TradeQrcodePayContract;
import com.xmd.cashier.dal.event.QRScanStatusEvent;
import com.xmd.cashier.presenter.TradeQrcodePayPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zr on 17-5-12.
 * 小摩豆在线买单
 */

public class TradeQrcodePayActivity extends BaseActivity implements TradeQrcodePayContract.View {
    private TradeQrcodePayContract.Presenter mPresenter;

    private TextView mAmountText;
    private ImageView mQRCodeImg;

    private ImageView mScanTipText;
    private RelativeLayout mScanStatusLayout;

    private LinearLayout mQRCodeErrorLayout;
    private TextView mQRCodeErrorText;
    private TextView mQRCodeExpireText;

    private TextView mGiftActivityText;

    private int mTradeType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_qrcode_pay);
        mTradeType = getIntent().getIntExtra(AppConstants.EXTRA_TRADE_TYPE, 0);
        mPresenter = new TradeQrcodePayPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mTradeType = getIntent().getIntExtra(AppConstants.EXTRA_TRADE_TYPE, 0);
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "支付");

        mAmountText = (TextView) findViewById(R.id.tv_qrcode_amount);
        mQRCodeErrorLayout = (LinearLayout) findViewById(R.id.layout_qrcode_error);
        mQRCodeErrorText = (TextView) findViewById(R.id.tv_qrcode_error);
        mQRCodeImg = (ImageView) findViewById(R.id.img_scan_qrcode);
        mQRCodeExpireText = (TextView) findViewById(R.id.tv_qrcode_expire_tip);

        mScanTipText = (ImageView) findViewById(R.id.img_scan_tip);
        mScanStatusLayout = (RelativeLayout) findViewById(R.id.ly_scan_status);

        mGiftActivityText = (TextView) findViewById(R.id.tv_pay_activity);
        mGiftActivityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onGiftActivity();
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
    public void setPresenter(TradeQrcodePayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void setAmount(String amount) {
        mAmountText.setText(amount);
    }

    @Override
    public void setQRCode(Bitmap bitmap) {
        mQRCodeImg.setImageBitmap(bitmap);
    }

    @Override
    public void updateScanStatus() {
        mScanStatusLayout.setVisibility(View.VISIBLE);
        mScanTipText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showQrError(String error) {
        mQRCodeErrorLayout.setVisibility(View.VISIBLE);
        mQRCodeImg.setVisibility(View.GONE);
        mQRCodeErrorText.setText(error);
    }

    @Override
    public void showQrSuccess() {
        mQRCodeErrorLayout.setVisibility(View.GONE);
        mQRCodeImg.setVisibility(View.VISIBLE);
        mQRCodeExpireText.setVisibility(View.VISIBLE);
    }

    @Override
    public void showGiftActivity() {
        mGiftActivityText.setVisibility(View.VISIBLE);
    }

    @Override
    public int getType() {
        return mTradeType;
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onKeyEventBack();
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(QRScanStatusEvent qrScanStatusEvent) {
        // 主线程更新扫码状态
        updateScanStatus();
    }
}
