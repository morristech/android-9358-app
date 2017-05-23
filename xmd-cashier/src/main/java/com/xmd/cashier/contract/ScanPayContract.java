package com.xmd.cashier.contract;

import android.graphics.Bitmap;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-5-12.
 */

public interface ScanPayContract {
    interface Presenter extends BasePresenter {
        void onCancel();
    }

    interface View extends BaseView<Presenter> {
        void showLoading();

        void hideLoading();

        void showError(String error);

        void showToast(String toast);

        void setOrigin(String origin);

        void setDiscount(String discount);

        void setPaid(String paid);

        void setQRCode(Bitmap bitmap);

        void updateScanStatus();
    }
}
