package com.xmd.inner;

import android.text.TextUtils;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.inner.bean.NativeCategoryBean;
import com.xmd.inner.bean.NativeCreateBill;
import com.xmd.inner.bean.NativeEmployeeBean;
import com.xmd.inner.bean.NativeItemBean;
import com.xmd.inner.bean.NativeServiceItemBean;
import com.xmd.inner.bean.NativeTechnician;
import com.xmd.inner.bean.NativeUpdateBill;
import com.xmd.inner.bean.NativeUserIdentifyBean;
import com.xmd.inner.bean.OrderBillBean;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.inner.httprequest.response.HaveIdentifyResult;
import com.xmd.inner.httprequest.response.OrderTimeListResult;
import com.xmd.inner.httprequest.response.ProjectListAvailableResult;
import com.xmd.inner.httprequest.response.TechnicianListResult;
import com.xmd.m.network.NetworkSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lhj on 17-12-4.
 */

public class SeatBillDataManager {
    List<NativeCategoryBean> mCateGoryList; //服务分类列表
    List<List<NativeServiceItemBean>> mServiceItemListList; //服务子项列表
    List<NativeTechnician> mFreeTechList;   //会所状态闲技师列表
    List<NativeTechnician> mAllTechList;   //会所所有技师列表
    List<OrderBillBean> mOrderBellList; //轮钟类型列表
    List<NativeUserIdentifyBean> mUserIdentifyBeenList;//手牌列表

    private static SeatBillDataManager mManagerInstance;
    private boolean hasIdentify;

    public SeatBillDataManager() {
        mCateGoryList = new ArrayList<>();
        mServiceItemListList = new ArrayList<>();
        mFreeTechList = new ArrayList<>();
        mAllTechList = new ArrayList<>();
        mOrderBellList = new ArrayList<>();
        mUserIdentifyBeenList = new ArrayList<>();
        getProjectList();
        getFreeTechListData();
        getAllTechListData();
        getOrderItemBellList();
        getHaveIdentify();
    }


    public static SeatBillDataManager getManagerInstance() {
        if (mManagerInstance == null) {
            mManagerInstance = new SeatBillDataManager();
        }
        return mManagerInstance;
    }

    public void getProjectList() {
        DataManager.getInstance().getAvailableProjectList(new NetworkSubscriber<ProjectListAvailableResult>() {
            @Override
            public void onCallbackSuccess(ProjectListAvailableResult result) {
                mCateGoryList.clear();
                mServiceItemListList.clear();
                for (int i = 0; i < result.getRespData().size(); i++) {
                    mCateGoryList.add(result.getRespData().get(i).category);
                    mServiceItemListList.add(result.getRespData().get(i).serviceItem);
                }

            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    public void getFreeTechListData() {
        DataManager.getInstance().loadFreeTechnicianList(new NetworkSubscriber<TechnicianListResult>() {

            @Override
            public void onCallbackSuccess(TechnicianListResult result) {
                mFreeTechList.clear();
                for (NativeTechnician technician : result.getRespData()) {
                    if (!TextUtils.isEmpty(technician.name)) {
                        mFreeTechList.add(technician);
                    }
                }

            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }


    public void getAllTechListData() {
        DataManager.getInstance().loadAllTechnicianList(new NetworkSubscriber<TechnicianListResult>() {

            @Override
            public void onCallbackSuccess(TechnicianListResult result) {
                mAllTechList.clear();
                for (NativeTechnician technician : result.getRespData()) {
                    if (!TextUtils.isEmpty(technician.name)) {
                        mAllTechList.add(technician);
                    }
                }

            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    public void getOrderItemBellList() {
        DataManager.getInstance().loadOrderItemBellList(new NetworkSubscriber<OrderTimeListResult>() {
            @Override
            public void onCallbackSuccess(OrderTimeListResult result) {
                mOrderBellList.clear();
                mOrderBellList.addAll(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    public void getHaveIdentify() {
        DataManager.getInstance().userIdentifyHave(new NetworkSubscriber<HaveIdentifyResult>() {
            @Override
            public void onCallbackSuccess(HaveIdentifyResult result) {
                hasIdentify = result.getRespData().hasUserIdentify;
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }


    public void setUserIdentifyBeenList(List<NativeUserIdentifyBean> identifyList) {
        mUserIdentifyBeenList.clear();
        mUserIdentifyBeenList.addAll(identifyList);
    }

    public List<NativeCategoryBean> getCategoryList() {
        return mCateGoryList;
    }

    public List<List<NativeServiceItemBean>> getServiceItemListList() {
        return mServiceItemListList;
    }

    public List<NativeServiceItemBean> getServiceItemList(int position) {
        if (position > mServiceItemListList.size()) {
            return new ArrayList<>();
        }
        return mServiceItemListList.get(position);
    }

    public List<NativeTechnician> getFreeTechList() {
        for (NativeTechnician tech : mFreeTechList) {
            tech.isSelected = false;
        }
        return mFreeTechList;
    }

    public List<NativeTechnician> getAllTechList() {
        return mAllTechList;
    }

    public List<OrderBillBean> getOrderBellList() {
        return mOrderBellList;
    }

    public boolean haveIdentify() {
        return hasIdentify;
    }

    public List<NativeUserIdentifyBean> getUserIdentifyBeenList() {
        return mUserIdentifyBeenList;
    }

    public boolean canToCreateBill(NativeCreateBill bill) {
        if (hasIdentify && TextUtils.isEmpty(bill.getUserIdentify())) {
            XToast.show("请选择手牌");
            return false;
        }
        int i = 0;
        for (NativeItemBean itemBean : bill.getItemList()) {
            i++;
            if (itemBean.getItemType().equals(ConstantResource.BILL_GOODS_TYPE)) {
                if (itemBean.getItemCount() == 0 || TextUtils.isEmpty(itemBean.getItemId())) {
                    XToast.show("请输入完整订单信息");
                    return false;
                }

                for (NativeEmployeeBean employeeBean : itemBean.getEmployeeList()) {
                    if (TextUtils.isEmpty(employeeBean.getEmployeeId())) {
                        XToast.show("请输入完整订单信息");
                       return false;
                    }
                    employeeBean.setBellId(null);
                }

            } else {
                if (TextUtils.isEmpty(itemBean.getItemId())) {
                    XToast.show("请输入完整订单信息");
                    return false;
                }
                boolean hadAddBill = false;
                Map<String, String> employeeIdMap = new HashMap<>();
                for (NativeEmployeeBean employeeBean : itemBean.getEmployeeList()) {
                    if (!TextUtils.isEmpty(employeeBean.getEmployeeId()) && employeeBean.getBellId() == 4) {
                        hadAddBill = true;
                    }
                }
                for (NativeEmployeeBean employeeBean : itemBean.getEmployeeList()) {
                    if (TextUtils.isEmpty(employeeBean.getEmployeeId())) {
                        XToast.show("请输入完整订单信息");
                        return false;
                    }
                    if (!TextUtils.isEmpty(employeeBean.getEmployeeId()) && employeeBean.getBellId() == 0) {
                        XToast.show("请输入完整订单信息");
                        return false;
                    }
                    if (!employeeIdMap.containsKey(employeeBean.getEmployeeId())) {
                        employeeIdMap.put(employeeBean.getEmployeeId(), employeeBean.getEmployeeName());
                    } else {
                        XToast.show("消费" + i + "的服务技师重复");
                        return false;
                    }

                    if (hadAddBill) {
                        if (!TextUtils.isEmpty(employeeBean.getEmployeeId()) && employeeBean.getBellId() != 4) {
                            XToast.show("上钟类型不符合规定");
                            return false;
                        }
                    }
                }
            }
        }


        return true;
    }

    public boolean canToAddBill(NativeUpdateBill bill) {
        int i = 0;
        for (NativeItemBean itemBean : bill.getItemList()) {
            i++;
            if (itemBean == null || TextUtils.isEmpty(itemBean.getItemType())) {
                XToast.show("订单信息异常，请重新操作");
                return false;
            }
            if (itemBean.getItemType().equals(ConstantResource.BILL_GOODS_TYPE)) {
                if (itemBean.getItemCount() == 0 || TextUtils.isEmpty(itemBean.getItemId())) {
                    XToast.show("请输入完整订单信息");
                    return false;
                }
                for (NativeEmployeeBean employeeBean : itemBean.getEmployeeList()) {
                    employeeBean.setBellId(null);
                }

            } else {
                if (TextUtils.isEmpty(itemBean.getItemId())) {
                    XToast.show("请输入完整订单信息");
                    return false;
                }
                boolean hadAddBill = false;
                Map<String, String> employeeIdMap = new HashMap<>();
                for (NativeEmployeeBean employeeBean : itemBean.getEmployeeList()) {
                    if (!TextUtils.isEmpty(employeeBean.getEmployeeId()) && employeeBean.getBellId() == 4) {
                        hadAddBill = true;
                    }
                }
                for (NativeEmployeeBean employeeBean : itemBean.getEmployeeList()) {
                    if (TextUtils.isEmpty(employeeBean.getEmployeeId())) {
                        XToast.show("请输入完整订单信息");
                        return false;
                    }
                    if (!TextUtils.isEmpty(employeeBean.getEmployeeId()) && employeeBean.getBellId() == 0) {
                        XToast.show("请输入完整订单信息");
                        return false;
                    }
                    if (!employeeIdMap.containsKey(employeeBean.getEmployeeId())) {
                        employeeIdMap.put(employeeBean.getEmployeeId(), employeeBean.getEmployeeName());
                    } else {
                        XToast.show("消费" + i + "的服务技师重复");
                        return false;
                    }
                    if (hadAddBill) {
                        if (!TextUtils.isEmpty(employeeBean.getEmployeeId()) && employeeBean.getBellId() != 4) {
                            XToast.show("上钟类型不符合规定");
                            return false;
                        }
                    }


                }
            }
        }
        return true;
    }

    public void clearCateGorySelected() {
        for (NativeCategoryBean categoryBean : mCateGoryList) {
            categoryBean.isSelected = false;
        }
    }

    public void onDestroyData() {
        if (mManagerInstance != null) {
            mManagerInstance = null;
        }
    }


    public String getConsumeName(String itemId) {
        String categoryId = "";
        String itemName = "";
        for (List<NativeServiceItemBean> bean : mServiceItemListList) {
            if (bean == null) {
                continue;
            }
            for (NativeServiceItemBean itemBean : bean) {
                if (itemBean.id.equals(itemId)) {
                    categoryId = itemBean.categoryId;
                }
            }

        }
        for (NativeCategoryBean categoryBean : mCateGoryList) {
            if (categoryBean.id.equals(categoryId)) {
                itemName = categoryBean.name;
            }

        }
        return itemName;
    }


}
