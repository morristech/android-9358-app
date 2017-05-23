package com.xmd.technician.contract;

import android.text.Editable;

/**
 * Created by heyangya on 16-12-20.
 */

public interface RegisterContract {
    interface View extends IBaseView {

    }

    interface Presenter extends IBasePresenter {
        void onClickNextStep();

        void onClickGetVerificationCode();

        void setPhoneNumber(Editable s);

        void setVerificationCode(Editable s);

        void setNewPassword(Editable s);

        void onClickBack();
    }
}
