package com.xmd.cashier.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.contract.MemberScanContract;
import com.xmd.cashier.dal.event.RechargeFinishEvent;
import com.xmd.cashier.presenter.MemberScanPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zr on 17-7-11.
 * 微信 支付宝 收款
 */

public class MemberScanActivity extends BaseActivity implements MemberScanContract.View {
    private MemberScanContract.Presenter mPresenter;

    private TextView mScanContentText;
    private TextView mScanDescText;
    private TextView mScanPlanOrAmountText;
    private TextView mScanAmountText;
    private LinearLayout mScanNormalLayout;
    private ImageView mScanQrcodeImg;
    private RelativeLayout mScanSuccessLayout;
    private Button mScanPrintBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_scan);
        mPresenter = new MemberScanPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "扫码付款");
        mScanContentText = (TextView) findViewById(R.id.tv_scan_content);
        mScanDescText = (TextView) findViewById(R.id.tv_scan_desc);
        mScanPlanOrAmountText = (TextView) findViewById(R.id.tv_scan_plan_or_amount);
        mScanAmountText = (TextView) findViewById(R.id.tv_scan_amount);

        mScanNormalLayout = (LinearLayout) findViewById(R.id.layout_scan_normal);
        mScanQrcodeImg = (ImageView) findViewById(R.id.img_scan_qrcode);

        mScanSuccessLayout = (RelativeLayout) findViewById(R.id.layout_scan_success);
        mScanPrintBtn = (Button) findViewById(R.id.btn_scan_print);

        mScanPrintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.printResult();
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
    public void setPresenter(MemberScanContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showScanInfo(String content, String subTitle, String subDetail, String amount) {
        mScanContentText.setText(content);
        mScanDescText.setText(subTitle);
        mScanPlanOrAmountText.setText(subDetail);
        mScanAmountText.setText(amount);
    }

    @Override
    public void showQrcode(Bitmap bitmap) {
        mScanQrcodeImg.setImageBitmap(bitmap);
    }

    @Override
    public void showScanSuccess() {
        mScanNormalLayout.setVisibility(View.GONE);
        mScanSuccessLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onKeyEventBack();
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RechargeFinishEvent event) {
        showScanSuccess();
    }
}
