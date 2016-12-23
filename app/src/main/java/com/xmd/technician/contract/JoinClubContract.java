package com.xmd.technician.contract;

import android.text.Editable;

import com.xmd.technician.model.TechNo;

/**
 * Created by heyangya on 16-12-20.
 */

public interface JoinClubContract {
    interface View extends IBaseView {
    }

    interface Presenter extends IBasePresenter {

        void onClickSkip();

        void onClickJoin();

        void setInviteCode(Editable s);

        void onClickBack();

        void onClickShowTechNos();

        void onSelectTechNo(TechNo techNo);
    }
}
