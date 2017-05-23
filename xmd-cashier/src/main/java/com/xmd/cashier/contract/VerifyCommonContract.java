package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 16-12-12.
 */

public interface VerifyCommonContract {
    interface Presenter extends BasePresenter {
        void onClickVerify();

        void onClickCancel();
    }

    interface View extends BaseView<Presenter> {
        void showLoadingView();

        void hideLoadingView();

        String getCode();

        String getType();

        String getAmount();

        boolean needAmount();

        void showToast(String msg);

        void showError(String msg);
    }
}
