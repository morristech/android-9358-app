package com.xmd.cashier.contract;

import android.graphics.Bitmap;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.MemberInfo;

/**
 * Created by zr on 17-11-4.
 */

public interface InnerPaymentContract {
    interface Presenter extends BasePresenter {
        void onMarkConfirm();

        void onMemberConfirm();

        void onPayActivityShow();

        void onEventBack();
    }

    interface View extends BaseView<Presenter> {
        void initMarkStub();

        void setMarkPaid(String markPaid);

        void setMarkName(String name);

        void setMarkDesc(String desc);

        void initScanStub();

        void setScanPaid(String scanPaid);

        void setQrcode(Bitmap bitmap);

        void setPayActivity(int show);

        void initMemberStub();

        void setMemberInfo(MemberInfo info);

        void setMemberOrigin(String memberOrigin);

        void setMemberDiscount(String memberDiscount);

        void setMemberPaid(String memberPaid);

        void setConfirmEnable(boolean enable);

        void setOrigin(String origin);

        void setDiscount(String discount);
    }
}
