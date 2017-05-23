package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.SettleSummaryInfo;

import java.util.List;

/**
 * Created by zr on 17-3-29.
 */

public interface SettleCurrentContract {
    interface Presenter extends BasePresenter {
        void getSummary();

        void onSettle();
    }

    interface View extends BaseView<Presenter> {
        void onCurrentSuccess(SettleSummaryInfo info, List<SettleSummaryInfo> list);

        void onCurrentFailed();

        void onCurrentEmpty();

        void showToast(String toast);

        void showError(String error);

        void showLoading();

        void hideLoading();
    }
}
