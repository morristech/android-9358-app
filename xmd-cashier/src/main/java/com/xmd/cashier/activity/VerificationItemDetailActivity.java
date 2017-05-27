package com.xmd.cashier.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.VerificationListAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.VerificationItemDetailContract;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.presenter.VerificationItemDetailPresenter;

public class VerificationItemDetailActivity extends BaseActivity implements VerificationItemDetailContract.View {
    public static final String EXTRA_VERIFICATION_ITEM = "extra_verification_item";
    private VerificationItemDetailContract.Presenter mPresenter;

    private TextView mNumberView;
    private TextView mValidTimeView;
    private TextView mObtainTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);

        showToolbar(R.id.toolbar, R.string.verification_item_detail_title);

        mPresenter = new VerificationItemDetailPresenter(this, this);

        mNumberView = (TextView) findViewById(R.id.tv_number);
        mValidTimeView = (TextView) findViewById(R.id.tv_valid_time);
        mObtainTimeView = (TextView) findViewById(R.id.tv_obtain_time);

        VerificationItem item = getIntent().getParcelableExtra(EXTRA_VERIFICATION_ITEM);
        showVerificationItem(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void setPresenter(VerificationItemDetailContract.Presenter presenter) {

    }

    @Override
    public void finishSelf() {
        finish();
    }


    private void showVerificationItem(VerificationItem item) {
        if (item.type.equals(AppConstants.TYPE_COUPON)) {
            mNumberView.setText(getFormatNumber(item.couponInfo.couponNo));
            mValidTimeView.setText(item.couponInfo.couponPeriod);
            mObtainTimeView.setText(item.couponInfo.getDate);
            ((WebView) findViewById(R.id.webview)).loadDataWithBaseURL(null, item.couponInfo.actContent, "text/html", "utf-8", null);
        }

        VerificationListAdapter.ViewHolder couponViewHolder = new VerificationListAdapter.ViewHolder(findViewById(R.id.coupon_view));
        couponViewHolder.hideCheckBox();
        couponViewHolder.bind(item);
    }

    private String getFormatNumber(String number) {
        String result = "";
        for (int i = 0; i < number.length(); i++) {
            result += number.charAt(i);
            if ((i + 1) % 4 == 0) {
                result += " ";
            }
        }
        return result;
    }
}
