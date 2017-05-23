package com.xmd.cashier.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
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
    private TextView mScanTipText;
    private RelativeLayout mScanStatusLayout;
    private TextView mCancelText;

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

        mQRCodeImg = (ImageView) findViewById(R.id.img_scan_qrcode);

        mScanTipText = (TextView) findViewById(R.id.tv_scan_tip);
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
