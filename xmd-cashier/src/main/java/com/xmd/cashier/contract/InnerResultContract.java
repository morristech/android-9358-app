package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-11-4.
 */

public interface InnerResultContract {
    interface Presenter extends BasePresenter {
        void onDetail();

        void onPrint();

        void onClose();

        void onEventBack();
    }

    interface View extends BaseView<Presenter> {
        void showSuccess();

        void showCancel();

        void showEnterAnim();

        void showExitAnim();

        void showStepView();
    }
}
