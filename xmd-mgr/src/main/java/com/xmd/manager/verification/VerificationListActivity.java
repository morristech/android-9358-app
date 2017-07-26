package com.xmd.manager.verification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CheckInfo;
import com.xmd.manager.beans.CheckInfoList;
import com.xmd.manager.beans.VerificationCouponDetailBean;
import com.xmd.manager.beans.VerificationSomeBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.journal.adapter.CommonVerificationListAdapter;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.VerificationSaveResult;
import com.xmd.manager.widget.EmptyView;
import com.xmd.manager.widget.VerificationAlertDialog;
import com.xmd.manager.window.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;


/**
 * Created by sdcm on 17-3-3.
 */

public class VerificationListActivity extends BaseActivity implements VerificationListListener {

    @BindView(R.id.coupon_empty_view)
    EmptyView mCouponEmptyView;
    @BindView(R.id.phone_number)
    TextView mPhoneNumber;
    @BindView(R.id.customer_coupon_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.selected_coupon_total)
    TextView mSelectedCouponTotal;
    @BindView(R.id.btn_verification)
    Button mBtnVerification;

    private static final int REQUEST_CODE_DETAIL = 0x1;
    private static final int REQUEST_CODE_FAIL = 0x2;

    private CommonVerificationListAdapter mVerificationListAdapter;
    private String mPhoneNumberValue;
    private Subscription mGetCustomerCouponListSubscription;
    private Subscription mVerificationResultSubscription;
    private Subscription mSaveResultSubscription;
    private String mSelectedTotal;
    private List<CheckInfo> mSelectedVerificationList = new ArrayList<>();
    private List<CheckInfo> mFailedVerificationList = new ArrayList<>();
    private int verificationSuccess = 0;
    private int verificationFail = 0;
    private int verificationCount = 0;
    //  private boolean mVerifying;
    private VerificationAlertDialog mVerificationDialog;
    private List<VerificationCouponDetailBean> discountList;
    private List<VerificationCouponDetailBean> normalCouponList;

    public static void startCustomerCouponListActivity(Activity activity, String phone) {
        Intent intent = new Intent(activity, VerificationListActivity.class);
        intent.putExtra(Constant.PARAM_PHONE_NUMBER, phone);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_info_list);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetCustomerCouponListSubscription, mVerificationResultSubscription, mSaveResultSubscription);
    }


    private void initView() {
        mSelectedTotal = "已选择 0 张";
        mBtnVerification.setEnabled(false);
        mSelectedCouponTotal.setText(Utils.changeColor(mSelectedTotal, ResourceUtils.getColor(R.color.main_btn_pressed), 3, mSelectedTotal.length() - 1));

        mVerificationResultSubscription = RxBus.getInstance()
                .toObservable(VerificationSomeBean.class)
                .subscribe(this::handleVerificationResult);

        mGetCustomerCouponListSubscription = RxBus.getInstance()
                .toObservable(CheckInfoList.class)
                .subscribe(this::handleGetCheckInfoListResult);

        mSaveResultSubscription = RxBus.getInstance()
                .toObservable(VerificationSaveResult.class)
                .subscribe(
                        verificationSaveResult -> loadData()
                );
        Intent data = getIntent();
        mPhoneNumberValue = data.getStringExtra(Constant.PARAM_PHONE_NUMBER);
        mCouponEmptyView.setEmptyPic(R.drawable.emtpy_coupons);
        mCouponEmptyView.setStatus(EmptyView.Status.Loading);


        mVerificationListAdapter = new CommonVerificationListAdapter();
        mVerificationListAdapter.setHandler(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setRemoveDuration(300);
        mRecyclerView.setItemAnimator(itemAnimator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mVerificationListAdapter);
    }


    private void loadData() {
        mFailedVerificationList.clear();
        mSelectedVerificationList.clear();
        verificationFail = 0;
        verificationSuccess = 0;
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CHECK_INFO, mPhoneNumberValue);
    }

    private void handleVerificationResult(VerificationSomeBean result) {
        //hideLoading();
        if (!result.verificationSucceed) {//核销失败
            verificationFail++;
            for (int i = 0; i < mSelectedVerificationList.size(); i++) {
                if (result.couponNo.equals(mSelectedVerificationList.get(i).getCode())) {
                    mFailedVerificationList.add(mSelectedVerificationList.get(i));
                }
            }
        } else {//核销成功
            verificationSuccess++;
            int index = findByCode(result.couponNo);
            if (index >= 0) {
                CheckInfo checkInfo = mVerificationListAdapter.getDataList().get(index);
                mSelectedVerificationList.remove(checkInfo);
                mVerificationListAdapter.getDataList().remove(index);
                mVerificationListAdapter.notifyItemRemoved(index);
                showSelectedView();
            }
        }
        if ((verificationFail + verificationSuccess) == verificationCount) {
            //    mVerifying = false;
            if (verificationFail > 0) {
                //部分核销成功
                Intent intent = new Intent(VerificationListActivity.this, VerificationCouponsFailActivity.class);
                intent.putParcelableArrayListExtra(VerificationCouponsFailActivity.FAILED_COUPONS_LIST, (ArrayList<? extends Parcelable>) mFailedVerificationList);
                intent.putExtra(VerificationCouponsFailActivity.VERIFICATION_COUPONS_SUCCESS_TOTAL, mSelectedVerificationList.size() - mFailedVerificationList.size());
                startActivityForResult(intent, REQUEST_CODE_FAIL);
                mFailedVerificationList.clear();
            } else if (verificationSuccess > 0) {
                //完全核销成功
                XToast.show("共核销" + verificationSuccess + "张券，核销成功" + verificationSuccess + "张券");
            }
        }
    }

    private int findByCode(String code) {
        List<CheckInfo> list = mVerificationListAdapter.getDataList();
        for (int i = 0; i < list.size(); i++) {
            if (code.equals(list.get(i).getCode())) {
                return i;
            }
        }
        return -1;
    }

    private void handleGetCheckInfoListResult(CheckInfoList result) {
        if (result.statusCode == 200) {
            onGetUserCouponListSucceeded(result.respData);
        } else {
            onGetUserCouponListFailed(result.msg);
        }
    }

    private void onGetUserCouponListSucceeded(List<CheckInfo> checkInfoList) {
        if (checkInfoList == null || checkInfoList.isEmpty()) {
            mCouponEmptyView.setEmptyTip("没有可核销物品");
            mCouponEmptyView.setStatus(EmptyView.Status.Empty);
            return;
        }
        mSelectedTotal = "已选择 0 张";
        mSelectedCouponTotal.setText(Utils.changeColor(mSelectedTotal, ResourceUtils.getColor(R.color.main_btn_pressed), 3, mSelectedTotal.length() - 1));
        mBtnVerification.setEnabled(false);
        mCouponEmptyView.setStatus(EmptyView.Status.Gone);
        mPhoneNumber.setText(mPhoneNumberValue);

        mVerificationListAdapter.setData(checkInfoList);
    }

    private void onGetUserCouponListFailed(String errorMsg) {
        mCouponEmptyView.setEmptyTip(errorMsg);
        mCouponEmptyView.setStatus(EmptyView.Status.Empty);
    }


    @OnClick(R.id.btn_verification)
    public void onClick() {
        //  showLoading();
        verificationSuccess = 0;
        verificationFail = 0;
        mFailedVerificationList.clear();
        verificationCount = mSelectedVerificationList.size();
        if (discountList == null) {
            discountList = new ArrayList<>();
        } else {
            discountList.clear();
        }
        if (normalCouponList == null) {
            normalCouponList = new ArrayList<>();
        } else {
            normalCouponList.clear();
        }
        for (int i = 0; i < mSelectedVerificationList.size(); i++) {
            if (mSelectedVerificationList.get(i).getType().equals("discount_coupon")) {
                discountList.add((VerificationCouponDetailBean) mSelectedVerificationList.get(i).getInfo());
            } else {
                normalCouponList.add((VerificationCouponDetailBean) mSelectedVerificationList.get(i).getInfo());
            }
        }

        if (discountList.size() > 0) {
            mVerificationDialog = new VerificationAlertDialog(this, discountList);
            mVerificationDialog.show();
            mVerificationDialog.setVerificationListener(new VerificationAlertDialog.VerificationSuccessListener() {
                @Override
                public void verificationSuccess(boolean canUse, float money) {
                    if (canUse) {
                        for (int i = 0; i < mSelectedVerificationList.size(); i++) {
                            Map<String, String> params = new HashMap<>();
                            if (money > 0) {
                                params.put(RequestConstant.KEY_VERIFICATION_AMOUNT, String.valueOf((int) money * 100));
                            } else {
                                params.put(RequestConstant.KEY_VERIFICATION_AMOUNT, "0");
                            }
                            params.put(RequestConstant.KEY_VERIFICATION_CODE, mSelectedVerificationList.get(i).getCode());
                            params.put(RequestConstant.KEY_VERIFICATION_TYPE, "");
                            params.put(RequestConstant.KEY_VERIFICATION_SOME, "some");
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_COMMON_SAVE, params);
                        }
                    } else {
                        //        hideLoading();
                        Toast.makeText(VerificationListActivity.this, "所选折扣券不满足使用条件,请重新选择", Toast.LENGTH_LONG).show();
                        return;

                    }

                }
            });
        }

        if (normalCouponList.size() > 0) {
            for (int i = 0; i < normalCouponList.size(); i++) {
                Map<String, String> params = new HashMap<>();
                params.put(RequestConstant.KEY_VERIFICATION_AMOUNT, "0");
                params.put(RequestConstant.KEY_VERIFICATION_CODE, mSelectedVerificationList.get(i).getCode());
                params.put(RequestConstant.KEY_VERIFICATION_TYPE, "");
                params.put(RequestConstant.KEY_VERIFICATION_SOME, "some");
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_COMMON_SAVE, params);
            }
        }


    }

    @Override
    public void onItemClicked(CheckInfo checkInfo) {
//        if (mVerifying) {
//            return;
//        }
        Intent intent = new Intent(this, VerificationActivity.class);
        intent.putExtra(VerificationActivity.EXTRA_DATA, checkInfo);
        startActivityForResult(intent, REQUEST_CODE_DETAIL);
    }

    @Override
    public void onItemChecked(boolean isChecked, CheckInfo checkInfo) {
//        if (mVerifying) {
//            return;
//        }

        if (isChecked) {
            mSelectedVerificationList.add(checkInfo);
        } else {
            mSelectedVerificationList.remove(checkInfo);
        }
        checkInfo.setSelected(isChecked);
        showSelectedView();
    }

    private void showSelectedView() {
        mSelectedTotal = "已选择 " + mSelectedVerificationList.size() + " 张";
        mSelectedCouponTotal.setText(Utils.changeColor(mSelectedTotal, ResourceUtils.getColor(R.color.main_btn_pressed), 3, mSelectedTotal.length() - 1));
        mBtnVerification.setEnabled(mSelectedVerificationList.size() > 0);
    }
}
