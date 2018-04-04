package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-5-16.
 */

public interface CashierResultContract {
    interface Presenter extends BasePresenter {
        void onConfirm();
    }

    interface View extends BaseView<Presenter> {
        void statusSuccess(String desc);

        void statusError(String error);

        void statusException();
    }
}
