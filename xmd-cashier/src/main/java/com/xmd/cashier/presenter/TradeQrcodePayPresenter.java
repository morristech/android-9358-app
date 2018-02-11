package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.TradeQrcodePayContract;
import com.xmd.cashier.contract.TradeQrcodePayContract.Presenter;
import com.xmd.cashier.dal.bean.GiftActivityInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.event.QRScanStatusEvent;
import com.xmd.cashier.dal.event.TradeDoneEvent;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GiftActivityResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-5-12.
 */

public class TradeQrcodePayPresenter implements Presenter {
    private static final String TAG = "TradeQrcodePayPresenter";
    private Context mContext;
    private TradeQrcodePayContract.View mView;

    private GiftActivityInfo mGiftActivityInfo;
    private Subscription mGetGiftActivitySubscription;

    private TradeManager mTradeManager;

    public TradeQrcodePayPresenter(Context context, TradeQrcodePayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        PosFactory.getCurrentCashier().speech("请扫描屏幕中二维码");
        Trade trade = mTradeManager.getCurrentTrade();
        mView.setAmount(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(trade.getWillPayMoney())));
        getGiftActivity();  //买单活动

        if (TextUtils.isEmpty(trade.payUrl)) {
            mView.showQrError("获取二维码失败");
        } else {
            Bitmap bitmap;
            try {
                bitmap = Utils.getQRBitmap(trade.payUrl);
            } catch (Exception e) {
                bitmap = null;
            }
            if (bitmap == null) {
                mView.showQrError("二维码解析失败");
            } else {
                mView.showQrSuccess();
                mView.setQRCode(bitmap);
                // 轮询扫码状态
                startGetScanStatus();
                // 轮询支付状态
                startGetPayStatus();
            }
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        stopGetScanStatus();
        stopGetPayStatus();
        if (mGetGiftActivitySubscription != null) {
            mGetGiftActivitySubscription.unsubscribe();
        }
    }

    // ****************************************轮询扫码状态******************************************
    private Call<StringResult> getScanStatusCall;
    private RetryPool.RetryRunnable mRetryScanStatus;
    private boolean resultScanStatus = false;

    private void startGetScanStatus() {
        mRetryScanStatus = new RetryPool.RetryRunnable(AppConstants.DEFAULT_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getScanStatus();
            }
        });
        RetryPool.getInstance().postWork(mRetryScanStatus);
    }

    private void stopGetScanStatus() {
        if (getScanStatusCall != null && !getScanStatusCall.isCanceled()) {
            getScanStatusCall.cancel();
        }
        if (mRetryScanStatus != null) {
            RetryPool.getInstance().removeWork(mRetryScanStatus);
            mRetryScanStatus = null;
        }
    }

    private boolean getScanStatus() {
        getScanStatusCall = XmdNetwork.getInstance().getService(SpaService.class)
                .checkScanStatus(AccountManager.getInstance().getToken(), mTradeManager.getCurrentTrade().payOrderId);
        XmdNetwork.getInstance().requestSync(getScanStatusCall, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                if (AppConstants.APP_REQUEST_YES.equals(result.getRespData())) {
                    resultScanStatus = true;
                    EventBus.getDefault().post(new QRScanStatusEvent());
                } else {
                    resultScanStatus = false;
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                resultScanStatus = false;
            }
        });
        return resultScanStatus;
    }

    // ****************************************轮询买单详情******************************************
    private Call<StringResult> getPayStatusCall;
    private RetryPool.RetryRunnable mRetryPayStatus;
    private boolean resultPayStatus = false;

    private void startGetPayStatus() {
        mRetryPayStatus = new RetryPool.RetryRunnable(AppConstants.TINNY_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getPayStatus();
            }
        });
        RetryPool.getInstance().postWork(mRetryPayStatus);
    }

    private void stopGetPayStatus() {
        if (getPayStatusCall != null && !getPayStatusCall.isCanceled()) {
            getPayStatusCall.cancel();
        }
        if (mRetryPayStatus != null) {
            RetryPool.getInstance().removeWork(mRetryPayStatus);
            mRetryPayStatus = null;
        }
    }

    private boolean getPayStatus() {
        getPayStatusCall = XmdNetwork.getInstance().getService(SpaService.class)
                .checkPayStatus(AccountManager.getInstance().getToken(), mTradeManager.getCurrentTrade().payOrderId, mTradeManager.getCurrentTrade().payNo);
        XmdNetwork.getInstance().requestSync(getPayStatusCall, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                if (AppConstants.APP_REQUEST_YES.equals(result.getRespData())) {
                    resultPayStatus = true;
                    mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                    PosFactory.getCurrentCashier().speech("支付成功");
                    EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
                    mView.finishSelf();
                } else {
                    resultPayStatus = false;
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                resultPayStatus = false;
            }
        });
        return resultPayStatus;
    }

    // 查看买单活动
    private void getGiftActivity() {
        if (mGetGiftActivitySubscription != null) {
            mGetGiftActivitySubscription.unsubscribe();
        }
        Observable<GiftActivityResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getGiftActivity(AccountManager.getInstance().getToken(), AccountManager.getInstance().getClubId());
        mGetGiftActivitySubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<GiftActivityResult>() {
            @Override
            public void onCallbackSuccess(GiftActivityResult result) {
                if (result != null && result.getRespData() != null) {
                    mGiftActivityInfo = result.getRespData();
                    mView.showGiftActivity();
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e("获取买单有礼活动失败:" + e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onGiftActivity() {
        UiNavigation.gotoGiftActActivity(mContext, mGiftActivityInfo);
    }

    @Override
    public void onKeyEventBack() {
        new CustomAlertDialogBuilder(mContext)
                .setMessage("请确认是否退出此次交易？")
                .setPositiveButton("继续交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("退出交易", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stopGetPayStatus();
                        stopGetScanStatus();
                        mTradeManager.getCurrentTrade().tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                        mTradeManager.getCurrentTrade().tradeStatusError = "已取消交易";
                        EventBus.getDefault().post(new TradeDoneEvent(mView.getType()));
                        mView.finishSelf();
                    }
                })
                .create()
                .show();
    }
}
