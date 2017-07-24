package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.MemberInfo;

/**
 * Created by zr on 17-7-11.
 */

public interface McardSuccessContract {
    interface Presenter extends BasePresenter {
        void onConfirm();

        void onFinish();
    }

    interface View extends BaseView<Presenter> {
        void showInfo(MemberInfo info);

        void showEnterAnim();

        void showExitAnim();
    }
}
