package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-5-16.
 */

public interface CashierResultContract {
    interface Presenter extends BasePresenter {
        void onConfirm();

        void onPrint();
    }

    interface View extends BaseView<Presenter> {
        void showStatus(int status);

        void showStatusError(String error);

        void showStatusSuccess(String desc);

        void showPrint();
    }
}
