package com.xmd.cashier.contract;

import com.xmd.cashier.BasePresenter;
import com.xmd.cashier.BaseView;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.MemberPlanInfo;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.TechInfo;

import java.util.List;

/**
 * Created by zr on 17-7-11.
 */

public interface MemberRechargeContract {
    interface Presenter extends BasePresenter {
        void loadPlanData();

        void onRechargePos();

        void onRechargeScan();

        void onRecharge(int type);

        void onTechClick();

        void onTechSelect(TechInfo info);

        void onTechDelete();

        void onAmountSet(String amount);

        void clearAmount();

        void onPackageSet(MemberPlanInfo.PackagePlanItem planItem);

        void clearPackage();

        void onReportResult(MemberRecordInfo info);
    }

    interface View extends BaseView<Presenter> {

        void showMemberInfo(MemberInfo info);

        void showPlanData(MemberPlanInfo info);

        void errorPlanData(String error);

        void loadingPlanData();

        void showTechInfo(TechInfo info);

        void deleteTechInfo();

        void clearAmount();

        void clearPackage();
    }
}
