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

        void onPrint();

        void onClose();

        void onEventBack();
    }

    interface View extends BaseView<Presenter> {
        void showSuccess(String desc);

        void showCancel(String error);

        void showDone(String desc);

        void showContinue(String desc);

        void showNotice();

        void showInit();

        void showEnterAnim();

        void showExitAnim();

        void showStepView();
    }
}
