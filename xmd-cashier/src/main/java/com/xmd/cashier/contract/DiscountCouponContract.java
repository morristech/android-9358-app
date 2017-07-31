package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.PayCouponInfo;

/**
 * Created by zr on 17-7-27.
 */

public interface DiscountCouponContract {
    interface Presenter extends BasePresenter {
    }

    interface View extends BaseView<Presenter> {
        String getCode();

        void showCouponInfo(PayCouponInfo info);
    }
}
