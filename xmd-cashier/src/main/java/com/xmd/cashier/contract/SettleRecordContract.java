package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.SettleRecordInfo;

import java.util.List;

/**
 * Created by zr on 17-3-29.
 */

public interface SettleRecordContract {
    interface Presenter extends BasePresenter {
        void loadInit();

        void loadMore();

        void onRecordClick(SettleRecordInfo recordInfo);

        void onPickView();
    }

    interface View extends BaseView<Presenter> {
        void showLoadIng();

        void showLoadError();

        void showLoadEmpty();

        void showLoadNoNetwork();

        void showLoadSuccess();

        void showMoreIng();

        void showMoreError();

        void showMoreNoNetwork();

        void showMoreNone();

        void showMoreSuccess();

        void showData(List<SettleRecordInfo> list);

        void clearData();
    }
}
