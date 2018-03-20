package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-8-17.
 */

public interface TradeMarkPayContract {
    interface Presenter extends BasePresenter {
        void onMark();

        void onNavigationBack();
    }

    interface View extends BaseView<Presenter> {
        void showAmount(String amount);

        void showChannelName(String name);

        void showChannelDesc(String desc);

        int getType();
    }
}
