package com.xmd.manager.journal.contract;

import android.widget.ImageView;

import com.xmd.manager.journal.BasePresenter;
import com.xmd.manager.journal.BaseView;
import com.xmd.manager.journal.model.Technician;

import java.util.List;

/**
 * Created by heyangya on 16-10-31.
 */

public interface TechnicianChoiceContract {
    interface Presenter extends BasePresenter {
        String getSelectedTechId();

        void onSelectTechnician(Technician technician, int viewPosition, ImageView selectedView);

        void onClickOk();
    }

    interface View extends BaseView<Presenter> {
        void showTechnicianList(List<Technician> technicianList, List<String> disableTechNoList);

        void showUnChecked(int viewPosition, ImageView selectedView);

        void showLoadTechnicianListFailed(String error);

        List<String> getForbiddenTechNoListFromIntent();

        void setOkButtonEnable(boolean enable);
    }
}
