package com.xmd.cashier.contract;

import android.graphics.Bitmap;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.MemberRecordInfo;

/**
 * Created by zr on 17-7-11.
 */

public interface MemberScanContract {
    interface Presenter extends BasePresenter {
        void printResult();
    }

    interface View extends BaseView<Presenter> {
        void showScanInfo(String description, String amount, String packageName);

        void showQrcode(Bitmap bitmap);

        void showScanSuccess();
    }
}
