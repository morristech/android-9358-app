package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.BillInfo;

import java.util.List;

/**
 * Created by zr on 16-11-23.
 * 交易记录
 */

public interface BillRecordContract {
    interface Presenter extends BasePresenter {
        void loadBills(int timeSelect, int typeSelect, int statusSelect);

        void loadMoreBills(int timeSelect, int typeSelect, int statusSelect);

        void onClickSearch();

        void onBillItemClick(BillInfo info);

        void onTimePopClick();

        void onTypePopClick();

        void onStatusPopClick();

        void dismissTimePop();

        void dismissTypePop();

        void dismissStatusPop();
    }

    interface View extends BaseView<BillRecordContract.Presenter> {
        void showTimePop();

        void showTypePop();

        void showStatusPop();

        void updateSumInfo(int account, int amount);

        void hideSumInfo();

        void resetTimePop();

        void resetTypePop();

        void resetStatusPop();

        void showLoadIng();

        void hideLoadIng();

        void showLoadError();

        void showLoadEmpty();

        void showLoadNoNetwork();

        void showLoadSuccess();

        void showMoreIng();

        void showMoreError();

        void showMoreNoNetwork();

        void showMoreNone();

        void showMoreSuccess();

        void showBillData(List<BillInfo> list);

        void onRefresh();

        void setHasMore(boolean hasMore);
    }
}
