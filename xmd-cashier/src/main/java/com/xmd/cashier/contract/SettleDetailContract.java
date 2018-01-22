package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.SettleRecordInfo;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;

/**
 * Created by zr on 17-4-7.
 */

public interface SettleDetailContract {
    interface Presenter extends BasePresenter {
        void getSettleDetail();

        void onPrint();
    }

    interface View extends BaseView<Presenter> {
        void initLayout();

        SettleRecordInfo returnRecordInfo();

        void onDetailSuccess(SettleSummaryResult.RespData respData);

        void onDetailFailed(String error);
    }
}
