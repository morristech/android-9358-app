package com.xmd.cashier.contract;

import android.graphics.Bitmap;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-5-12.
 */

public interface TradeQrcodePayContract {
    interface Presenter extends BasePresenter {
        void onGiftActivity();

        void onKeyEventBack();
    }

    interface View extends BaseView<Presenter> {
        void setAmount(String amount);

        void setQRCode(Bitmap bitmap);

        void updateScanStatus();

        void showQrError(String error);

        void showQrSuccess();

        void showGiftActivity();

        int getType();
    }
}
