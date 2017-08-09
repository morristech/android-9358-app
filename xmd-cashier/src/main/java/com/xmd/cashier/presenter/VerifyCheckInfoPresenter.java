package com.xmd.cashier.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xmd.cashier.R;
import com.xmd.cashier.activity.VerifyConfirmActivity;
import com.xmd.cashier.activity.VerifyCouponActivity;
import com.xmd.cashier.activity.VerifyOrderActivity;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyCheckInfoContract;
import com.xmd.cashier.dal.bean.CheckInfo;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.net.response.CheckInfoListResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;
import com.xmd.cashier.widget.VerifyDiscountDialog;

import java.util.List;

import rx.Subscription;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zr on 17-5-19.
 **/

public class VerifyCheckInfoPresenter implements VerifyCheckInfoContract.Presenter {
    private final static int REQUEST_CODE_CONSUME = 1;
    private Context mContext;
    private VerifyCheckInfoContract.View mView;

    private Subscription mLoadCheckInfoSubscription;
    private Subscription mVerifyCheckInfoSubscription;

    private String mPhone;

    public VerifyCheckInfoPresenter(Context context, VerifyCheckInfoContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        mPhone = null;
        VerifyManager.getInstance().clearVerifyList();
        if (mLoadCheckInfoSubscription != null) {
            mLoadCheckInfoSubscription.unsubscribe();
        }
        if (mVerifyCheckInfoSubscription != null) {
            mVerifyCheckInfoSubscription.unsubscribe();
        }
    }

    // 初始化列表
    public void resetList() {
        mView.clearCheckInfo();    // 列表清空
        VerifyManager.getInstance().clearVerifyList();  // 核销数据清空
    }

    @Override
    public void setPhone(String phone) {
        mPhone = phone;
    }

    @Override
    public void onActivityResult(Intent intent, int requestCode, int resultCode) {
        switch (requestCode) {
            case REQUEST_CODE_CONSUME:
                //核销券成功后,更新券列表
                if (resultCode == RESULT_OK) {
                    resetList();
                    onLoad();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoad() {
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
        }
        if (mLoadCheckInfoSubscription != null) {
            mLoadCheckInfoSubscription.unsubscribe();
        }
        mLoadCheckInfoSubscription = VerifyManager.getInstance().getCheckInfoList(mPhone, new Callback<CheckInfoListResult>() {
            @Override
            public void onSuccess(CheckInfoListResult o) {
                mView.hideLoading();
                mView.showCheckInfo(VerifyManager.getInstance().getVerifyList());
                mView.showBottomLayout();
                mView.updateBottomLayout(VerifyManager.getInstance().getSelectedCount());
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    @Override
    public void onVerify() {
        // 判断是否有选中
        if (VerifyManager.getInstance().getSelectedCount() <= 0) {
            mView.showError("请先选择需要核销的券");
            return;
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (VerifyManager.getInstance().hasDiscount()) {
            setDiscount();
        } else {
            doVerifyRequest();
        }
    }

    private void doVerifyRequest() {
        if (mVerifyCheckInfoSubscription != null) {
            mVerifyCheckInfoSubscription.unsubscribe();
        }
        mView.showLoading();
        mVerifyCheckInfoSubscription = VerifyManager.getInstance().verifyCheckInfo(new Callback<List<CheckInfo>>() {
            @Override
            public void onSuccess(List<CheckInfo> o) {
                mView.hideLoading();
                if (VerifyManager.getInstance().hasFailed()) {
                    gotoVerifyResultActivity(mContext);
                } else {
                    mView.showError("全部核销成功");
                    // 刷新列表
                    resetList();
                    onLoad();
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                gotoVerifyResultActivity(mContext);
            }
        });
    }

    private void setDiscount() {
        final List<CouponInfo> discounts = VerifyManager.getInstance().getDiscountList();
        final VerifyDiscountDialog dialog = new VerifyDiscountDialog(mContext, discounts);
        dialog.show();
        dialog.setCallBack(new VerifyDiscountDialog.CallBack() {
            @Override
            public void onNegative() {
                dialog.dismiss();
                // 取消时清空设置的消费金额
                for (CouponInfo info : discounts) {
                    info.originAmount = 0;
                }
            }

            @Override
            public void onPositive(String input) {
                if (TextUtils.isEmpty(input)) {
                    mView.showToast("请输入消费金额");
                    return;
                }
                dialog.dismiss();
                for (CouponInfo info : discounts) {
                    info.originAmount = Utils.stringToMoney(input);
                }
                doVerifyRequest();
            }
        });
    }

    private void gotoVerifyResultActivity(Context context) {
        Intent intent = new Intent(context, VerifyConfirmActivity.class);
        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_CONSUME);
    }

    @Override
    public void onItemClick(CheckInfo info) {
        switch (info.getInfoType()) {
            case AppConstants.CHECK_INFO_TYPE_COUPON:
                Intent couponIntent = new Intent(mContext, VerifyCouponActivity.class);
                couponIntent.putExtra(AppConstants.EXTRA_COUPON_VERIFY_INFO, (CouponInfo) info.getInfo());
                couponIntent.putExtra(AppConstants.EXTRA_IS_SHOW, true);
                ((Activity) mContext).startActivityForResult(couponIntent, REQUEST_CODE_CONSUME);
                break;
            case AppConstants.CHECK_INFO_TYPE_ORDER:
                Intent orderIntent = new Intent(mContext, VerifyOrderActivity.class);
                orderIntent.putExtra(AppConstants.EXTRA_ORDER_VERIFY_INFO, (OrderInfo) info.getInfo());
                orderIntent.putExtra(AppConstants.EXTRA_IS_SHOW, true);
                ((Activity) mContext).startActivityForResult(orderIntent, REQUEST_CODE_CONSUME);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelect(CheckInfo info, boolean selected) {
        VerifyManager.getInstance().setItemSelectedStatus(info, selected);
        mView.updateBottomLayout(VerifyManager.getInstance().getSelectedCount());
    }

    @Override
    public void onItemSelectValid(CheckInfo info) {
        mView.showError("该券不在可用时间段内");
    }
}
