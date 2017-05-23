package com.xmd.cashier.contract;

import android.content.Intent;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by heyangya on 16-8-23.
 */

public interface MemberPayContract {
    interface Presenter extends BasePresenter {
        void onClickOk();

        void onClickOtherPay();

        void onNavigationBack();

        void onActivityResult(int requestCode, int resultCode, Intent data);

    }

    interface View extends BaseView<Presenter> {
        void setBalance(String balance);

        void setCardNumber(String cardNumber);

        void setPhone(String phone);

        void setInfo(String info);

        void setDiscount(String discount);

        void setOriginPayMoney(String money);

        void setNeedPayMoneyEnough(String money);

        void setNeedpayMoneyNotEnough(String money);

        void showLoading();

        void hideLoading();

        void showError(String error);

        void showToast(String toast);
    }
}
