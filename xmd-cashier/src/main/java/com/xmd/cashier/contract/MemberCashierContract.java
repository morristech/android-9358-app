package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.MemberInfo;

/**
 * Created by zr on 17-7-22.
 */

public interface MemberCashierContract {
    interface Presenter extends BasePresenter {
        void onMemberPay();

        void onNavigationBack();
    }

    interface View extends BaseView<Presenter> {
        void showInfo(MemberInfo info);

        void showButton(boolean enable);

        void showOriginAmount(String origin);

        void showDiscountAmount(String discount);

        void showNeedAmount(String need);
    }
}
