package com.xmd.cashier.contract;

import android.content.Intent;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.CheckInfo;

import java.util.List;

/**
 * Created by zr on 17-5-19.
 */

public interface VerifyCheckInfoContract {
    interface Presenter extends BasePresenter {
        void setPhone(String phone);

        void onActivityResult(Intent intent, int requestCode, int resultCode);

        void onLoad();

        void onVerify();

        void onItemClick(CheckInfo info);

        void onItemSelect(CheckInfo info, boolean selected);

        void onItemSelectValid(CheckInfo info);
    }

    interface View extends BaseView<Presenter> {
        void showLoading();

        void hideLoading();

        void showError(String error);

        void showToast(String toast);

        void showCheckInfo(List<CheckInfo> list);

        void clearCheckInfo();

        void showBottomLayout();

        void hideBottomLayout();

        void updateBottomLayout(int selected);
    }
}
