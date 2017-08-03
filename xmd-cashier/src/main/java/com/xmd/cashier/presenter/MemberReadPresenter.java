package com.xmd.cashier.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.zxing.client.android.Intents;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberReadContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.event.MagneticReaderEvent;
import com.xmd.cashier.dal.net.response.MemberCardResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.NFCManager;
import com.xmd.cashier.manager.TradeManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import rx.Subscription;

/**
 * Created by zr on 17-7-11.
 */

public class MemberReadPresenter implements MemberReadContract.Presenter {
    private Context mContext;
    private MemberReadContract.View mView;

    private MagneticReaderTask mReaderTask = null;
    private NFCManager mNFCManager;

    private Subscription mGetMemberInfoByCodeSubscription;
    private Subscription mGetMemberInfoByScanSubscription;
    private Subscription mCardMemberInfoSubscription;

    @Override
    public void onResume() {
        mNFCManager.setNFCListener(mNFCListener);
        mNFCManager.onResume((Activity) mContext);
    }

    @Override
    public void onPause() {
        mNFCManager.onPause((Activity) mContext);
    }

    @Override
    public void onNewIntent(Intent intent) {
        try {
            mNFCManager.procNFCIntent(intent);
        } catch (IOException e) {
            mView.showToast("读取失败:" + e.getLocalizedMessage());
        }
    }

    @Override
    public void onConfirm(final String readType) {
        String cardNo = mView.getInputContent();
        if (TextUtils.isEmpty(cardNo)) {
            mView.showError("请输入内容");
            return;
        }

        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }

        switch (readType) {
            case AppConstants.MEMBER_BUSINESS_TYPE_CARD:
                //开卡 : 调用开卡接口
                MemberManager.getInstance().setCardNo(cardNo);
                doCardMember();
                break;
            case AppConstants.MEMBER_BUSINESS_TYPE_PAYMENT:
            case AppConstants.MEMBER_BUSINESS_TYPE_RECHARGE:
                // 充值 消费
                getMemberByCode(readType, cardNo);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickScan() {
        UiNavigation.gotoScanCodeActivity(mContext);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UiNavigation.REQUEST_CODE_MEMBER_SCAN) {
            if (data != null && data.getAction().equals(Intents.Scan.ACTION)) {
                String result = data.getStringExtra(Intents.Scan.RESULT);
                XLogger.i(result);
                getMemberByScan(result);
            }
        }
    }

    private void getMemberByScan(String memberToken) {
        if (TextUtils.isEmpty(memberToken)) {
            mView.showError("无法获取会员信息!");
            return;
        }
        if (mGetMemberInfoByScanSubscription != null) {
            mGetMemberInfoByScanSubscription.unsubscribe();
        }
        mView.showLoading();
        mGetMemberInfoByScanSubscription = TradeManager.getInstance().fetchMemberInfo(memberToken, new Callback<MemberInfo>() {
            @Override
            public void onSuccess(MemberInfo o) {
                EventBus.getDefault().post(o);
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError("无法获取会员信息:" + error);
            }
        });
    }

    // 查询会员数据
    private void getMemberByCode(final String readType, String cardNo) {
        if (mGetMemberInfoByCodeSubscription != null) {
            mGetMemberInfoByCodeSubscription.unsubscribe();
        }
        mView.showLoading();
        mGetMemberInfoByCodeSubscription = MemberManager.getInstance().requestMemberInfo(cardNo, new Callback<MemberInfo>() {
            @Override
            public void onSuccess(MemberInfo o) {
                switch (readType) {
                    case AppConstants.MEMBER_BUSINESS_TYPE_PAYMENT:
                        // 消费
                        TradeManager.getInstance().getCurrentTrade().memberInfo = o;
                        TradeManager.getInstance().getCurrentTrade().memberPayMethod = AppConstants.MEMBER_PAY_METHOD_CODE;
                        EventBus.getDefault().post(o);
                        mView.finishSelf();
                        break;
                    case AppConstants.MEMBER_BUSINESS_TYPE_RECHARGE:
                        // 充值
                        MemberManager.getInstance().setRechargeMemberInfo(o);
                        MemberManager.getInstance().setMemberId(o.id);
                        UiNavigation.gotoMemberRechargeActivity(mContext);
                        mView.finishSelf();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("获取会员数据失败:" + error);
            }
        });
    }

    private void doCardMember() {
        if (mCardMemberInfoSubscription != null) {
            mCardMemberInfoSubscription.unsubscribe();
        }
        mView.showLoading();
        mCardMemberInfoSubscription = MemberManager.getInstance().requestCard(new Callback<MemberCardResult>() {
            @Override
            public void onSuccess(MemberCardResult o) {
                mView.hideLoading();
                UiNavigation.gotoMcardSuccessActivity(mContext);
                mView.showEnterAnim();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("会员开卡失败:" + error);
            }
        });
    }

    private class MagneticReaderTask extends Thread {
        private boolean isRun = false;

        @Override
        public void run() {
            isRun = true;
            try {
                while (isRun) {
                    String readerResult = MemberManager.getInstance().getMagneticReaderResult();
                    if (!TextUtils.isEmpty(readerResult)) {
                        PosFactory.getCurrentCashier().textToSound("读卡成功");
                        MagneticReaderEvent event = new MagneticReaderEvent();
                        event.setResult(readerResult);
                        EventBus.getDefault().post(event);
                    }
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                XLogger.e(e.getMessage());
                isRun = false;
            }
        }
    }

    private NFCManager.NFCListener mNFCListener = new NFCManager.NFCListener() {
        @Override
        public void onReceiveDataOffline(String id) {
            PosFactory.getCurrentCashier().textToSound("读卡成功");
            mView.setInputContent(id);
            mNFCManager.clearNFCParams();
        }

        @Override
        public void onError(String error) {
            mView.showToast("读取失败:" + error);
        }
    };

    public MemberReadPresenter(Context context, MemberReadContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        if (mReaderTask == null) {
            mReaderTask = new MagneticReaderTask();
            mReaderTask.start();
        }

        mNFCManager = NFCManager.getInstance();
        mNFCManager.init(mContext);
        PosFactory.getCurrentCashier().textToSound("请刷会员卡");
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mReaderTask != null) {
            mReaderTask.interrupt();
            mReaderTask = null;
        }
        if (mGetMemberInfoByCodeSubscription != null) {
            mGetMemberInfoByCodeSubscription.unsubscribe();
        }
        if (mCardMemberInfoSubscription != null) {
            mCardMemberInfoSubscription.unsubscribe();
        }
        if (mGetMemberInfoByScanSubscription != null) {
            mGetMemberInfoByScanSubscription.unsubscribe();
        }
    }
}
