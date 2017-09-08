package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.GiftActivityInfo;

import java.util.List;

/**
 * Created by zr on 17-9-8.
 */

public interface GiftActContract {
    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<Presenter> {
        void setActivityData(List<GiftActivityInfo.GiftActivityPackage> data);

        void setActivityTime(String time);

        void setActivityCopyRight(String copyright);

        GiftActivityInfo getActivityInfo();
    }
}
