package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.BillInfo;

import java.util.List;

/**
 * Created by zr on 16-11-28.
 */

public interface BillSearchContract {
    interface Presenter extends BasePresenter {
        void onBillItemClick(BillInfo info);

        void searchBills();

        void searchMoreBills();
    }

    interface View extends BaseView<BillSearchContract.Presenter> {
        String getTradeNo();

        void showKeyBoard();

        void hideKeyBoard();

        void showLoadIng();

        void hideLoadIng();

        void showLoadEmpty();

        void showLoadSuccess();

        void showMoreIng();

        void showMoreError();

        void showMoreNoNetwork();

        void showMoreNone();

        void showMoreSuccess();

        void setHasMore(boolean hasMore);

        void showBillData(List<BillInfo> list);
    }
}
