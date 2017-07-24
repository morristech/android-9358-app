package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.BillInfo;

/**
 * Created by zr on 16-11-23.
 * 交易详情
 */

public interface BillDetailContract {
    interface Presenter extends BasePresenter {
        void onClickMore();

        void print(BillInfo info);

        void refund(BillInfo info);
    }

    interface View extends BaseView<BillDetailContract.Presenter> {
        void showMorePop();
    }
}
