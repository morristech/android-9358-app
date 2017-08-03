package com.xmd.cashier.contract;

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

        void onClickPosPay();

        void onClickMemberPay();

        void onClickXMDOnlinePay();

        void doCashier();

        boolean checkInput();
    }

    interface View extends BaseView<CashierContract.Presenter> {

        void setOriginMoney(int value);

        void setDiscountMoney(int value);

        int getOriginMoney();

        int getDiscountMoney();

        void setFinallyMoney(String value);

        void setCouponButtonText(String text);
    }
}
