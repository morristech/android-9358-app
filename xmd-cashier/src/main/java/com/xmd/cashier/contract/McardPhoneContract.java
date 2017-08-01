package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-7-11.
 */

public interface McardPhoneContract {
    interface Presenter extends BasePresenter {
        void onConfirm();
    }

    interface View extends BaseView<Presenter> {
        String getPhone();

        void showEnterAnim();

        void showExitAnim();

        void showStepView(int cardModel);
    }
}
