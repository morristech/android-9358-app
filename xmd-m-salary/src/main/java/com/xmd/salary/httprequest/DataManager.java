package com.xmd.salary.httprequest;

import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.salary.httprequest.response.CommissionDetailResult;
import com.xmd.salary.httprequest.response.CommissionSettingResult;
import com.xmd.salary.httprequest.response.CommissionSumAmountResult;
import com.xmd.salary.httprequest.response.CommissionSumDataResult;
import com.xmd.salary.httprequest.response.SalarySettingResult;

import rx.Subscription;

/**
 * Created by Lhj on 17-11-22.
 */

public class DataManager {

    private static final DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {

    }
    public Subscription mTechSalarySetting; //工资构成
    public Subscription mTechCommissionDetail;//提成明细
    public Subscription mTechCommissionSumData;//汇总数据按天
    public Subscription mTechCommissionSumAmount;//汇总总数据

    public void getTechSalarySetting(NetworkSubscriber<SalarySettingResult> listener){
        mTechSalarySetting = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).commissionSalarySetting(),listener);
    }

    public void getTechCommissionDetail(String page, String pageSize, String st, String et, String type, NetworkSubscriber<CommissionDetailResult> listener){
        mTechCommissionDetail = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).commissionDetailRecords(page,pageSize,st,et,type),listener);
    }

    public void getTechCommissionSumData(String startDate, String endDate, String type, NetworkSubscriber<CommissionSumDataResult> listener){
        mTechCommissionSumData = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).commissionSumData(startDate,endDate,type),listener);
    }

    public void getTechCommissionSumAmount(String startDate, String endDate, NetworkSubscriber<CommissionSumAmountResult> listener){
        mTechCommissionSumAmount = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).commissionSumAmount(startDate,endDate),listener);
    }

    public void getCommissionSetting(NetworkSubscriber<CommissionSettingResult> listener){
        mTechSalarySetting = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).clubCommissionSetting(),listener);
    }
}
