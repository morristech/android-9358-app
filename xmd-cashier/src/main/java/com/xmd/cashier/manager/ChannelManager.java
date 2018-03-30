package com.xmd.cashier.manager;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.TradeChannelInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.TradeChannelListResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 18-3-19.
 * 会所支付方式
 */

public class ChannelManager {
    private static final String TAG = "ChannelManager";
    private static ChannelManager mInstance = new ChannelManager();
    private Subscription mGetPayChannelSubscription;

    public static ChannelManager getInstance() {
        return mInstance;
    }


    // 获取会所设置的支付方式
    private List<TradeChannelInfo> tradeChannelInfos = new ArrayList<>();

    public List<TradeChannelInfo> getTradeChannelInfos() {
        return tradeChannelInfos;
    }

    public List<String> getCashierChannelTexts() {
        List<String> texts = new ArrayList<>();
        for (TradeChannelInfo tradeChannelInfo : tradeChannelInfos) {
            if (!AppConstants.PAY_CHANNEL_WX.equals(tradeChannelInfo.type) && !AppConstants.PAY_CHANNEL_ALI.equals(tradeChannelInfo.type)) {
                texts.add(tradeChannelInfo.name);
            }
        }
        return texts;
    }

    public List<String> getRechargeChannelTexts() {
        List<String> texts = new ArrayList<>();
        for (TradeChannelInfo tradeChannelInfo : tradeChannelInfos) {
            if (!AppConstants.PAY_CHANNEL_WX.equals(tradeChannelInfo.type) && !AppConstants.PAY_CHANNEL_ALI.equals(tradeChannelInfo.type) && !AppConstants.PAY_CHANNEL_ACCOUNT.equals(tradeChannelInfo.type)) {
                texts.add(tradeChannelInfo.name);
            }
        }
        return texts;
    }

    public TradeChannelInfo getCurrentChannelByText(String text) {
        TradeChannelInfo info = null;
        for (TradeChannelInfo tradeChannelInfo : tradeChannelInfos) {
            if (text.equals(tradeChannelInfo.name)) {
                info = tradeChannelInfo;
            }
        }
        return info;
    }

    public void getPayChannelList(final Callback<TradeChannelListResult> callback) {
        cancelPayChannelSubscription();
        tradeChannelInfos.clear();
        Observable<TradeChannelListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getPayChannelList(AccountManager.getInstance().getToken());
        mGetPayChannelSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<TradeChannelListResult>() {
            @Override
            public void onCallbackSuccess(TradeChannelListResult result) {
                if (result != null && result.getRespData() != null && !result.getRespData().isEmpty()) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取会所支付方式---成功：RIGHT");
                    tradeChannelInfos.addAll(result.getRespData());

                    TradeChannelInfo qrcodeChannel = new TradeChannelInfo();
                    qrcodeChannel.name = AppConstants.CASHIER_TYPE_QRCODE_TEXT;
                    qrcodeChannel.type = AppConstants.PAY_CHANNEL_QRCODE;
                    tradeChannelInfos.add(0, qrcodeChannel);

                    if (callback != null) {
                        callback.onSuccess(result);
                    }
                } else {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取会所支付方式---成功：EMPTY");
                    if (callback != null) {
                        callback.onError("会所未设置支付方式");
                    }
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取会所支付方式---失败：" + e.getLocalizedMessage());
                if (callback != null) {
                    callback.onError(e.getLocalizedMessage());
                }
            }
        });
    }

    public void cancelPayChannelSubscription() {
        if (mGetPayChannelSubscription != null) {
            mGetPayChannelSubscription.unsubscribe();
            mGetPayChannelSubscription = null;
        }
    }
}
