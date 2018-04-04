package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-11-4.
 */

public interface InnerResultContract {
    interface Presenter extends BasePresenter {
        void onDetail();

        void onDone();

        void onContinue();

        void onClose();

        void onEventBack();
    }

    interface View extends BaseView<Presenter> {
        void statusSuccess(String desc);

        void statusError(String error);

        void statusException();

        void showEnterAnim();

        void showExitAnim();

        void showStepView();
    }
}
