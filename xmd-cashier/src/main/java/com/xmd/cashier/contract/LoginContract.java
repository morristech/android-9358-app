package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;

/**
 * Created by heyangya on 16-8-23.
 */

public interface LoginContract {
    interface Presenter extends BasePresenter {
        void onClickLoginButton();

        void onClickVersionView();
    }

    interface View extends BaseView<Presenter> {
        String getUserName();

        String getPassword();

        void setLoginName(String userName);

        void cleanPassword();

        void onLoginStart();

        void onLoginEnd(String error); //error==null 表示登录成功,其他表示错误

        void showVersionName(String versionName);
    }
}
