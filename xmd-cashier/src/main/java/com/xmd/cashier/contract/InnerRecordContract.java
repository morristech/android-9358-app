package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.TradeRecordInfo;

import java.util.List;

/**
 * Created by zr on 17-11-1.
 */

public interface InnerRecordContract {
    interface Presenter extends BasePresenter {
        void load(boolean init);

        void loadMore();

        void onDetail(TradeRecordInfo info);

        void onPay(TradeRecordInfo info);

        void printClient(TradeRecordInfo info);

        void printClub(TradeRecordInfo info);
    }

    interface View extends BaseView<Presenter> {
        void clearData();

        void showData(List<TradeRecordInfo> list);

        void showRefreshIng();

        void showRefreshError();

        void showRefreshEmpty();

        void showRefreshNoNetwork();

        void showRefreshSuccess();

        void showMoreLoading();

        void showMoreError();

        void showMoreNoNetwork();

        void showMoreNone();

        void showMoreSuccess();
    }
}
