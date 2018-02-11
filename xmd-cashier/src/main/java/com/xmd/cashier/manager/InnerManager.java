package com.xmd.cashier.manager;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.SwitchInfo;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.InnerSwitchResult;
import com.xmd.cashier.dal.net.response.WorkTimeResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.ServerException;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import rx.Observable;

/**
 * Created by zr on 17-11-1.
 * 内网收银:处理房间,手牌,技师号;
 */

public class InnerManager {
    private static final String TAG = "InnerManager";
    private IPos mPos;

    private InnerManager() {
        mPos = PosFactory.getCurrentCashier();
    }

    private static InnerManager mInstance = new InnerManager();

    public static InnerManager getInstance() {
        return mInstance;
    }

    private List<InnerOrderInfo> innerOrderInfos = new ArrayList<>();

    public List<InnerOrderInfo> getInnerOrderInfos() {
        return innerOrderInfos;
    }

    public List<InnerOrderInfo> getSelectedInnerOrderInfos() {
        List<InnerOrderInfo> selected = new ArrayList<>();
        for (InnerOrderInfo innerOrderInfo : innerOrderInfos) {
            if (innerOrderInfo.selected) {
                selected.add(innerOrderInfo);
            }
        }
        return selected;
    }

    public void clearInnerOrderInfos() {
        innerOrderInfos.clear();
    }

    public boolean findOrderByRoom(long roomId) {
        for (InnerOrderInfo orderInfo : innerOrderInfos) {
            if (roomId == orderInfo.roomId) {
                return true;
            }
        }
        return false;
    }

    public boolean findOrderByHand(long handId) {
        for (InnerOrderInfo orderInfo : innerOrderInfos) {
            if (handId == orderInfo.id) {
                return true;
            }
        }
        return false;
    }

    public void addInnerOrderInfo(InnerOrderInfo info) {
        if (!innerOrderInfos.contains(info)) {
            innerOrderInfos.add(info);
        }
    }

    public void removeInnerOrderInfo(InnerOrderInfo info) {
        if (innerOrderInfos.contains(info)) {
            innerOrderInfos.remove(info);
        }
    }

    public void removeUnselectedInfos() {
        Iterator<InnerOrderInfo> it = innerOrderInfos.iterator();
        while (it.hasNext()) {
            InnerOrderInfo info = it.next();
            if (!info.selected) {
                it.remove();
            }
        }
    }

    public void selectedOrderInfos() {
        for (InnerOrderInfo orderInfo : innerOrderInfos) {
            orderInfo.selected = true;
        }
    }

    public void unselectedOrderInfos() {
        for (InnerOrderInfo orderInfo : innerOrderInfos) {
            orderInfo.selected = false;
        }
    }

    public String getOrderIds() {
        StringBuilder result = new StringBuilder();
        for (InnerOrderInfo info : innerOrderInfos) {
            if (info.selected) {
                result.append(info.id + ",");
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        } else {
            return null;
        }
    }

    public int getSelectCount() {
        int count = 0;
        for (InnerOrderInfo info : innerOrderInfos) {
            if (info.selected) {
                count += 1;
            }
        }
        return count;
    }

    public int getOrderAmount() {
        int amount = 0;
        for (InnerOrderInfo info : innerOrderInfos) {
            if (info.selected) {
                amount += info.amount;
            }
        }
        return amount;
    }

    //获取会所营业时间
    private String startTime;

    public String getStartTime() {
        return startTime;
    }

    public void getClubWorkTime() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "获取会所内网营业时间：" + RequestConstant.URL_GET_CLUB_WORK_TIME);
        Observable<WorkTimeResult> observable = XmdNetwork.getInstance().getService(SpaService.class).getWorkTime(AccountManager.getInstance().getToken());
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<WorkTimeResult>() {
            @Override
            public void onCallbackSuccess(WorkTimeResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "获取会所内网营业时间---成功：" + result.getRespData().startTimeStr);
                startTime = result.getRespData().startTimeStr;
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "获取会所内网营业时间---失败：" + e.getLocalizedMessage());
                startTime = AppConstants.STATISTICS_DEFAULT_TIME;
            }
        });
    }

    public void resetClubWorkTime() {
        startTime = AppConstants.STATISTICS_DEFAULT_TIME;
    }


    //获取内网开关
    private boolean mInnerSwitch = false;
    private Call<InnerSwitchResult> mCallInnerSwitch;
    private RetryPool.RetryRunnable mRetryGetInnerSwitch;
    private boolean resultInnerSwitch;

    public boolean getInnerSwitch() {
        return mInnerSwitch;
    }

    public void startGetInnerSwitch() {
        mRetryGetInnerSwitch = new RetryPool.RetryRunnable(AppConstants.TINNY_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getInnerSwitchConfig();
            }
        });
        RetryPool.getInstance().postWork(mRetryGetInnerSwitch);
    }

    public void stopGetInnerSwitch() {
        if (mCallInnerSwitch != null && !mCallInnerSwitch.isCanceled()) {
            mCallInnerSwitch.cancel();
        }
        if (mRetryGetInnerSwitch != null) {
            RetryPool.getInstance().removeWork(mRetryGetInnerSwitch);
            mRetryGetInnerSwitch = null;
        }
        mInnerSwitch = false;   //默认内网开关关闭
    }

    public boolean getInnerSwitchConfig() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "获取会所内网开关配置信息：" + RequestConstant.URL_GET_INNER_SWITCH);
        mCallInnerSwitch = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerSwitch(AccountManager.getInstance().getToken(), AppConstants.INNER_SWITCH_CODE);
        XmdNetwork.getInstance().requestSync(mCallInnerSwitch, new NetworkSubscriber<InnerSwitchResult>() {
            @Override
            public void onCallbackSuccess(InnerSwitchResult result) {
                SwitchInfo switchInfo = result.getRespData();
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "获取会所内网开关配置---成功：" + switchInfo.status);
                if (AppConstants.APP_REQUEST_YES.equals(switchInfo.status)) {
                    mInnerSwitch = true;
                } else {
                    mInnerSwitch = false;
                }
                resultInnerSwitch = true;
                EventBus.getDefault().post(switchInfo);
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "获取会所内网开关配置---失败：" + e.getLocalizedMessage());
                if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                    resultInnerSwitch = true;
                } else {
                    resultInnerSwitch = false;
                }
            }
        });
        return resultInnerSwitch;
    }
}
