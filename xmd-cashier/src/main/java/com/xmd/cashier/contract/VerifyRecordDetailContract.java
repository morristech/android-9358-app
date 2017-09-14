package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.net.response.VerifyRecordDetailResult;

/**
 * Created by zr on 17-5-2.
 */

public interface VerifyRecordDetailContract {
    interface Presenter extends BasePresenter {
        void getVerifyDetailById(String recordId);

        void printVerifyRecord(boolean keep);
    }

    interface View extends BaseView<Presenter> {
        void onDetailSuccess(VerifyRecordDetailResult.RespData data);
    }
}
