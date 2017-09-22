package com.xmd.manager.common;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.OrderProjectBean;
import com.xmd.manager.beans.OrderStatusBean;
import com.xmd.manager.beans.OrderTechNoBean;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.OrderProjectListResult;
import com.xmd.manager.service.response.OrderTechListResult;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by Lhj on 17-9-20.
 */

public class OrderFilterManager {

    private List<OrderStatusBean> mOrderStatusBeans;
    private List<OrderProjectBean> mOrderProjectBeans;
    private List<OrderTechNoBean> mOrderTechNoBeans;
    private Subscription mOrderProjectSubscription;
    private Subscription mOrderTechNoSubscription;
    private static final OrderFilterManager orderFilterManager = new OrderFilterManager();

    public static OrderFilterManager getInstance() {
        return orderFilterManager;
    }

    public void initData() {
        mOrderStatusBeans = new ArrayList<>();
        mOrderProjectBeans = new ArrayList<>();
        mOrderTechNoBeans = new ArrayList<>();
        mOrderTechNoSubscription = RxBus.getInstance().toObservable(OrderTechListResult.class).subscribe(
                result -> handlerOrderTechListResult(result));
        mOrderProjectSubscription = RxBus.getInstance().toObservable(OrderProjectListResult.class).subscribe(
                result -> handlerOrderProjectListResult(result));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_ORDER_TECHNICIAN_LIST);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_ORDER_PROJECT_LIST);
    }

    private void handlerOrderProjectListResult(OrderProjectListResult result) {
        if (result.statusCode == 200) {
            if (result.respData == null || result.respData.size() == 0) {
                return;
            }
            for (OrderProjectBean bean : result.respData) {
                bean.isSelect = 0;
                mOrderProjectBeans.add(bean);
            }

        }
    }

    private void handlerOrderTechListResult(OrderTechListResult result) {
        if (result.statusCode == 200) {
            if (result.respData == null || result.respData.size() == 0) {
                return;
            }
            for (OrderTechNoBean bean : result.respData) {
                bean.isSelect = 0;
                if (Utils.isNotEmpty(bean.techNo)) {
                    mOrderTechNoBeans.add(bean);
                }
            }
        }
    }

    public void setOrderStatusList(List<OrderStatusBean> data) {
        this.mOrderStatusBeans = data;
    }

    public void setTechNoList(List<OrderTechNoBean> data) {
        this.mOrderTechNoBeans = data;
    }

    public void setOrderProject(List<OrderProjectBean> data) {
        this.mOrderProjectBeans = data;
    }

    public List<OrderStatusBean> getOrderStatusList() {
        if (mOrderStatusBeans.size() == 0) {
            mOrderStatusBeans = new ArrayList<>();
            mOrderStatusBeans.add(new OrderStatusBean(ResourceUtils.getString(R.string.order_filter_untreated), Constant.ORDER_STATUS_SUBMIT, 0));
            mOrderStatusBeans.add(new OrderStatusBean(ResourceUtils.getString(R.string.order_filter_refused), Constant.ORDER_STATUS_REJECTED, 0));
            mOrderStatusBeans.add(new OrderStatusBean(ResourceUtils.getString(R.string.order_filter_overtime), Constant.ORDER_STATUS_OVERTIME, 0));
            mOrderStatusBeans.add(new OrderStatusBean(ResourceUtils.getString(R.string.order_filter_accept), Constant.ORDER_STATUS_ACCEPT, 0));
            mOrderStatusBeans.add(new OrderStatusBean(ResourceUtils.getString(R.string.order_filter_completed), Constant.ORDER_STATUS_COMPLETE, 0));
            mOrderStatusBeans.add(new OrderStatusBean(ResourceUtils.getString(R.string.order_filter_nullity), Constant.ORDER_STATUS_FAILURE, 0));
        }
        return mOrderStatusBeans;
    }

    public List<OrderTechNoBean> getTechNoList() {
        return mOrderTechNoBeans;
    }

    public List<OrderProjectBean> getOrderProjectList() {
        return mOrderProjectBeans;
    }

    public void managerDestroy() {
        if (mOrderProjectSubscription != null) {
            RxBus.getInstance().unsubscribe(mOrderProjectSubscription);
        }
        if (mOrderTechNoSubscription != null) {
            RxBus.getInstance().unsubscribe(mOrderTechNoSubscription);
        }
    }

    public String getFilterOrderStatus() {
        List<String> selectedOrderStatus = new ArrayList<>();
        for (OrderStatusBean bean : mOrderStatusBeans) {
            if (bean.isSelected == 1) {
                selectedOrderStatus.add(bean.orderStatus);
            }
        }
        return Utils.listToString(selectedOrderStatus);
    }

    public String getFilterOrderProject() {
        List<String> selectedOrderProject = new ArrayList<>();
        for (OrderProjectBean bean : mOrderProjectBeans) {
            if (bean.isSelect == 1) {
                selectedOrderProject.add(bean.id);
            }
        }
        return Utils.listToString(selectedOrderProject);
    }

    public String getFilterOrderTechNo() {
        List<String> selectedOrderTech = new ArrayList<>();
        for (OrderTechNoBean bean : mOrderTechNoBeans) {
            if (bean.isSelect == 1) {
                selectedOrderTech.add(bean.id);
            }
        }
        return Utils.listToString(selectedOrderTech);
    }
}
