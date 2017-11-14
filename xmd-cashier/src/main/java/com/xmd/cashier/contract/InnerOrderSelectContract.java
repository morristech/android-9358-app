package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.InnerOrderInfo;

import java.util.List;

/**
 * Created by zr on 17-11-8.
 */

public interface InnerOrderSelectContract {
    interface Presenter extends BasePresenter {
        void onNegative();

        void onPositive();

        void onItemSelect(InnerOrderInfo info, int position);
    }

    interface View extends BaseView<Presenter> {
        void showCountText(String text);

        void hideCountText();

        void updateItem(int position);

        void showOrderData(List<InnerOrderInfo> list);
    }

}
