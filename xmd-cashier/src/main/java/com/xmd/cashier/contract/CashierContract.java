package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-4-13.
 */

public interface CashierContract {
    interface Presenter extends BasePresenter {
        void onChannel();

        void onClickSetCouponInfo();

        void onOriginMoneyChanged();
    }

    interface View extends BaseView<CashierContract.Presenter> {
        void setOriginMoney(int value);

        void setDiscountMoney(int value);

        int getOriginMoney();

        void setFinallyMoney(String value);

        void setCouponButtonText(String text);
    }
}
