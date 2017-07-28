package com.xmd.cashier.pos;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.PayCallback;
import com.xmd.cashier.pos.bean.WanPosPayResult;

import org.json.JSONException;
import org.json.JSONObject;

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
 */

public class PosImpl implements IPos {
    private static PosImpl mInstance;
    private BizServiceInvoker mBizServiceInvoker;
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
    public void init(Context context, final Callback<?> callback) {
        if (isInit) {
            callback.onSuccess(null);
            return;
        }
        try {
            WeiposImpl.as().init(context, new Weipos.OnInitListener() {
                @Override
                public void onInitOk() {
                    try {
                        // 初始化服务调用
                        mBizServiceInvoker = WeiposImpl.as().getService(BizServiceInvoker.class);

                        initPrinter();  //初始化打印
                        initMagneticReader();   //初始化磁条刷卡

                        callback.onSuccess(null);
                        isInit = true;
                    } catch (Exception e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                }

                @Override
                public void onError(String s) {
                    callback.onError(s);
                }

                @Override
                public void onDestroy() {
                    callback.onError("onDestroy");
                }
            });
        } catch (Exception e) {
            callback.onError(e.getLocalizedMessage());
        }
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
    public void pay(Context context, String tradeNo, int money, int payType, final PayCallback<Object> callback) {
        final WanPosPayResult payResult = new WanPosPayResult();
        payResult.out_trade_no = tradeNo;
        try {
            RequestInvoke cashierReq = new RequestInvoke();
            cashierReq.pkgName = context.getPackageName();
            cashierReq.sdCode = "CASH002";// 收银服务的sdcode信息
            cashierReq.bpId = WeiPosCashierSign.InvokeCashier_BPID;
            cashierReq.launchType = BizServiceInvoker.LAUNCH_TYPE_ACTIVITY;
            cashierReq.params = WeiPosCashierSign.sign(WeiPosCashierSign.InvokeCashier_BPID, WeiPosCashierSign.InvokeCashier_KEY, "POS",
                    "10004", tradeNo, "水疗项目", null, "1", String.valueOf(money), context.getPackageName(), WeiposResultActivity.class.getName());
            cashierReq.seqNo = "1";

            // 设置结果回调
            mBizServiceInvoker.setOnResponseListener(new BizServiceInvoker.OnResponseListener() {
                @Override
                public void onResponse(String s, String s1, byte[] bytes) {
                    String payResultString = new String(bytes);
                    XLogger.i("onResponse-->" + s + "," + s1 + "," + payResultString);
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
                return AppConstants.PAY_TYPE_WECHART;
            case "1004":
                return AppConstants.PAY_TYPE_ALIPAY;
            case "1006":
                return AppConstants.PAY_TYPE_CARD;
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
        XLogger.d("PosIdentifierNo:" + deviceInfo);
        try {
            JSONObject object = new JSONObject(deviceInfo);
            return object.getString("en").replace(" ", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void textToSound(String text) {
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

    @Override
    public void setPrintListener(final Callback<?> callback) {
        if (mLatticePrinter != null) {
            mLatticePrinter.setOnEventListener(new IPrint.OnEventListener() {
                @Override
                public void onEvent(int i, String s) {
                    switch (i) {
                        case IPrint.EVENT_CONNECTED:
                            //连接打印机成功
                        case IPrint.EVENT_STATE_OK:
                            //打印机状态正常
                            break;
                        case IPrint.EVENT_OK:
                            //打印完成结束
                            if (callback != null) {
                                callback.onSuccess(null);
                            }
                            break;
                        default:
                            if (callback != null) {
                                callback.onError(s);
                            }
                            break;
                    }
                }
            });
        }
    }


    private void initPrinter() {
        try {
            // 设备可能没有打印机，open会抛异常
            mLatticePrinter = WeiposImpl.as().openLatticePrinter();
        } catch (Exception e2) {
            XLogger.e("can not init latticePrinter");
        }
    }

    private void initMagneticReader() {
        try {
            mMagneticReader = WeiposImpl.as().openMagneticReader();
        } catch (Exception e) {
            XLogger.e("can not init MagneticReader");
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
