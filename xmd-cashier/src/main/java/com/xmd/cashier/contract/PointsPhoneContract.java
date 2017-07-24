package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by heyangya on 16-8-23.
 */

public interface PointsPhoneContract {
    interface Presenter extends BasePresenter {
        void onClickOk();

        void onClickCancel();
    }

    interface View extends BaseView<Presenter> {

        void showPoints(int points);

        String getPhone();

        void setPhone(String phone);
    }
}
