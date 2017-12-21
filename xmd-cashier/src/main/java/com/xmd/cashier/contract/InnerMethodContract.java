package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerRecordInfo;

import java.util.List;

/**
 * Created by zr on 17-11-2.
 */

public interface InnerMethodContract {
    interface Presenter extends BasePresenter {
        void onVerifySelect();

        void onPayClick();

        void onSelectChange();

        void onOrderClick(InnerOrderInfo info, int position);

        void processData();
    }

    interface View extends BaseView<Presenter> {
        void showNeedPayAmount(int amount);

        void showDiscountAmount(int amount);

        void showOrderList(List<InnerOrderInfo> list);

        String returnSource();

        InnerRecordInfo returnRecordInfo();

        void showEnterAnim();

        void showExitAnim();

        void showStepView();

        void setStatusLayout(boolean show);

        void updateStatus(boolean selected);

        void showSelectCount(int count);

        void updateItem(int position);

        void updateAll();

        void showDiscountEnter(boolean tag);
    }
}
