package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.InnerOrderInfo;

import java.util.List;

/**
 * Created by zr on 17-11-8.
 */

public interface InnerTechedOrderContract {
    interface Presenter extends BasePresenter {
        void onNegative();

        void onPositive();

        void onItemSelect(InnerOrderInfo info, int position);
    }

    interface View extends BaseView<Presenter> {
        void showOrderData(List<InnerOrderInfo> list);

        void updateItem(int position);

        String returnEmpId();

        void showDesc(String desc);

        void hideDesc();
    }
}
