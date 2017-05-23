package com.xmd.cashier.contract;

import android.content.Intent;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-4-13.
 */

public interface CashierContract {
    interface Presenter extends BasePresenter {
        void onClickSetCouponInfo();

        void onDiscountMoneyChanged();

        void onOriginMoneyChanged();

        void onClickCashier();

        void onClickMemberPay();

        void onClickXMDOnlinePay();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        boolean checkInput();
    }

    interface View extends BaseView<CashierContract.Presenter> {

        void setOriginMoney(int value);

        void setDiscountMoney(int value);

        int getOriginMoney();

        int getDiscountMoney();

        void setFinallyMoney(String value);

        void setCouponButtonText(String text);

        void showLoading();

        void hideLoading();

        void showError(String error);

        void showToast(String toast);
    }
}
