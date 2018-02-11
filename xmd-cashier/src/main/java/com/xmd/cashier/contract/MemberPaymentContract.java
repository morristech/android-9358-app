package com.xmd.cashier.contract;

import android.graphics.Bitmap;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-7-11.
 */

public interface MemberPaymentContract {
    interface Presenter extends BasePresenter {
        void rechargeByCash();

        void printNormal();

        void onKeyEventBack();
    }

    interface View extends BaseView<Presenter> {
        void showScanInfo(String content, String subTitle, String subDetail, String amount);

        void showQrcode(Bitmap bitmap);

        void showScan();

        void enableCash();

        void disableCash();

        void showCash();

        void showSuccess();

        String getCashierMethod();
    }
}
