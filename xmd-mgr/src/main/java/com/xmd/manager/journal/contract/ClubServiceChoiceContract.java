package com.xmd.manager.journal.contract;

import com.xmd.manager.beans.ServiceItem;
import com.xmd.manager.beans.ServiceItemInfo;
import com.xmd.manager.journal.BasePresenter;
import com.xmd.manager.journal.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */
public interface ClubServiceChoiceContract {
    interface Presenter extends BasePresenter {
        void onClickServiceItem(ServiceItem item);

        void onClickConfirmButton();

        void onClickCancelButton();

        boolean isServiceItemSelected(ServiceItem item);
    }

    interface View extends BaseView<Presenter> {
        void showServiceList(List<ServiceItemInfo> serviceList);

        void setConfirmButtonEnable(boolean enable);

        ArrayList<ServiceItem> getSelectedServiceItem();

        int getMaxSelectSize();
    }
}
