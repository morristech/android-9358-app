package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.MemberRecordInfo;

import java.util.List;

/**
 * Created by zr on 17-7-11.
 */

public interface MemberRecordContract {
    interface Presenter extends BasePresenter {
        void load(boolean init);

        void loadMore();

        void setSearch(String search);

        void setFilter(String filter);

        void print(MemberRecordInfo info, boolean retry, boolean keep);
    }

    interface View extends BaseView<Presenter> {
        void clearData();

        void showData(List<MemberRecordInfo> list);

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
