package com.xmd.cashier.contract;

import android.content.Intent;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.VerificationItem;

import java.util.List;

/**
 * Created by heyangya on 16-8-24.
 */

public interface VerificationContract {
    interface Presenter extends BasePresenter {
        void onClickSearch();

        void onClickScan();

        void onScanResult(Intent intent);

        void onVerificationItemChecked(VerificationItem item, boolean isChecked);

        void onVerificationItemClicked(VerificationItem item);

        void onNavigationBack();

        void onClickOk();

        void onClickCleanAll();
    }

    interface View extends BaseView<VerificationContract.Presenter> {
        String getNumber();

        void showLoadingView();

        void hideLoadingView();

        void showVerificationData(List<VerificationItem> verificationItems);

        void showError(String error);

        void showToast(String toast);

        void hideKeyboard();
    }
}
