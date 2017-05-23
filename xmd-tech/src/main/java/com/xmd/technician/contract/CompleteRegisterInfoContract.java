package com.xmd.technician.contract;

import android.content.Intent;
import android.text.Editable;

/**
 * Created by heyangya on 16-12-20.
 */

public interface CompleteRegisterInfoContract {
    interface View extends IBaseView {
    }

    interface Presenter extends IBasePresenter {

        void setNickName(Editable s);

        void setGender(boolean female);

        void onClickAvatar();

        void onClickFinish();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onBack();
    }
}
