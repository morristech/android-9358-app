package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-8-17.
 */

public interface CashPayContract {
    interface Presenter extends BasePresenter {
        void onCashPay();

        void onNavigationBack();
    }

    interface View extends BaseView<Presenter> {
        void showAmount(String amount);

        void enableCashBtn();

        void disableCashBtn();

        void showCashBtn();

        void showCashSuccess();

        int getAmount();
    }
}
