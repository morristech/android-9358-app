package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.CheckInfo;

import java.util.List;

/**
 * Created by zr on 17-3-14.
 */

public interface VerifyConfirmContract {
    interface Presenter extends BasePresenter {
        void onVerifyContinue();

        void onInfoClick(CheckInfo info);

        void onInfoSelected(CheckInfo info, boolean selected);

        void onInfoSelectedValid(CheckInfo info);
    }

    interface View extends BaseView<VerifyConfirmContract.Presenter> {
        void setSuccessText(int count);

        void setFailedText(int count);

        void setButtonText(int count);

        void showVerifyResultList(List<CheckInfo> list);

        void showError(String message);

        void showLoading();

        void hideLoading();

        void showToast(String message);
    }
}
