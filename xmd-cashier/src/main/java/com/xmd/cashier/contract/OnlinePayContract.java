package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.OnlinePayInfo;

import java.util.List;

/**
 * Created by zr on 17-4-11.
 */

public interface OnlinePayContract {
    interface Presenter extends BasePresenter {
        void load(boolean init);

        void loadMore();

        void print(OnlinePayInfo info, boolean retry);

        void pass(OnlinePayInfo info, int position);

        void unpass(OnlinePayInfo info, int position);

        void setFilter(String filter);

        void setSearch(String search);
    }

    interface View extends BaseView<Presenter> {
        void updateDataStatus(String status, int position);

        void clearData();

        void showData(List<OnlinePayInfo> list);

        void showLoading();

        void hideLoading();

        void showError(String error);

        void showToast(String toast);

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
