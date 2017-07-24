package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-7-11.
 */

public interface MemberNavigationContract {
    interface Presenter extends BasePresenter {
        void onRecharge();

        void onPayment();

        void onCard();

        void onRecord();
    }

    interface View extends BaseView<Presenter> {
        void enterRecharge(boolean enter);

        void enterCard(boolean enter);

        void showEnterAnim();
    }
}
