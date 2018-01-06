package com.xmd.cashier.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.ScanPayResultContract;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.presenter.ScanPayResultPresenter;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

/**
 * Created by zr on 17-5-16.
 */

public class ScanPayResultActivity extends BaseActivity implements ScanPayResultContract.View {
    private static final String TAG = "ScanPayResultPresenter";
    private ScanPayResultContract.Presenter mPresenter;
    private TextView mPayAmount;
    private TextView mPayUserName;
    private Button mPayConfirm;

    private OnlinePayInfo mInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pay_result);
        mPresenter = new ScanPayResultPresenter(this, this);
        mInfo = (OnlinePayInfo) getIntent().getSerializableExtra(AppConstants.EXTRA_ONLINE_PAY_INFO);
        if (mInfo == null) {
            XLogger.e(TAG, " exit : 支付中出现异常，请在买单列表中确认支付状态");
            showError("支付中出现异常，请在买单列表中确认支付状态");
            return;
        }

        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        mPayAmount = (TextView) findViewById(R.id.tv_pay_amount);
        mPayUserName = (TextView) findViewById(R.id.tv_user_name);
        mPayConfirm = (Button) findViewById(R.id.btn_pay_confirm);

        mPayAmount.setText(String.format(getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(mInfo.payAmount)));
        mPayUserName.setText(mInfo.userName);
        mPayConfirm.setOnClickListener(new View.OnClickListener() {
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
        XLogger.i(TAG, "补收款微信支付宝支付成功回退(onKeyEventBack)");
        return super.onKeyEventBack();
    }

    @Override
    public void setPresenter(ScanPayResultContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showError(String error) {
        new CustomAlertDialogBuilder(ScanPayResultActivity.this)
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
