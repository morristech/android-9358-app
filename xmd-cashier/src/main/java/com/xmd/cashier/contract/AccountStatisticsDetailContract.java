package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.OfflineAccountStatisticInfo;
import com.xmd.cashier.dal.bean.OnlineAccountStatisticInfo;

/**
 * Created by zr on 17-9-19.
 */

public interface AccountStatisticsDetailContract {
    interface Presenter extends BasePresenter {
        void initDate(int bizType);

        void pullData();

        void loadData();

        void loadCustomData();

        void onCustomStartPick(android.view.View view);

        void onCustomEndPick(android.view.View view);

        void plusDay();

        void minusDay();

        void plusWeek();

        void minusWeek();

        void plusMonth();

        void minusMonth();

        void onPrint(int bizType);

        void styleSettle();

        void styleMoney();

        void setStyle();

        String formatAmount(long amount);
    }

    interface View extends BaseView<Presenter> {
        void initDayDate();

        void setDayDate(String date);

        void setDayPlusEnable(boolean enable);

        void setDayMinusEnable(boolean enable);

        void initWeekDate();

        void setWeekDate(String date);

        void setWeekPlusEnable(boolean enable);

        void setWeekMinusEnable(boolean enable);

        void initMonthDate();

        void setMonthDate(String date);

        void setMonthPlusEnable(boolean enable);

        void setMonthMinusEnable(boolean enable);

        void initTotalDate();

        void setTotalDate(String date);

        void initCustomDate();

        void setCustomStart(String date);

        void setCustomEnd(String date);

        void initDataLayout();

        void initDataLoading();

        void setDataNormal(OnlineAccountStatisticInfo online, OfflineAccountStatisticInfo offline);

        void setDataError(String error);

        void showSettleStyle();

        void showMoneyStyle();
    }
}
