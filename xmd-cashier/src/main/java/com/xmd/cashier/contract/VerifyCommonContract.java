package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.CommonVerifyInfo;

/**
 * Created by zr on 16-12-12.
 */

public interface VerifyCommonContract {
    interface Presenter extends BasePresenter {
        void onClickVerify(CommonVerifyInfo info);
    }

    interface View extends BaseView<Presenter> {
        void showLoadingView();

        void hideLoadingView();

        String getCode();

        String getType();

        String getAmount();

        boolean needAmount();
    }
}
