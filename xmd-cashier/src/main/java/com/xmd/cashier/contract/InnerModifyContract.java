package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-12-18.
 */

public interface InnerModifyContract {
    interface Presenter extends BasePresenter {
        void onRealPayChange();

        void onCashier();

        void processData();

        void onEventBack();
    }

    interface View extends BaseView<Presenter> {
        void showKeyboard();

        void hideKeyboard();

        void setOrigin(String origin);

        void setDiscount(String discount);

        void setAlreadyPay(String alreadyPay);

        void setLeftPay(String leftPay);

        void setInput(int input);

        void setDesc(int input, int left);

        int getRealPayMoney();
    }
}
