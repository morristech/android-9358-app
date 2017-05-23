package com.xmd.technician.contract;

import android.text.Editable;

import com.xmd.technician.http.gson.RoleListResult;
import com.xmd.technician.model.TechNo;

import java.util.List;

/**
 * Created by heyangya on 16-12-20.
 */

public interface JoinClubContract {
    interface View extends IBaseView {
        void showRoleList(List<RoleListResult.Item> roles, int selectPosition);
    }

    interface Presenter extends IBasePresenter {

        void onClickSkip();

        void onClickJoin();

        void setInviteCode(Editable s);

        void onClickBack();

        void onClickShowTechNos();

        void onSelectTechNo(TechNo techNo);

        void onSelectRole(int selectPosition);
    }
}
