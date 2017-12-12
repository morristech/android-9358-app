package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.ItemStatisticsInfo;

import java.util.List;

/**
 * Created by zr on 17-12-11.
 */

public interface ItemStatisticsDetailContract {
    interface Presenter extends BasePresenter {
        void initData(int bizType);

        void loadData();

        void onSelectMinus(int bizType);

        void onSelectPlus(int bizType);

        void onCustomStartPicker(android.view.View view);

        void onCustomEndPicker(android.view.View view);

        void onCustomConfirm();

        void onStyleChange();

        void onPrint();
    }

    interface View extends BaseView<Presenter> {
        void showDataError(String error);

        void showDataEmpty();

        void showData(List<ItemStatisticsInfo> list, int style);

        void showDataSuccess();

        void initDay(String showDay, String startTime, String endTime);

        void initMonth(String showMonth, String startTime, String endTime);

        void initCustom(String startTime, String endTime);

        void setCustomStart(String startTime);

        void setCustomEnd(String endTime);

        void showStyle(int style);

        void updateDataByStyle(int style);
    }
}
