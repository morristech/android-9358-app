package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;

/**
 * Created by zr on 17-3-29.
 */

public interface SettleCurrentContract {
    interface Presenter extends BasePresenter {
        void onSettle();

        void getSettle();
    }

    interface View extends BaseView<Presenter> {
        void initLayout();

        void onCurrentSuccess(SettleSummaryResult.RespData detailData);

        void onCurrentFailed(String error);

        void onCurrentEmpty();
    }
}
