package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerSelectContract;
import com.xmd.cashier.dal.bean.EmployeeGroupInfo;
import com.xmd.cashier.dal.bean.ExInnerRoomInfo;
import com.xmd.cashier.dal.bean.ExInnerTechInfo;
import com.xmd.cashier.dal.bean.ExInnerTechStatusInfo;
import com.xmd.cashier.dal.bean.InnerHandInfo;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerRoomInfo;
import com.xmd.cashier.dal.bean.InnerTechInfo;
import com.xmd.cashier.dal.event.InnerFinishEvent;
import com.xmd.cashier.dal.event.InnerPushEvent;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.InnerHandListResult;
import com.xmd.cashier.dal.net.response.InnerOrderListResult;
import com.xmd.cashier.dal.net.response.InnerRoomListResult;
import com.xmd.cashier.dal.net.response.InnerTechListResult;
import com.xmd.cashier.dal.net.response.InnerUnpaidResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-11-8.
 */

public class InnerSelectPresenter implements InnerSelectContract.Presenter {
    private Context mContext;
    private InnerSelectContract.View mView;

    private Subscription mGetRoomInfoSubscription;
    private Subscription mGetOrderByRoomSubscription;
    private Subscription mGetHandInfoSubscription;
    private Subscription mGetOrderByHandSubscription;
    private Subscription mGetTechInfoSubscription;
    private Subscription mGetInnerUnpaidSubscription;

    private String mSelectType;

    public InnerSelectPresenter(Context context, InnerSelectContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        mSelectType = AppConstants.INNER_SEARCH_TYPE_ROOM;
        mView.showStepView();
        InnerManager.getInstance().clearInnerOrderInfos();
        initData();
        mView.hideBadgeView();
        getInnerUnpaidCount();
    }

    private void initData() {
        updateSum();
        switch (mSelectType) {
            case AppConstants.INNER_SEARCH_TYPE_ROOM:
                mView.initRoom();
                getRoomInfos(null);
                break;
            case AppConstants.INNER_SEARCH_TYPE_ORDER:
                mView.initHand();
                getHandInfos(null);
                break;
            case AppConstants.INNER_SEARCH_TYPE_TECH:
                mView.initTech();
                getTechInfos(null);
                break;
            default:
                break;
        }
    }

    private void getRoomInfos(String roomName) {
        mView.showLoading();
        if (mGetRoomInfoSubscription != null) {
            mGetRoomInfoSubscription.unsubscribe();
        }
        Observable<InnerRoomListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getRoomList(AccountManager.getInstance().getToken(), roomName);
        mGetRoomInfoSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerRoomListResult>() {
            @Override
            public void onCallbackSuccess(InnerRoomListResult result) {
                mView.hideLoading();
                mView.showExRoomData(formatRoomInfos(result.getRespData()));
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.showToast(e.getLocalizedMessage());
            }
        });
    }

    private List<ExInnerRoomInfo> formatRoomInfos(List<InnerRoomInfo> list) {
        Map<Long, List<InnerRoomInfo>> tempListMap = new HashMap<>();
        Map<Long, String> tempStrMap = new HashMap<>();
        for (InnerRoomInfo roomInfo : list) {
            if (tempListMap.containsKey(roomInfo.roomTypeId)) {
                tempListMap.get(roomInfo.roomTypeId).add(roomInfo);
            } else {
                List<InnerRoomInfo> tempList = new ArrayList<>();
                tempList.add(roomInfo);
                tempListMap.put(roomInfo.roomTypeId, tempList);
                tempStrMap.put(roomInfo.roomTypeId, roomInfo.roomTypeName);
            }
        }
        List<ExInnerRoomInfo> resultList = new ArrayList<>();
        Iterator<Map.Entry<Long, List<InnerRoomInfo>>> it = tempListMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<InnerRoomInfo>> entry = it.next();
            ExInnerRoomInfo exRoomInfo = new ExInnerRoomInfo();
            exRoomInfo.roomTypeId = entry.getKey();
            exRoomInfo.roomTypeName = tempStrMap.get(entry.getKey());
            exRoomInfo.rooms = entry.getValue();
            resultList.add(exRoomInfo);
        }
        return resultList;
    }

    private void getHandInfos(String userIdentify) {
        mView.showLoading();
        if (mGetHandInfoSubscription != null) {
            mGetHandInfoSubscription.unsubscribe();
        }
        Observable<InnerHandListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getHandList(AccountManager.getInstance().getToken(), userIdentify);
        mGetHandInfoSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerHandListResult>() {
            @Override
            public void onCallbackSuccess(InnerHandListResult result) {
                mView.hideLoading();
                mView.showHandData(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.showToast(e.getLocalizedMessage());
            }
        });
    }

    private void getTechInfos(String techNo) {
        mView.showLoading();
        if (mGetTechInfoSubscription != null) {
            mGetTechInfoSubscription.unsubscribe();
        }

        Observable<InnerTechListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getTechnicianList(AccountManager.getInstance().getToken(), techNo, AppConstants.INNER_SEARCH_TYPE_TECH);
        mGetTechInfoSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerTechListResult>() {
            @Override
            public void onCallbackSuccess(InnerTechListResult result) {
                mView.hideLoading();
                mView.showTechStatusData(formatTechInfos(result.getRespData()));
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.showToast(e.getLocalizedMessage());
            }
        });
    }

    private List<ExInnerTechStatusInfo> formatTechInfos(List<InnerTechInfo> innerTechInfos) {
        Map<String, List<InnerTechInfo>> tempMap = new HashMap<>();
        for (InnerTechInfo techInfo : innerTechInfos) {
            if (tempMap.containsKey(techInfo.status)) {
                tempMap.get(techInfo.status).add(techInfo);
            } else {
                List<InnerTechInfo> tempList = new ArrayList<>();
                tempList.add(techInfo);
                tempMap.put(techInfo.status, tempList);
            }
        }
        List<ExInnerTechStatusInfo> resultList = new ArrayList<>();
        Iterator<Map.Entry<String, List<InnerTechInfo>>> it = tempMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<InnerTechInfo>> entry = it.next();
            ExInnerTechStatusInfo techStatusInfo = new ExInnerTechStatusInfo();
            techStatusInfo.status = entry.getKey();
            techStatusInfo.exInfos = formatExTechInfos(entry.getValue());
            resultList.add(techStatusInfo);
        }
        Collections.sort(resultList, new Comparator<ExInnerTechStatusInfo>() {
            @Override
            public int compare(ExInnerTechStatusInfo o1, ExInnerTechStatusInfo o2) {
                return o1.status.compareTo(o2.status);
            }
        });
        return resultList;
    }

    private List<ExInnerTechInfo> formatExTechInfos(List<InnerTechInfo> infos) {
        Map<EmployeeGroupInfo, List<InnerTechInfo>> tempMap = new HashMap<>();
        for (InnerTechInfo techInfo : infos) {
            if (techInfo.employeeGroupInfo == null) {
                EmployeeGroupInfo tempGroup = new EmployeeGroupInfo();
                tempGroup.groupId = -1;
                tempGroup.groupName = "未分类";
                techInfo.employeeGroupInfo = tempGroup;
            }
            if (tempMap.containsKey(techInfo.employeeGroupInfo)) {
                tempMap.get(techInfo.employeeGroupInfo).add(techInfo);
            } else {
                List<InnerTechInfo> tempList = new ArrayList<>();
                tempList.add(techInfo);
                tempMap.put(techInfo.employeeGroupInfo, tempList);
            }
        }
        List<ExInnerTechInfo> resultList = new ArrayList<>();
        Iterator<Map.Entry<EmployeeGroupInfo, List<InnerTechInfo>>> it = tempMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<EmployeeGroupInfo, List<InnerTechInfo>> entry = it.next();
            ExInnerTechInfo exInnerTechInfo = new ExInnerTechInfo();
            exInnerTechInfo.groupInfo = entry.getKey();
            exInnerTechInfo.techInfos = entry.getValue();
            resultList.add(exInnerTechInfo);
        }
        Collections.sort(resultList, new Comparator<ExInnerTechInfo>() {
            @Override
            public int compare(ExInnerTechInfo o1, ExInnerTechInfo o2) {
                return Long.valueOf(o2.groupInfo.groupId).compareTo(o1.groupInfo.groupId);
            }
        });
        return resultList;
    }

    @Override
    public void onStart() {
        updateSum();
    }

    private void updateSum() {
        int count = InnerManager.getInstance().getInnerOrderInfos().size();
        if (count > 0) {
            mView.showSum("已选：" + count);
        } else {
            mView.hideSum();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mGetRoomInfoSubscription != null) {
            mGetRoomInfoSubscription.unsubscribe();
        }
        if (mGetHandInfoSubscription != null) {
            mGetHandInfoSubscription.unsubscribe();
        }
        if (mGetTechInfoSubscription != null) {
            mGetTechInfoSubscription.unsubscribe();
        }
        if (mGetOrderByRoomSubscription != null) {
            mGetOrderByRoomSubscription.unsubscribe();
        }
        if (mGetOrderByHandSubscription != null) {
            mGetOrderByHandSubscription.unsubscribe();
        }
        if (mGetInnerUnpaidSubscription != null) {
            mGetInnerUnpaidSubscription.unsubscribe();
        }
    }

    @Override
    public void onOrderConfirm() {
        if (InnerManager.getInstance().getInnerOrderInfos().isEmpty()) {
            mView.showToast("请选择结账订单...");
            return;
        }
        UiNavigation.gotoInnerMethodActivity(mContext, AppConstants.INNER_METHOD_SOURCE_NORMAL, null);
        mView.showEnterAnim();
    }

    @Override
    public void onOrderSearch() {
        String search = mView.returnSearchText();
        switch (mSelectType) {
            case AppConstants.INNER_SEARCH_TYPE_ROOM:
                getRoomInfos(search);
                break;
            case AppConstants.INNER_SEARCH_TYPE_ORDER:
                getHandInfos(search);
                break;
            case AppConstants.INNER_SEARCH_TYPE_TECH:
                getTechInfos(search);
                break;
            default:
                break;
        }
    }

    @Override
    public void onOrderSelect() {
        UiNavigation.gotoInnerOrderSelectActivity(mContext);
    }

    @Override
    public void onRoomSelect(InnerRoomInfo info, int position) {
        //room
        if (info.selected) {
            // select->unselect
            Iterator<InnerOrderInfo> it = InnerManager.getInstance().getInnerOrderInfos().iterator();
            while (it.hasNext()) {
                InnerOrderInfo orderInfo = it.next();
                if (orderInfo.roomId == info.id) {
                    orderInfo.selected = false;
                    it.remove();
                }
            }
            info.selected = !info.selected;
            mView.updateRoom(position);
            updateSum();
        } else {
            // unselect->select
            getOrderByRoom(info, position);
        }
    }

    private void getOrderByRoom(final InnerRoomInfo info, final int position) {
        mView.showLoading();
        if (mGetOrderByRoomSubscription != null) {
            mGetOrderByRoomSubscription.unsubscribe();
        }
        Observable<InnerOrderListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerOrderList(AccountManager.getInstance().getToken(), null, null, String.valueOf(info.id));
        mGetOrderByRoomSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerOrderListResult>() {
            @Override
            public void onCallbackSuccess(InnerOrderListResult result) {
                mView.hideLoading();
                for (InnerOrderInfo orderInfo : result.getRespData()) {
                    orderInfo.selected = true;
                    InnerManager.getInstance().addInnerOrderInfo(orderInfo);
                }
                info.selected = !info.selected;
                mView.updateRoom(position);
                updateSum();
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.showToast(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onHandSelect(InnerHandInfo info, int position) {
        //hand
        if (info.selected) {
            Iterator<InnerOrderInfo> it = InnerManager.getInstance().getInnerOrderInfos().iterator();
            while (it.hasNext()) {
                InnerOrderInfo orderInfo = it.next();
                if (orderInfo.userIdentify.equals(info.userIdentify)) {
                    orderInfo.selected = false;
                    it.remove();
                }
            }
            info.selected = !info.selected;
            mView.updateHand(position);
            updateSum();
        } else {
            getOrderByHand(info, position);
        }
    }

    private void getOrderByHand(final InnerHandInfo info, final int position) {
        mView.showLoading();
        if (mGetOrderByHandSubscription != null) {
            mGetOrderByHandSubscription.unsubscribe();
        }
        Observable<InnerOrderListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerOrderList(AccountManager.getInstance().getToken(), String.valueOf(info.id), null, null);
        mGetOrderByHandSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerOrderListResult>() {
            @Override
            public void onCallbackSuccess(InnerOrderListResult result) {
                mView.hideLoading();
                for (InnerOrderInfo orderInfo : result.getRespData()) {
                    orderInfo.selected = true;
                    InnerManager.getInstance().addInnerOrderInfo(orderInfo);
                }
                info.selected = !info.selected;
                mView.updateHand(position);
                updateSum();
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.showToast(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onTechSelect(InnerTechInfo info) {
        UiNavigation.gotoInnerOrderTechActivity(mContext, info.id);
    }

    @Override
    public void onNaviRoomClick() {
        mSelectType = AppConstants.INNER_SEARCH_TYPE_ROOM;
        initData();
    }

    @Override
    public void onNaviHandClick() {
        mSelectType = AppConstants.INNER_SEARCH_TYPE_ORDER;
        initData();
    }

    @Override
    public void onNaviTechClick() {
        mSelectType = AppConstants.INNER_SEARCH_TYPE_TECH;
        initData();
    }

    @Override
    public void onClickRecord() {
        UiNavigation.gotoInnerRecordActivity(mContext);
    }

    private void getInnerUnpaidCount() {
        Observable<InnerUnpaidResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerUnpaid(AccountManager.getInstance().getToken());
        mGetInnerUnpaidSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerUnpaidResult>() {
            @Override
            public void onCallbackSuccess(InnerUnpaidResult result) {
                int unpaid = result.getRespData().unpaidCount;
                if (unpaid > 0) {
                    mView.showBadgeView(unpaid);
                } else {
                    mView.hideBadgeView();
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.d(e.getLocalizedMessage());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InnerFinishEvent event) {
        switch (mSelectType) {
            case AppConstants.INNER_SEARCH_TYPE_ROOM:
                getRoomInfos(null);
                break;
            case AppConstants.INNER_SEARCH_TYPE_ORDER:
                getHandInfos(null);
                break;
            case AppConstants.INNER_SEARCH_TYPE_TECH:
                getTechInfos(null);
                break;
            default:
                break;
        }
        mView.initSearch();
        getInnerUnpaidCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InnerPushEvent event) {
        getInnerUnpaidCount();
    }
}
