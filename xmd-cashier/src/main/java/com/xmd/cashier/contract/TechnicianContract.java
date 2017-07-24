package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.TechInfo;

import java.util.List;

/**
 * Created by zr on 17-7-11.
 */

public interface TechnicianContract {
    interface Presenter extends BasePresenter {
        void load(boolean init);

        void onTechSelect(TechInfo info);
    }

    interface View extends BaseView<Presenter> {
        void clearData();

        void showData(List<TechInfo> list);

        void showRefreshIng();

        void showRefreshError();

        void showRefreshEmpty();

        void showRefreshNoNetwork();

        void showRefreshSuccess();
    }
}
