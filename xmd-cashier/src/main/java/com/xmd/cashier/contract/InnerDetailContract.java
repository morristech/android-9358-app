package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerRecordInfo;

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
        InnerRecordInfo returnRecordInfo();

        String returnSource();

        void showRecordDetail(List<InnerOrderInfo> list);

        void showAmount(InnerRecordInfo recordInfo);

        void showAmount(int amount);

        void showOperate();

        void showPositive();

        void setOperate(boolean flag);
    }
}
