package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.dal.bean.ReportData;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.db.DataReportTable;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaOkHttp;
import com.xmd.cashier.dal.net.response.ReportTradeDataResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;


/**
 * Created by heyangya on 16-8-23.
 */

public class DataReportManager {
    private static DataReportManager mInstance = new DataReportManager();
    private AccountManager mAccountManager;
    private CashierManager mCashierManager;

    private DataReportManager() {
        mAccountManager = AccountManager.getInstance();
        mCashierManager = CashierManager.getInstance();
    }

    public static DataReportManager getInstance() {
        return mInstance;
    }

    public void reportData(Trade trade) {
        FormBody.Builder builder = new FormBody.Builder();
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_TOKEN, mAccountManager.getToken());
        params.put(RequestConstant.KEY_USER_ID, mAccountManager.getUserId());
        params.put(RequestConstant.KEY_CLUB_ID, mAccountManager.getClubId());
        params.put(RequestConstant.KEY_TRADE_NO, trade.tradeNo);
        params.put(RequestConstant.KEY_STATUS, String.valueOf(trade.tradeStatus));
        params.put(RequestConstant.KEY_ORIGIN_MONEY, String.valueOf(trade.getOriginMoney()));

        if (TradeManager.getInstance().haveSelected()) {
            params.put(RequestConstant.KEY_COUPON_LIST, TradeManager.formatCouponList(trade.getCouponList()));
            params.put(RequestConstant.KEY_COUPON_RESULT, TradeManager.formatCouponResult(trade.getCouponList()));
            params.put(RequestConstant.KEY_COUPON_MONEY, String.valueOf(trade.getVerificationMoney()));
        }

        params.put(RequestConstant.KEY_DISCOUNT_TYPE, String.valueOf(trade.getDiscountType()));
        params.put(RequestConstant.KEY_COUPON_DISCOUNT_MONEY, String.valueOf(trade.getCouponDiscountMoney()));
        params.put(RequestConstant.KEY_USER_DISCOUNT_MONEY, String.valueOf(trade.getUserDiscountMoney()));

        if (trade.getMemberPaidMoney() > 0) {
            params.put(RequestConstant.KEY_MEMBER_CARD_NO, trade.memberInfo.cardNo);
            params.put(RequestConstant.KEY_MEMBER_PAY_MONEY, String.valueOf(trade.getMemberPaidMoney()));
            params.put(RequestConstant.KEY_MEMBER_PAY_DISCOUNT_MONEY, String.valueOf(trade.getMemberPaidDiscountMoney()));
            params.put(RequestConstant.KEY_MEMBER_PAY_RESULT, String.valueOf(trade.memberPayResult));
            params.put(RequestConstant.KEY_MEMBER_PAY_CERTIFICATE, trade.memberPayCertificate);
        }

        if (trade.posPayReturn != null) {
            params.put(RequestConstant.KEY_POS_PAY_OUT_TRADE_NO, trade.getPosTradeNo());
            params.put(RequestConstant.KEY_POS_PAY_INNER_TRADE_NO, mCashierManager.getTradeNo(trade.posPayReturn));
            params.put(RequestConstant.KEY_POS_PAY_MONEY, String.valueOf(trade.posMoney));
            params.put(RequestConstant.KEY_POS_PAY_TYPE, String.valueOf(mCashierManager.getPayType(trade.posPayReturn)));
            params.put(RequestConstant.KEY_POS_PAY_RESULT, String.valueOf(trade.posPayResult));
            params.put(RequestConstant.KEY_POS_PAY_CERTIFICATE, mCashierManager.getCertificate(trade.posPayReturn));
            params.put(RequestConstant.KEY_POS_PAY_EXTRAINFO, mCashierManager.getExtraInfo(trade.posPayReturn));
        }

        params.put(RequestConstant.KEY_PAY_DATE, trade.tradeTime);
        params.put(RequestConstant.KEY_SIGN, RequestConstant.DEFAULT_SIGN_VALUE);
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = params.get(key);
            if (TextUtils.isEmpty(value)) {
                iterator.remove();
            } else {
                builder.add(key, value);
            }
        }
        SpaOkHttp.reportTradeDataSync(builder.build(), new ReportCallback(trade, params));
    }

    private class ReportCallback implements Callback<ReportTradeDataResult> {
        private String tradeNo;
        private Trade trade;
        private Map<String, String> params;

        public ReportCallback(String tradeNo, Map<String, String> params) {
            this.tradeNo = tradeNo;
            this.params = params;
        }

        public ReportCallback(Trade trade, Map<String, String> params) {
            this.trade = trade;
            this.tradeNo = trade.tradeNo;
            this.params = params;
        }

        @Override
        public void onSuccess(ReportTradeDataResult o) {
            XLogger.i("trade data report ok!");
            if (trade != null) {
                if (o.respData != null) {
                    trade.posPoints = o.respData.cashierPoints;
                }
            } else {
                DataReportTable.delete(tradeNo);
            }
        }

        @Override
        public void onError(String error) {
            //TODO 加入数据库，下次汇报
            XLogger.e("trade data report failed:" + error);
            if (!DataReportTable.isDataExist(tradeNo)) {
                params.remove(RequestConstant.KEY_TOKEN);
                params.remove(RequestConstant.KEY_SIGN);
                ReportData reportData = new ReportData();
                reportData.tradeNo = tradeNo;
                reportData.data = mapToBytes(params);
                DataReportTable.insert(reportData);
            }
        }
    }

    private byte[] mapToBytes(Map<String, String> map) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(map);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> bytesToMap(byte[] bytes) {
        Map<String, String> result = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            result = (Map<String, String>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void reportDataFromDB(ReportData data) {
        XLogger.i("report " + data.tradeNo);
        Map<String, String> params = bytesToMap(data.data);
        params.put(RequestConstant.KEY_SIGN, RequestConstant.DEFAULT_SIGN_VALUE);
        params.put(RequestConstant.KEY_TOKEN, AccountManager.getInstance().getToken());
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            String value = params.get(key);
            builder.add(key, value);
        }

        SpaOkHttp.reportTradeDataSync(builder.build(), new ReportCallback(data.tradeNo, params));
    }

    public void startMonitor() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                XLogger.i("data reporter scan ......");
                if (AccountManager.getInstance().isLogin()) {
                    List<ReportData> dataList = DataReportTable.query();
                    for (ReportData data : dataList) {
                        reportDataFromDB(data);
                    }
                }
            }
        }, 10000, 5 * 60 * 1000);
    }
}
