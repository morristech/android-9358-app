package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by heyangya on 16-8-23.
 */

public interface PayTypeChoiceContract {
    interface Presenter extends BasePresenter {
        void onClickPay();

        void onCancel();

        void onSelectPayType(int payType);
    }

    interface View extends BaseView<Presenter> {
        void showNeedPayMoney(String money);

        void showPayType(int payType);

        void setPayButtonEnable(boolean enable);
    }
}
