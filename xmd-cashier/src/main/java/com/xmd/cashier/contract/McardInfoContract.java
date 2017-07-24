package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.TechInfo;

/**
 * Created by zr on 17-7-11.
 */

public interface McardInfoContract {
    interface Presenter extends BasePresenter {
        void onConfirm();

        void onTechClick();

        void onTechSelect(TechInfo info);

        void onTechDelete();

        void onGenderSelect(String gender);

        void onBirthSelect(String birth);

        void onBirthDelete();

        void onNameChange(String name);
    }

    interface View extends BaseView<Presenter> {
        void showInfo(String phone, String name);

        void showTechInfo(TechInfo info);

        void deleteTechInfo();

        void showBirth(String birth);

        void showEnterAnim();

        void showExitAnim();
    }
}
