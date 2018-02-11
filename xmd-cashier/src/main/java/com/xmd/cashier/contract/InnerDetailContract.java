package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.TradeRecordInfo;

import java.util.List;

/**
 * Created by zr on 17-11-7.
 */

public interface InnerDetailContract {
    interface Presenter extends BasePresenter {
        void onDetailNegative();

        void onDetailPositive();

        void onDetailPrint();

        void onDetailPay();
    }

    interface View extends BaseView<Presenter> {
        TradeRecordInfo returnRecordInfo();

        String returnSource();

        void showRecordDetail(List<InnerOrderInfo> list);

        void showAmount(TradeRecordInfo recordInfo);

        void showAmount(int amount);

        void showOperate();

        void showPositive();

        void setOperate(boolean flag);
    }
}
