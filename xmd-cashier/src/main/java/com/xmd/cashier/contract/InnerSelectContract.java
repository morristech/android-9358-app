package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.InnerHandInfo;
import com.xmd.cashier.dal.bean.InnerRoomInfo;
import com.xmd.cashier.dal.bean.InnerTechInfo;

import java.util.List;

/**
 * Created by zr on 17-11-8.
 */

public interface InnerSelectContract {
    interface Presenter extends BasePresenter {
        void onOrderConfirm();

        void onOrderSearch();

        void onOrderSelect();

        void onRoomSelect(InnerRoomInfo info, int position);

        void onHandSelect(InnerHandInfo info, int position);

        void onTechSelect(InnerTechInfo info, int position);

        void onNaviRoomClick();

        void onNaviHandClick();

        void onNaviTechClick();

        void onClickRecord();
    }

    interface View extends BaseView<Presenter> {
        String returnSearchText();

        void showRoomData(List<InnerRoomInfo> list);

        void updateRoom(int position);

        void showHandData(List<InnerHandInfo> list);

        void updateHand(int position);

        void showTechData(List<InnerTechInfo> list);

        void updateTech(int position);

        void showSum(String text);

        void hideSum();

        void initRoom();

        void initHand();

        void initTech();

        void showEnterAnim();

        void showExitAnim();

        void showStepView();

        void initSearch();

        void showBadgeView(int count);

        void hideBadgeView();
    }
}
