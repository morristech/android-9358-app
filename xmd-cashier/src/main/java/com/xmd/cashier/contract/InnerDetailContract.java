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
    }

    interface View extends BaseView<Presenter> {
        InnerRecordInfo returnRecordInfo();

        void showRecordDetail(List<InnerOrderInfo> list);

        void showAmount(InnerRecordInfo recordInfo);
    }
}
