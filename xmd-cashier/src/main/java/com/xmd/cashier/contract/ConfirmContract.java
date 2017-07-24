package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.VerificationItem;

/**
 * Created by heyangya on 16-8-24.
 */

public interface ConfirmContract {
    interface Presenter extends BasePresenter {
        void onClickCancel();

        void onClickOk();

        void onCouponMoneyChanged();

        void onVerificationItemClicked(VerificationItem item);
    }

    interface View extends BaseView<ConfirmContract.Presenter> {

        void setOriginMoney(int value);

        void setDiscountMoney(int value);

        void setFinallyMoney(int value);

        void showTradeStatusInfo(Trade trade);

        String getStartShowMessage(); //返回需要确认的信息

        int getDiscountMoney();


        void hideKeyboard();

        void hideCouponView();

        void hideCancelButton();

    }
}
