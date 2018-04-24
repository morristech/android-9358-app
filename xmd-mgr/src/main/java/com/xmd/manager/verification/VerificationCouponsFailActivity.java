package com.xmd.manager.verification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.R;
import com.xmd.manager.beans.CheckInfo;
import com.xmd.manager.beans.VerificationSomeBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.journal.adapter.CommonVerificationListAdapter;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.window.BaseActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 17-3-6.
 */

public class VerificationCouponsFailActivity extends BaseActivity implements VerificationListListener {
    public static String FAILED_COUPONS_LIST = "failed_coupons_list";
    public static String VERIFICATION_COUPONS_SUCCESS_TOTAL = "verification_coupons_success_total";

    @BindView(R.id.verification_success_total)
    TextView mVerificationSuccessTotal;
    @BindView(R.id.verification_failed_total)
    TextView mVerificationFailedTotal;
    @BindView(R.id.customer_fail_coupon_list)
    RecyclerView mCustomerCouponList;
    @BindView(R.id.btn_verification_again)
    Button mBtnVerificationAgain;

    private static final int REQUEST_CODE_DETAIL = 0x1;

    private List<CheckInfo> mFailedList;
    private int mSuccessCount;
    private CommonVerificationListAdapter mVerificationListAdapter;
    private Subscription mVerificationResultSubscription;
    private int verificationCount = 0;
    private int verificationResult = 0;
    private int mSelectedCount = 0;
    private boolean mVerifying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_coupons);
        ButterKnife.bind(this);
        mFailedList = getIntent().getParcelableArrayListExtra(FAILED_COUPONS_LIST);
        for (CheckInfo checkInfo : mFailedList) {
            checkInfo.setSelected(true);
        }
        mSuccessCount = getIntent().getIntExtra(VERIFICATION_COUPONS_SUCCESS_TOTAL, 0);
        showSuccessAndFailedCount(mSuccessCount, mFailedList.size());
        showSelectView(mSelectedCount);
        mCustomerCouponList.setLayoutManager(new LinearLayoutManager(this));
        mCustomerCouponList.setItemAnimator(new DefaultItemAnimator());
        mVerificationListAdapter = new CommonVerificationListAdapter();
        mVerificationListAdapter.setHandler(this);
        mVerificationListAdapter.setData(mFailedList);
        mCustomerCouponList.setAdapter(mVerificationListAdapter);

        mVerificationResultSubscription = RxBus.getInstance().toObservable(VerificationSomeBean.class).subscribe(
                this::handleVerificationResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mVerificationResultSubscription);
    }

    private void handleVerificationResult(VerificationSomeBean result) {
        verificationResult++;
        if (result.verificationSucceed) {
            int removedIndex = -1;
            for (int i = 0; i < mFailedList.size(); i++) {
                if (mFailedList.get(i).getCode().equals(result.couponNo)) {
                    removedIndex = i;
                    break;
                }
            }
            mFailedList.remove(removedIndex);
            mVerificationListAdapter.notifyItemRemoved(removedIndex);
            mSuccessCount++;
            mSelectedCount--;
            showSuccessAndFailedCount(mSuccessCount, mFailedList.size());
            showSelectView(mSelectedCount);
        } else {
            XToast.show("核销失败：" + result.message);
        }
        if (verificationCount == verificationResult) {
            mVerifying = false;
            hideLoading();
        }
    }

    @OnClick({R.id.btn_verification_again})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verification_again:
                showLoading();
                mVerifying = true;
                verificationResult = 0;
                verificationCount = mFailedList.size();
                for (int i = 0; i < mFailedList.size(); i++) {
                    if (mFailedList.get(i).getSelected()) {
                        Map<String, String> params = new HashMap<>();
                        params.put(RequestConstant.KEY_VERIFICATION_AMOUNT, "0");
                        params.put(RequestConstant.KEY_VERIFICATION_CODE, mFailedList.get(i).getCode());
                        params.put(RequestConstant.KEY_VERIFICATION_TYPE, "");
                        params.put(RequestConstant.KEY_VERIFICATION_SOME, "some");
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_COMMON_SAVE, params);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        this.finish();
    }

    private void showSuccessAndFailedCount(int verificationTotal, int failedTotal) {
        String mVerificationTotalDes = verificationTotal + " 张券已核销成功";
        String mVerificationFailedDes = String.format("以下%s张券核销失败，点击重新核销", failedTotal);
        mVerificationSuccessTotal.setText(Utils.changeColor(mVerificationTotalDes, ResourceUtils.getColor(R.color.primary_color), 0, mVerificationTotalDes.length() - 7));
        if (failedTotal > 0) {
            mVerificationFailedTotal.setVisibility(View.VISIBLE);
            mVerificationFailedTotal.setText(Utils.changeColor(mVerificationFailedDes, ResourceUtils.getColor(R.color.primary_color), 2, mVerificationFailedDes.length() - 13));
        } else {
            XToast.show("所有券都已核销成功！");
            setResult(RESULT_OK);
            finish();
        }
    }

    private void showSelectView(int selectedTotal) {
        String mSelectedVerificationDes;
        if (selectedTotal > 0) {
            mBtnVerificationAgain.setEnabled(true);
            mSelectedVerificationDes = "已选择 " + selectedTotal + " 张,重新核销";
            mBtnVerificationAgain.setText(Utils.changeColor(mSelectedVerificationDes, ResourceUtils.getColor(R.color.main_btn_pressed), 3, mSelectedVerificationDes.length() - 5));
        } else {
            mSelectedVerificationDes = "已选择 0 张";
            mBtnVerificationAgain.setText(Utils.changeColor(mSelectedVerificationDes, ResourceUtils.getColor(R.color.main_btn_pressed), 3, mSelectedVerificationDes.length() - 1));
            mBtnVerificationAgain.setEnabled(false);
        }
    }


    @Override
    public void onItemClicked(CheckInfo checkInfo) {
        if (mVerifying) {
            return;
        }
        Intent intent = new Intent(this, VerificationActivity.class);
        intent.putExtra(VerificationActivity.EXTRA_DATA, checkInfo);
        startActivityForResult(intent, REQUEST_CODE_DETAIL);
    }

    @Override
    public void onItemChecked(boolean isChecked, CheckInfo checkInfo) {
        if (mVerifying) {
            return;
        }
        checkInfo.setSelected(isChecked);
        if (isChecked) {
            mSelectedCount++;
        } else {
            mSelectedCount--;
        }
        showSelectView(mSelectedCount);
    }
}
