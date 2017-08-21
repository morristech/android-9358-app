package com.xmd.cashier.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.contract.ScanPayContract;
import com.xmd.cashier.presenter.ScanPayPresenter;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

/**
 * Created by zr on 17-5-12.
 * 小摩豆在线买单
 */

public class ScanPayActivity extends BaseActivity implements ScanPayContract.View {
    private ScanPayContract.Presenter mPresenter;

    private TextView mOriginText;
    private TextView mDiscountText;
    private TextView mPaidText;
    private ImageView mQRCodeImg;
    private ImageView mScanTipText;
    private RelativeLayout mScanStatusLayout;
    private TextView mCancelText;

    private LinearLayout mQRCodeLoadingLayout;
    private LinearLayout mQRCodeErrorLayout;
    private TextView mQRCodeRefreshText;
    private TextView mQRCodeErrorText;
    private TextView mQRCodeExpireText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pay);
        mPresenter = new ScanPayPresenter(this, this);

        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "买单收款", TOOL_BAR_NAV_NONE);

        mOriginText = (TextView) findViewById(R.id.tv_pay_origin_money);
        mDiscountText = (TextView) findViewById(R.id.tv_pay_discount_money);
        mPaidText = (TextView) findViewById(R.id.tv_pay_paid_money);

        mQRCodeLoadingLayout = (LinearLayout) findViewById(R.id.layout_qrcode_loading);
        mQRCodeErrorLayout = (LinearLayout) findViewById(R.id.layout_qrcode_error);
        mQRCodeRefreshText = (TextView) findViewById(R.id.tv_qrcode_refresh);
        mQRCodeErrorText = (TextView) findViewById(R.id.tv_qrcode_error);
        mQRCodeImg = (ImageView) findViewById(R.id.img_scan_qrcode);
        mQRCodeExpireText = (TextView) findViewById(R.id.tv_qrcode_expire_tip);
        mQRCodeRefreshText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 刷新二维码URL接口
                mPresenter.getQrcode();
            }
        });

        mScanTipText = (ImageView) findViewById(R.id.tv_scan_tip);
        mScanStatusLayout = (RelativeLayout) findViewById(R.id.ly_scan_status);
        mCancelText = (TextView) findViewById(R.id.tv_cancel);
        mCancelText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mCancelText.getPaint().setAntiAlias(true);
        mCancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCancel();
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
    public void setPresenter(ScanPayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void setOrigin(String origin) {
        mOriginText.setText(origin);
    }

    @Override
    public void setDiscount(String discount) {
        mDiscountText.setText(discount);
    }

    @Override
    public void setPaid(String paid) {
        mPaidText.setText(paid);
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
    public void showQrLoading() {
        mQRCodeLoadingLayout.setVisibility(View.VISIBLE);
        mQRCodeErrorLayout.setVisibility(View.GONE);
        mQRCodeImg.setVisibility(View.GONE);
    }

    @Override
    public void showQrError(String error) {
        mQRCodeLoadingLayout.setVisibility(View.GONE);
        mQRCodeErrorLayout.setVisibility(View.VISIBLE);
        mQRCodeImg.setVisibility(View.GONE);
        mQRCodeErrorText.setText(error);
    }

    @Override
    public void showQrSuccess() {
        mQRCodeLoadingLayout.setVisibility(View.GONE);
        mQRCodeErrorLayout.setVisibility(View.GONE);
        mQRCodeImg.setVisibility(View.VISIBLE);
        mQRCodeExpireText.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onCancel();
        return true;
    }

    @Override
    public void showError(String error) {
        new CustomAlertDialogBuilder(ScanPayActivity.this)
                .setMessage(error)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishSelf();
                    }
                })
                .create()
                .show();
    }
}
