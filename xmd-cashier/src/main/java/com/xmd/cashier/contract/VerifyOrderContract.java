package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.OrderInfo;

/**
 * Created by zr on 2017/4/16 0016.
 */

public interface VerifyOrderContract {
    interface Presenter extends BasePresenter {
        void onVerify(OrderInfo info);
    }

    interface View extends BaseView<Presenter> {
    }
}
