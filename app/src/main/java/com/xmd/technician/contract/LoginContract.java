package com.xmd.technician.contract;

/**
 * Created by heyangya on 16-12-19.
 */

public interface LoginContract {
    interface View extends IBaseView {
        void showTechNoLogin();

        void showPhoneLogin();

        void setPhoneNumber(String value);

        void enableLogin(boolean enable);
    }

    interface Presenter extends IBasePresenter {
        void onClickSwitchLoginMethod(); //点击切换登录方法

        void onClickLogin(); //点击登录

        void onClickRegister(); //点击注册

        void onClickFindPassword();//点击找回密码

        void setInviteCode(String value);

        void setTechNumber(String value);

        void setPassword(String value);

        void setPhoneNumber(String value);
    }
}
