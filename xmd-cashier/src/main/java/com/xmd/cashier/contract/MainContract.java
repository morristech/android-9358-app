package com.xmd.cashier.contract;

import android.content.Intent;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by heyangya on 16-8-24.
 */

public interface MainContract {
    interface Presenter extends BasePresenter {
        void onVerifyClick();

        void onScanClick();

        void onClickDrawer();

        void onClickLogout();

        void onClickSetting();

        void onClickVersion();

        void onCashierLayoutClick();

        void onMemberLayoutClick();

        void onOrderLayoutClick();

        void onOnlinePayLayoutClick();

        void onRecordLayoutClick();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        boolean onKeyEventBack();
    }

    interface View extends BaseView<MainContract.Presenter> {

        void showVersionName(String versionName);

        void showUserName(String userName);

        void showClubName(String clubName);

        void showClubIcon(String clubIcon);

        void onNavigationMenu();

        String getVerifyCode();
    }
}
