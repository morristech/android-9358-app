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
    }

    interface View extends BaseView<Presenter> {
        void showToast(String toast);

        void showError(String error);

        void showLoading();

        void hideLoading();

        void onDetailSuccess(VerifyRecordDetailResult.RespData data);
    }
}
