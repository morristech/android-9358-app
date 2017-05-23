package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;

/**
 * Created by zr on 17-4-7.
 */

public interface SettleDetailContract {
    interface Presenter extends BasePresenter {
        void getSummaryById(String recordId);

        void onPrint(SettleSummaryResult.RespData respData);
    }

    interface View extends BaseView<Presenter> {
        void onDetailSuccess(SettleSummaryResult.RespData respData);

        void onDetailFailed();

        void showToast(String toast);

        void showError(String error);

        void showLoading();

        void hideLoading();
    }
}
