package com.xmd.cashier.pos;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.shidou.commonlibrary.helper.XLogger;
import com.wangpos.by.cashier3.CashierHelper;
import com.xmd.cashier.BuildConfig;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.event.EventPrintResult;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.PayCallback;
import com.xmd.cashier.pos.bean.WanPosPayNewResult;
import com.xmd.cashier.pos.bean.WanPosPayResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.weipass.pos.sdk.BizServiceInvoker;
import cn.weipass.pos.sdk.IPrint;
import cn.weipass.pos.sdk.LatticePrinter;
import cn.weipass.pos.sdk.MagneticReader;
import cn.weipass.pos.sdk.Weipos;
import cn.weipass.pos.sdk.impl.WeiposImpl;
import cn.weipass.service.bizInvoke.RequestInvoke;
import cn.weipass.service.bizInvoke.RequestResult;

/**
 * Created by heyangya on 16-9-6.
 * 旺POS支付
 */

public class PosImpl implements IPos {
    private static final String TAG = "PosImpl";
    private static PosImpl mInstance;
    private BizServiceInvoker mBizServiceInvoker;   //收银2支付服务
    private LatticePrinter mLatticePrinter;
    private MagneticReader mMagneticReader;
    private boolean isInit;

    public static final int smallSize = 24 * 2;
    public static final int mediumSize = 16 * 2;
    public static final int largeSize = 12 * 2;
    public static final int extralargeSize = 8 * 2;

    private PosImpl() {

    }

    public synchronized static PosImpl getInstance() {
        if (mInstance == null) {
            mInstance = new PosImpl();
        }
        return mInstance;
    }

    @Override
    public String getAppCode() {
        return AppConstants.APP_CODE_WEI_POS;
    }

    @Override
    public boolean needCheckUpdate() {
        return false;
    }

    @Override
    public boolean needChoicePayTypeByCaller() {
        return false;
    }

    @Override
    public String getPackageName() {
        return "cn.weipass.cashier";
    }

    @Override
    public void init(Context context, final Callback<?> callback) {
        if (isInit) {
            if (callback != null) {
                callback.onSuccess(null);
            }
            return;
        }
        try {
            WeiposImpl.as().init(context, new Weipos.OnInitListener() {
                @Override
                public void onInitOk() {
                    try {
                        mLatticePrinter = WeiposImpl.as().getService(LatticePrinter.class); //打印服务
                        mMagneticReader = WeiposImpl.as().getService(MagneticReader.class); //磁条卡服务
                        // NFC服务通过NFCManager管理
                        if (callback != null) {
                            callback.onSuccess(null);
                        }
                        isInit = true;
                        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "旺POS初始化成功");
                    } catch (Exception e) {
                        XLogger.i(AppConstants.LOG_BIZ_LOCAL_CONFIG + "旺POS初始化失败(服务初始化)：" + e.getLocalizedMessage());
                        if (callback != null) {
                            callback.onError(e.getLocalizedMessage());
                        }
                    }
                }

                @Override
                public void onError(String s) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "旺POS初始化失败：" + s);
                    if (callback != null) {
                        callback.onError(s);
                    }
                }

                @Override
                public void onDestroy() {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "旺POS初始化失败：onDestroy");
                    if (callback != null) {
                        callback.onError("onDestroy");
                    }
                }
            });
        } catch (Exception e) {
            XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "旺POS初始化失败：" + e.getLocalizedMessage());
            if (callback != null) {
                callback.onError(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void pay(Context context, String tradeNo, int money, int payType, final PayCallback<Object> callback) {
        final WanPosPayResult payResult = new WanPosPayResult();
        payResult.out_trade_no = tradeNo;
        if (Utils.checkWeiPosApp(context, WeiPosCashierSign.PACKAGE_INVOKE_WANGPOS_CASHIER)) {
            // ***** 安装了旺收银 *****
            HashMap<String, String> params = new HashMap<>();
            params.put("app_id", WeiPosCashierSign.InvokeCashier_APPID);
            params.put("out_trade_no", tradeNo);
            params.put("body", "水疗项目");
            params.put("pay_type", String.valueOf(1));
            params.put("total_fee", String.valueOf(money));
            params.put("notify_url", BuildConfig.WANG_POS_NOTIFY_HOST + RequestConstant.WANG_POS_NOTIFY_URL);
//            params.put("activity_path", WeiposResultActivity.class.getName());
            CashierHelper.consume(context, params, new CashierHelper.PayCallBack() {
                @Override
                public void success(String s) {
                    XLogger.i(TAG, "CashierInvoke on success -->" + s);
                    try {
                        WanPosPayNewResult<WanPosPayNewResult.RespData> wanPosPayNewResult = new Gson().fromJson(s, WanPosPayNewResult.class);
                        WanPosPayNewResult.RespData respData = wanPosPayNewResult.data;
                        payResult.out_trade_no = respData.out_trade_no;
                        payResult.trade_status = respData.trade_status;
                        payResult.cashier_trade_no = respData.cashier_trade_no;
                        payResult.operator = respData.operator;
                        payResult.pay_type = String.valueOf(respData.pay_type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    callback.onResult(null, payResult);
                }

                @Override
                public void failed(String s) {
                    XLogger.i(TAG, "CashierInvoke on failed -->" + s);
                    try {
                        WanPosPayNewResult wanPosPayNewResult = new Gson().fromJson(s, WanPosPayNewResult.class);
                        callback.onResult(wanPosPayNewResult.errMsg, payResult);
                    } catch (Exception e) {
                        callback.onResult("解析数据异常", payResult);
                    }
                }
            });
        } else {
            // ***** 未安装旺收银 *****
            try {
                mBizServiceInvoker = WeiposImpl.as().getService(BizServiceInvoker.class);
            } catch (Exception e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "初始化支付调用失败！");
            }
            if (mBizServiceInvoker == null) {
                callback.onResult("发起支付调用失败", payResult);
                return;
            }

            try {
                RequestInvoke cashierReq = new RequestInvoke();
                cashierReq.pkgName = context.getPackageName();
                cashierReq.sdCode = "CASH002";// 收银服务的sdcode信息
                cashierReq.bpId = WeiPosCashierSign.InvokeCashier_BPID;
                cashierReq.launchType = BizServiceInvoker.LAUNCH_TYPE_ACTIVITY;
                cashierReq.params = WeiPosCashierSign.sign(WeiPosCashierSign.InvokeCashier_BPID, WeiPosCashierSign.InvokeCashier_KEY, "POS",
                        "10006", tradeNo, "水疗项目", null, "1", String.valueOf(money), context.getPackageName(), WeiposResultActivity.class.getName());
                cashierReq.seqNo = "1";

                // 设置结果回调
                mBizServiceInvoker.setOnResponseListener(new BizServiceInvoker.OnResponseListener() {
                    @Override
                    public void onResponse(String s, String s1, byte[] bytes) {
                        String payResultString = new String(bytes);
                        XLogger.i(TAG, "onResponse-->" + s + "," + s1 + "," + payResultString);
                        boolean paySuccess = false;
                        String errMsg = null;
                        try {
                            //错误代码:0表示成功，非0表示失败
                            JSONObject jsonObject = new JSONObject(payResultString);
                            paySuccess = jsonObject.getString("errCode").equals("0");
                            //其他支付数据
                            WanPosPayResult wanPosPayResult = new Gson().fromJson(new String(bytes), WanPosPayResult.class);
                            payResult.errCode = wanPosPayResult.errCode;
                            payResult.errMsg = wanPosPayResult.errMsg;
                            payResult.out_trade_no = wanPosPayResult.out_trade_no;
                            payResult.trade_status = wanPosPayResult.trade_status;
                            payResult.cashier_trade_no = wanPosPayResult.cashier_trade_no;
                            payResult.operator = wanPosPayResult.operator;
                            payResult.pay_type = wanPosPayResult.pay_type;
                            payResult.pay_info = wanPosPayResult.pay_info;
                            payResult.buy_user_info = wanPosPayResult.buy_user_info;
                            if (!TextUtils.isEmpty(wanPosPayResult.buy_user_info)) {
                                payResult.mBuyUserInfo = new Gson().fromJson(wanPosPayResult.buy_user_info, WanPosPayResult.BuyUserInfo.class);
                            }
                            errMsg = wanPosPayResult.errMsg + "";//保证errMsg不为null
                        } catch (Exception e) {
                            errMsg = "支付结果=" + payResult.toString();
                            errMsg += ",错误原因=" + e.getLocalizedMessage();
                        } finally {
                            callback.onResult(paySuccess ? null : errMsg, payResult);
                        }
                    }

                    @Override
                    public void onFinishSubscribeService(boolean b, String s) {
                        XLogger.i(TAG, "onFinishSubscribeService-->" + b + "," + s);
                    }
                });

                RequestResult request = mBizServiceInvoker.request(cashierReq);
                if (request != null) {
                    switch (request.result) {
                        case BizServiceInvoker.REQ_SUCCESS: {
                            break;
                        }
                        case BizServiceInvoker.REQ_ERR_INVAILD_PARAM: {
                            callback.onResult("请求参数错误！", payResult);
                            break;
                        }
                        case BizServiceInvoker.REQ_ERR_NO_BP: {
                            callback.onResult("未知的合作伙伴！", payResult);
                            break;
                        }
                        case BizServiceInvoker.REQ_ERR_NO_SERVICE: {
                            callback.onResult("未找到合适的服务！", payResult);
                            break;
                        }
                        case BizServiceInvoker.REQ_NONE: {
                            callback.onResult("请求未知错误！", payResult);
                            break;
                        }
                    }
                } else {
                    callback.onResult("Pos收银回调结果异常", payResult);
                }
            } catch (Exception e) {
                callback.onResult("支付流程异常:" + e.getLocalizedMessage(), payResult);
            }
        }
    }

    @Override
    public int getPayType(Object o) {
        String payType = ((WanPosPayResult) o).pay_type;
        if (TextUtils.isEmpty(payType)) {
            return AppConstants.PAY_TYPE_UNKNOWN;
        }
        switch (payType) {
            case "1001":
                return AppConstants.PAY_TYPE_CASH;
            case "1003":
                return AppConstants.PAY_TYPE_WECHAT;
            case "1004":
                return AppConstants.PAY_TYPE_ALIPAY;
            case "1006"://收银2
            case "1":   //旺收银
                return AppConstants.PAY_TYPE_UNION;
            default:
                return AppConstants.PAY_TYPE_UNKNOWN;
        }
    }

    @Override
    public String getTradeNo(Object o) {
        return ((WanPosPayResult) o).cashier_trade_no;
    }

    @Override
    public String getPayCertificate(Object o) {
        WanPosPayResult result = (WanPosPayResult) o;
        if (result.mBuyUserInfo != null) {
            if (!TextUtils.isEmpty(result.mBuyUserInfo.voucher_no)) {
                return result.mBuyUserInfo.voucher_no;
            }
            if (!TextUtils.isEmpty(result.mBuyUserInfo.third_serial_no)) {
                return result.mBuyUserInfo.third_serial_no;
            }
        }
        return null;
    }

    @Override
    public String getExtraInfo(Object o) {
        WanPosPayResult result = (WanPosPayResult) o;
        if (result.mBuyUserInfo != null) {
            if (!TextUtils.isEmpty(result.mBuyUserInfo.bank_no)) {
                return result.mBuyUserInfo.bank_no;
            }
        }
        return null;
    }

    @Override
    public boolean isUserCancel(Object o) {
        WanPosPayResult result = (WanPosPayResult) o;
        return !TextUtils.isEmpty(result.errMsg) && result.errMsg.equals(WanPosPayResult.PAY_INFO_CANCEL);
    }

    @Override
    public void printBitmap(byte[] bitmap) {
        if (mLatticePrinter != null) {
            mLatticePrinter.printImage(bitmap, IPrint.Gravity.CENTER);
        }
    }

    @Override
    public void printText(String text) {
        printText(text, false);
    }

    @Override
    public void printText(String text, boolean highLight) {
        printText(text, GRAVITY_LEFT, highLight);
    }

    @Override
    public void printRight(String text) {
        printRight(text, false);
    }

    @Override
    public void printRight(String text, boolean highLight) {
        printText(text, GRAVITY_RIGHT, highLight);
    }

    @Override
    public void printCenter(String text) {
        printCenter(text, false);
    }

    @Override
    public void printCenter(String text, boolean highLight) {
        printText(text, GRAVITY_CENTER, highLight);
    }

    private void printText(String text, int gravity, boolean highLight) {
        if (mLatticePrinter != null) {
            String printText;
            switch (gravity) {
                case GRAVITY_CENTER:
                    printText = getBlankBySize(((highLight ? largeSize : mediumSize) - stringSize(text)) / 2) + text;
                    break;
                case GRAVITY_RIGHT:
                    printText = getBlankBySize((highLight ? largeSize : mediumSize) - stringSize(text)) + text;
                    break;
                default:
                    printText = text;
                    break;
            }
            mLatticePrinter.printText(printText + "\n", LatticePrinter.FontFamily.SONG, (highLight ? LatticePrinter.FontSize.LARGE : LatticePrinter.FontSize.MEDIUM), LatticePrinter.FontStyle.NORMAL);
        }
    }

    @Override
    public void printText(String left, String right) {
        printText(left, right, false);
    }

    @Override
    public void printBoldText(String left, String right) {
        printBoldText(left, right, false);
    }

    private void printBoldText(String left, String right, boolean highLight) {
        if (mLatticePrinter != null) {
            String printText = left + getBlankBySize((highLight ? largeSize : mediumSize) - stringSize(left) - stringSize(right)) + right;
            mLatticePrinter.printText(printText + "\n", LatticePrinter.FontFamily.SONG, (highLight ? LatticePrinter.FontSize.LARGE : LatticePrinter.FontSize.MEDIUM), LatticePrinter.FontStyle.BOLD);
        }
    }

    @Override
    public void printText(String left, String right, boolean highLight) {
        if (mLatticePrinter != null) {
            String printText = left + getBlankBySize((highLight ? largeSize : mediumSize) - stringSize(left) - stringSize(right)) + right;
            mLatticePrinter.printText(printText + "\n", LatticePrinter.FontFamily.SONG, (highLight ? LatticePrinter.FontSize.LARGE : LatticePrinter.FontSize.MEDIUM), LatticePrinter.FontStyle.NORMAL);
        }
    }

    @Override
    public void printDivide() {
        if (mLatticePrinter != null) {
            String printText = "";
            for (int i = 0; i < mediumSize; i++) {
                printText += "-";
            }
            mLatticePrinter.printText(printText + "\n", LatticePrinter.FontFamily.SONG, LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.NORMAL);
        }
    }

    @Override
    public void printEnd() {
        if (mLatticePrinter != null) {
            mLatticePrinter.printText("\n\n\n\n\n", LatticePrinter.FontFamily.SONG, LatticePrinter.FontSize.MEDIUM, LatticePrinter.FontStyle.NORMAL);
            mLatticePrinter.submitPrint();
        }
    }

    @Override
    public String getPosIdentifierNo() {
        String deviceInfo = WeiposImpl.as().getDeviceInfo();
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "POS DEVICE INFO：" + deviceInfo);
        try {
            JSONObject object = new JSONObject(deviceInfo);
            return object.getString("en").replace(" ", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void speech(String text) {
        WeiposImpl.as().speech(text);
    }

    @Override
    public String getMagneticReaderInfo() {
        if (mMagneticReader != null) {
            String[] decodeData = mMagneticReader.getCardDecodeThreeTrackData();
            if (decodeData != null && decodeData.length > 0) {
                String retStr = "";
                for (int i = 0; i < decodeData.length; i++) {
                    if (decodeData[i] == null)
                        continue;
                    String txt = decodeData[i].trim();
                    if (retStr.length() > 0) {
                        retStr = retStr + "=";
                    } else {
                        if (txt.indexOf("=") >= 0) {
                            String[] arr = txt.split("=");
                            if (arr[0].length() == 16 || arr[0].length() == 19) {
                                return arr[0];
                            }
                        }
                    }
                    retStr = retStr + txt;
                }
                return retStr;
            } else {
                return null;
            }
        }
        return null;
    }

    // 设置打印的监听
    @Override
    public void setPrintListener() {
        if (mLatticePrinter != null) {
            mLatticePrinter.setOnEventListener(new IPrint.OnEventListener() {
                @Override
                public void onEvent(int what, String info) {
                    String message = "unknown";
                    switch (what) {
                        case IPrint.EVENT_CONNECT_FAILD:
                            message = "连接打印机失败";
                            break;
                        case IPrint.EVENT_CONNECTED:
                            message = "连接打印机成功";
                            break;
                        case IPrint.EVENT_PAPER_JAM:
                            message = "打印机卡纸";
                            break;
                        case IPrint.EVENT_UNKNOW:
                            message = "打印机未知错误";
                            break;
                        case IPrint.EVENT_STATE_OK:
                            message = "打印机状态正常";
                            break;
                        case IPrint.EVENT_OK:
                            message = "打印完成结束";
                            break;
                        case IPrint.EVENT_NO_PAPER:
                            message = "打印机缺纸";
                            break;
                        case IPrint.EVENT_HIGH_TEMP:
                            message = "打印机高温";
                            break;
                        case IPrint.EVENT_PRINT_FAILD:
                            message = "打印失败";
                            break;
                        default:
                            break;
                    }
                    XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "打印结果：" + message);
                    EventBus.getDefault().post(new EventPrintResult(message));
                }
            });
        }
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    private static int stringSize(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    private static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    public static String getBlankBySize(int size) {
        String resultStr = "";
        for (int i = 0; i < size; i++) {
            resultStr += " ";
        }
        return resultStr;
    }
}
