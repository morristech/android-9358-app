package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.VerifyRecordInfo;

import java.util.List;

/**
 * Created by zr on 17-5-2.
 */

public interface VerifyRecordContract {
    interface Presenter extends BasePresenter {
        void load(boolean init, String startTime, String endTime);

        void loadMore();

        void loadTypeMap();

        void setTypeFilter(String filter);

        void setSearch(String search);

        void onRecordClick(String recordId);
    }

    interface View extends BaseView<Presenter> {
        void initFilter(List<String> list);

        void clearData();

        void showData(List<VerifyRecordInfo> list);

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
