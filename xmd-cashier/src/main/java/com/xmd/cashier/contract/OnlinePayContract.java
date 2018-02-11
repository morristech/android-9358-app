package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.PayRecordInfo;
import com.xmd.cashier.dal.bean.TradeRecordInfo;

import java.util.List;

/**
 * Created by zr on 17-4-11.
 */

public interface OnlinePayContract {
    interface Presenter extends BasePresenter {
        void load(boolean init);

        void loadMore();

        void print(TradeRecordInfo info, boolean retry, boolean keep);

        void pass(TradeRecordInfo info, int position);

        void unpass(TradeRecordInfo info, int position);

        void setFilter(String filter);

        void setSearch(String search);

        void detail(String code);

        void onPayDetail(List<PayRecordInfo> payRecordInfos);
    }

    interface View extends BaseView<Presenter> {
        void updateDataStatus(String status, int position);

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
