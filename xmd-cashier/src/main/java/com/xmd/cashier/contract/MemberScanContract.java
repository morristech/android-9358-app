package com.xmd.cashier.contract;

import android.graphics.Bitmap;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by zr on 17-7-11.
 */

public interface MemberScanContract {
    interface Presenter extends BasePresenter {
        void printResult();
    }

    interface View extends BaseView<Presenter> {
        void showScanInfo(String content, String subTitle, String subDetail, String amount);

        void showQrcode(Bitmap bitmap);

        void showScanSuccess();
    }
}
