package com.xmd.salary;

import com.xmd.salary.bean.BellListBean;
import com.xmd.salary.bean.CardItemBean;
import com.xmd.salary.bean.CommissionListBean;
import com.xmd.salary.bean.CommissionListBeanX;
import com.xmd.salary.bean.CommissionSettingBean;
import com.xmd.salary.bean.CommodityItemBean;
import com.xmd.salary.bean.MemberActivityDetailBean;
import com.xmd.salary.bean.OrderItemBean;
import com.xmd.salary.bean.OrderParameterBean;
import com.xmd.salary.bean.PackageItemsBean;
import com.xmd.salary.bean.PackageListBean;
import com.xmd.salary.bean.SalesCommissionListBean;
import com.xmd.salary.bean.ServiceCellBean;
import com.xmd.salary.bean.ServiceCommissionListBean;
import com.xmd.salary.bean.ServiceItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-11-24.
 */

public class SalaryIntroduceDataManager {

    private List<CommodityItemBean> mCommodityCommissionList;//推销提成-实物商品
    private List<CardItemBean> mCardItemBeanList;
    private List<OrderItemBean> mOrderItemBeanList;

    private List<BellListBean> mBellList; //横向列表
    private List<ServiceItemBean> mServiceItemList; //纵向列表
    private List<List<ServiceCellBean>> mServiceCellList; //
    private List<CommissionListBeanX> mCommissionListBeanX;

    private CommissionSettingBean mSettingBean;
    private static SalaryIntroduceDataManager mSalaryIntroduceInstance;

    public SalaryIntroduceDataManager() {
        mCommodityCommissionList = new ArrayList<>();
        mCardItemBeanList = new ArrayList<>();
        mOrderItemBeanList = new ArrayList<>();
        mServiceItemList = new ArrayList<>();
        mBellList = new ArrayList<>();
        mServiceCellList = new ArrayList<>();
        mCommissionListBeanX = new ArrayList<>();
    }

    public static SalaryIntroduceDataManager getSalaryIntroduceInstance() {
        if (mSalaryIntroduceInstance == null) {
            mSalaryIntroduceInstance = new SalaryIntroduceDataManager();
        }
        return mSalaryIntroduceInstance;
    }

    public void setCommissionListBeen(CommissionSettingBean settingBean) {
        this.mSettingBean = settingBean;
        if (mSettingBean == null) {
            return;
        }
        CommodityCommissionListDataChanged();
        OrderCommissionListDataChanged();
        CardCommissionListDataChanged();
        //   ServiceItemListDataChanged();
        BellListDataChanged();
        CommissionListBeanXDataChanged();
        ServiceCellListDataChanged();
    }

    private void CommissionListBeanXDataChanged() {
        mCommissionListBeanX.clear();
        for (ServiceCommissionListBean serviceBean : mSettingBean.serviceCommissionList) {
            for (CommissionListBeanX commissionBeanX : serviceBean.commissionList) {
                mCommissionListBeanX.add(commissionBeanX);
            }
        }
        setServiceItemList();
    }

    private void ServiceCellListDataChanged() {
        mServiceCellList.clear();
        for (int i = 0; i < mCommissionListBeanX.size(); i++) {
            List<ServiceCellBean> cellList = new ArrayList<>();
            for (int j = 0; j < mSettingBean.bellList.size(); j++) {
                Boolean hasItem = false;
                for (int k = 0; k < mCommissionListBeanX.get(i).bellCommissionList.size(); k++) {
                    if ((mSettingBean.bellList.get(j).id) == (mCommissionListBeanX.get(i).bellCommissionList.get(k).bellId)) {
                        hasItem = true;
                        cellList.add(new ServiceCellBean(i, String.valueOf(String.format("%1.1f", mCommissionListBeanX.get(i).bellCommissionList.get(k).commission / 100f))));
                        continue;
                    }
                }
                if (!hasItem) {
                    cellList.add(new ServiceCellBean(i, "无"));
                }
            }
            mServiceCellList.add(cellList);
        }

    }

    private void BellListDataChanged() {
        mBellList.clear();
        mBellList.addAll(mSettingBean.bellList);
    }

    public void setServiceItemList() {
        mServiceItemList.clear();
        for (CommissionListBeanX beanX : getCommissionBeanXList()) {
            mServiceItemList.add(new ServiceItemBean(beanX.businessName));
        }
    }

    private void CardCommissionListDataChanged() {
        mCardItemBeanList.clear();
        MemberActivityDetailBean memberActivityDetail = mSettingBean.memberActivityDetail;
        for (PackageListBean packageBean : memberActivityDetail.packageList) {
            CardItemBean cardItemBean = new CardItemBean(packageBean.name, packageBean.amount, getPackageItem(packageBean), packageBean.commissionAmount);
            mCardItemBeanList.add(cardItemBean);
        }
    }

    private String getPackageItem(PackageListBean packageBean) {
        StringBuilder strBuilder = new StringBuilder();
        for (PackageItemsBean packageItemBean : packageBean.packageItems) {
            strBuilder.append(packageItemBean.name).append("*").append(String.valueOf(packageItemBean.itemCount));
        }

        return strBuilder.toString();
    }

    private void CommodityCommissionListDataChanged() {
        mCommodityCommissionList.clear();
        for (SalesCommissionListBean commission : mSettingBean.salesCommissionList) {
            for (CommissionListBean bean : commission.commissionList) {
                CommodityItemBean itemBean = new CommodityItemBean(bean.businessName, bean.commission);
                mCommodityCommissionList.add(itemBean);
            }
        }

    }

    private void OrderCommissionListDataChanged() {
        mOrderItemBeanList.clear();
        OrderParameterBean orderParameter = mSettingBean.orderParameter;
        OrderItemBean orderBean = new OrderItemBean(getAppointTypeName(orderParameter.appointType), orderParameter.techCommission);
        mOrderItemBeanList.add(orderBean);
    }

    public List<CommodityItemBean> getCommissionList() {
        return mCommodityCommissionList;
    }

    public List<OrderItemBean> getOrderCommissionList() {
        return mOrderItemBeanList;
    }

    public List<CardItemBean> getCardCommissionList() {
        return mCardItemBeanList;
    }

    public List<BellListBean> getBellList() {
        return mBellList;
    }

    public List<ServiceItemBean> getServiceItemList() {
        return mServiceItemList;
    }

    public List<List<ServiceCellBean>> getServiceCellList() {
        return mServiceCellList;
    }

    public List<CommissionListBeanX> getCommissionBeanXList() {
        return mCommissionListBeanX;
    }


    private String getAppointTypeName(String appointType) {
        return appointType;
    }


}
