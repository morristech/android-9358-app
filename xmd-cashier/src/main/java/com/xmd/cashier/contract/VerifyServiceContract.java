package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 16-12-12.
 */

public interface VerifyServiceContract {
    interface Presenter extends BasePresenter {
        void onClickVerify();
    }

    interface View extends BaseView<Presenter> {
        void showLoadingView();

        void hideLoadingView();

        void showToast(String msg);

        void showError(String msg);

        String getCode();
    }
}
