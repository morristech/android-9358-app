package com.xmd.cashier.pos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteException;

import com.iboxpay.cashbox.minisdk.CashboxProxy;
import com.iboxpay.cashbox.minisdk.PayType;
import com.iboxpay.cashbox.minisdk.SignType;
import com.iboxpay.cashbox.minisdk.callback.IAuthCallback;
import com.iboxpay.cashbox.minisdk.callback.ITradeCallback;
import com.iboxpay.cashbox.minisdk.exception.ConfigErrorException;
import com.iboxpay.cashbox.minisdk.model.Config;
import com.iboxpay.cashbox.minisdk.model.ErrorMsg;
import com.iboxpay.cashbox.minisdk.model.ParcelableBitmap;
import com.iboxpay.cashbox.minisdk.model.ParcelableMap;
import com.iboxpay.cashbox.minisdk.model.PrintPreference;
import com.iboxpay.cashbox.minisdk.model.TradingNo;
import com.iboxpay.print.IPrintJobStatusCallback;
import com.iboxpay.print.PrintManager;
import com.iboxpay.print.model.CharacterParams;
import com.iboxpay.print.model.GraphParams;
import com.iboxpay.print.model.PrintItemJobInfo;
import com.iboxpay.print.model.PrintJobInfo;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.ThreadManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.PayCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by heyangya on 16-10-10.
 */

public class PosImpl implements IPos {
    private static PosImpl mInstance = new PosImpl();
    private boolean mIsInited;

    private String mAppCode = "3000210";//appCode,行业合作商唯一标志,格式:3001010
    private String mMerchantNo = "012441956355123";//盒子商户唯一标识
    private PrintPreference printPreference;
    private PrintManager mPrintManager;
    private List<PrintItemJobInfo> mPrintItemJobInfoList = new ArrayList<>();

    private PosImpl() {
    }

    @Override
    public String getAppCode() {
        return AppConstants.APP_CODE_HUI_POS;
    }

    @Override
    public boolean needCheckUpdate() {
        return true;
    }

    @Override
    public void init(final Context context, final Callback<?> callback) {
        if (mIsInited) {
            XLogger.i("pos env already been init!");
            callback.onSuccess(null);
            return;
        }
        final Context applicationContext = context.getApplicationContext();
        mPrintManager = (PrintManager) context.getSystemService("iboxpay_print");
        printPreference = new PrintPreference();
        printPreference.setDisplayIBoxPaySaleSlip(PrintPreference.SALESLIP_HIDE);
        Config.config = new Config(mAppCode, printPreference);
        Config.config.setIboxMchtNo(mMerchantNo);
        try {
            XLogger.i("start init pos env");
            CashboxProxy.getInstance(applicationContext).initAppInfo(Config.config, new IAuthCallback() {
                @Override
                public void onAuthSuccess() {
                    XLogger.i("init pos env ok");
                    mIsInited = true;
                    ThreadManager.postToUI(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(null);
                        }
                    });
                }

                @Override
                public void onAuthFail(final ErrorMsg errorMsg) {
                    XLogger.i("init pos env failed:" + errorMsg.getErrorMsg());
                    ThreadManager.postToUI(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(errorMsg.getErrorCode() + "," + errorMsg.getErrorMsg());
                        }
                    });
                }
            });
        } catch (final Exception e) {
            XLogger.i("init pos env failed:" + e.getLocalizedMessage());
            ThreadManager.postToUI(new Runnable() {
                @Override
                public void run() {
                    callback.onError(e.getLocalizedMessage());
                }
            });
        }
    }

    @Override
    public boolean needChoicePayTypeByCaller() {
        return true;
    }

    public static IPos getInstance() {
        return mInstance;
    }

    @Override
    public String getPackageName() {
        return "com.iboxpay.cashbox.sdk.plug";
    }

    @Override
    public void pay(final Context context, final String tradeNo, final int money, int payType, final PayCallback<Object> callback) {
        PayType posType = PayType.TYPE_CARD;
        switch (payType) {
            case AppConstants.PAY_TYPE_CARD:
                posType = PayType.TYPE_CARD;
                break;
            case AppConstants.PAY_TYPE_WECHART:
                posType = PayType.TYPE_WEIPAY_QRCODE;
                break;
            case AppConstants.PAY_TYPE_ALIPAY:
                posType = PayType.TYPE_ALIPAY;
                break;
        }
        //设置参数
        final String transactionId = String.valueOf(System.currentTimeMillis());
        final String orderTime = DateUtils.doDate2String(new Date());
        ParcelableMap parcelableMap = new ParcelableMap();
        parcelableMap.put(ParcelableMap.TRANSACTION_ID, transactionId);
        parcelableMap.put(ParcelableMap.ORDER_TIME, orderTime);

        //唤起支付SDK
        try {
            //签名
            String sign = SignTool.getSign(Config.config, tradeNo, null, String.valueOf(money), parcelableMap.getMap());

            CashboxProxy.getInstance(context).startTrading(posType, String.valueOf(money),
                    tradeNo, transactionId, SignType.TYPE_MD5, sign, parcelableMap, new ITradeCallback() {
                        @Override
                        public void onTradeSuccess(final ParcelableMap parcelableMap) {
                            ThreadManager.postToUI(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onResult(null, parcelableMap);
                                }
                            });
                        }

                        @Override
                        public void onTradeSuccessWithSign(final ParcelableMap parcelableMap, ParcelableBitmap parcelableBitmap) {
                            ThreadManager.postToUI(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onResult(null, parcelableMap);
                                }
                            });
                        }

                        @Override
                        public void onTradeFail(final ErrorMsg errorMsg) {
                            ThreadManager.postToUI(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onResult(errorMsg.getErrorMsg(), errorMsg);
                                }
                            });
                        }
                    });
        } catch (ConfigErrorException | RuntimeException e) {
            callback.onResult(e.getLocalizedMessage(), e);
        }
    }

    public void cancelPay(Context context, String money, String tradeNo, String cbTradeNo, String orderTime) {
        try {
            String transactionId = String.valueOf(System.currentTimeMillis());
            ParcelableMap parcelableMap = new ParcelableMap();
            parcelableMap.put(ParcelableMap.TRANSACTION_ID, transactionId);
            parcelableMap.put(ParcelableMap.ORDER_TIME, orderTime);

            String sign = SignTool.getSign(Config.config, tradeNo, cbTradeNo, String.valueOf(money), parcelableMap.getMap());
            CashboxProxy.getInstance(context).cancelTrading(money, transactionId, sign, SignType.TYPE_MD5,
                    new TradingNo(tradeNo, cbTradeNo), parcelableMap, new ITradeCallback() {
                        @Override
                        public void onTradeSuccess(ParcelableMap parcelableMap) {
                            XLogger.i("onTradeSuccess");
                        }

                        @Override
                        public void onTradeSuccessWithSign(ParcelableMap parcelableMap, ParcelableBitmap parcelableBitmap) {
                            XLogger.i("onTradeSuccess");
                        }

                        @Override
                        public void onTradeFail(ErrorMsg errorMsg) {
                            XLogger.i("onTradeSuccess");
                        }
                    });
        } catch (ConfigErrorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPayType(Object o) {
        if (o != null && o instanceof ParcelableMap) {
            ParcelableMap parcelableMap = (ParcelableMap) o;
            String payType = parcelableMap.get(ParcelableMap.PAY_TYPE);
            if (payType.equals(PayType.TYPE_WEIPAY_QRCODE.name())) {
                return AppConstants.PAY_TYPE_WECHART;
            } else if (payType.equals(PayType.TYPE_ALIPAY)) {
                return AppConstants.PAY_TYPE_ALIPAY;
            } else if (payType.equals(PayType.TYPE_CARD)) {
                return AppConstants.PAY_TYPE_CARD;
            }
        }
        return AppConstants.PAY_TYPE_UNKNOWN;
    }

    @Override
    public String getTradeNo(Object o) {
        if (o != null && o instanceof ParcelableBitmap) {

        }
        return null;
    }

    @Override
    public String getPayCertificate(Object o) {
        if (o != null && o instanceof ParcelableBitmap) {

        }
        return null;
    }

    @Override
    public String getExtraInfo(Object o) {
        if (o != null && o instanceof ParcelableBitmap) {

        }
        return null;
    }

    @Override
    public boolean isUserCancel(Object o) {
        if (o != null && o instanceof ErrorMsg) {
            ErrorMsg errorMsg = (ErrorMsg) o;
            String errMsgCode = errorMsg.getErrorCode();
            if (errMsgCode != null && errMsgCode.equals("OL-1A019")) {
                return true;
            }
            String errMsg = errorMsg.getErrorMsg();
            if (errMsg != null && errMsg.contains("交易取消")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void printBitmap(byte[] bitmapBytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        mPrintItemJobInfoList.add(new PrintItemJobInfo(bitmap, new GraphParams(160, 160, GraphParams.GRAPH_GRAVITY_CENTER)));
    }

    @Override
    public void printText(String text) {
        printText(text, GRAVITY_LEFT);
    }

    @Override
    public void printText(String text, boolean highLight) {
        printText(text);
    }

    @Override
    public void printRight(String text) {
        printText(text, GRAVITY_RIGHT);
    }

    @Override
    public void printRight(String text, boolean highLight) {
        printRight(text);
    }

    @Override
    public void printCenter(String text) {
        printText(text, GRAVITY_CENTER);
    }

    @Override
    public void printCenter(String text, boolean highLight) {
        printCenter(text);
    }

    private void printText(String text, int gravity) {
        if (!text.endsWith("\n")) {
            text += "\n";
        }
        mPrintItemJobInfoList.add(new PrintItemJobInfo(text, new CharacterParams(1, 1)));
    }

    @Override
    public void printText(String left, String right) {
        printText(left + right, GRAVITY_LEFT);
    }

    @Override
    public void printText(String left, String right, boolean highLight) {
        printText(left, right);
    }

    @Override
    public void printDivide() {
        printText("\n", GRAVITY_CENTER);
    }

    @Override
    public void printEnd() {
        printText(" \n \n \n \n", GRAVITY_CENTER);
        printerFlush();
    }

    @Override
    public String getPosIdentifierNo() {
        String boxSn = CashboxProxy.getBoxSn(MainApplication.getInstance().getApplicationContext());
        XLogger.d("PosIdentifierNo:" + boxSn);
        return boxSn;
    }

    @Override
    public void textToSound(String text) {

    }

    @Override
    public String getMagneticReaderInfo() {
        return null;
    }

    @Override
    public void setPrintListener(Callback<?> callback) {

    }

    private void printerFlush() {
        PrintJobInfo printJobInfo = new PrintJobInfo();
        for (PrintItemJobInfo jobInfo : mPrintItemJobInfoList) {
            printJobInfo.addPrintItemJobTask(jobInfo);
        }
        mPrintItemJobInfoList.clear();
        mPrintManager.printLocaleJob(printJobInfo, new IPrintJobStatusCallback() {
            @Override
            public void onPrintJobStatusChange(int i, String s) throws RemoteException {
                XLogger.i("printer:" + s);
            }

            @Override
            public IBinder asBinder() {
                return null;
            }
        });
    }
}
