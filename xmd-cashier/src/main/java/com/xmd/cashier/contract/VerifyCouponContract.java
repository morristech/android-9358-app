package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.CouponInfo;

/**
 * Created by zr on 2017/4/16 0016.
 */

public interface VerifyCouponContract {
    interface Presenter extends BasePresenter {
        void onVerify(CouponInfo info);
    }

    interface View extends BaseView<Presenter> {
        void showLoading();

        void hideLoading();

        void showToast(String toast);

        void showError(String error);
    }
}
