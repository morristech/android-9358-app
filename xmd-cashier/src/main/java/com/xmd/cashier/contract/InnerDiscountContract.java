package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.OrderDiscountInfo;
import com.xmd.cashier.dal.bean.VerificationItem;

import java.util.List;

/**
 * Created by zr on 17-12-11.
 */

public interface InnerDiscountContract {
    interface Presenter extends BasePresenter {
        void confirmDiscount();

        void searchDiscount();

        void onReductionChange();

        void onVerifySelect(VerificationItem item,int position);

        void onVerifyClick(VerificationItem item, int position);

        void onVerifiedClick(String code);
    }

    interface View extends BaseView<Presenter> {
        void setReductionMoney(int value);

        void setDiscountMoney(int value);

        int getReductionMoney();

        String getSearchContent();

        void showKeyboard();

        void hideKeyboard();

        void showVerifyData(List<VerificationItem> list);

        void showVerifyList();

        void hideVerifyList();

        void updateVerifyItem(int position);

        void showVerifiedLayout(List<OrderDiscountInfo> list);

        void hideVerifiedLayout();

        void showFlowData(List<String> list);
    }
}
