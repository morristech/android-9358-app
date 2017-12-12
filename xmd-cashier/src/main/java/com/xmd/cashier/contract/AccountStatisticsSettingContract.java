package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-9-27.
 */

public interface AccountStatisticsSettingContract {
    interface View extends BaseView<Presenter> {
        void showStart(String startTime);

        void showEnd(String endTime);
    }

    interface Presenter extends BasePresenter {
        void onStartPick(android.view.View view);

        void onEndPick(android.view.View view);

        void onConfirm();
    }
}
