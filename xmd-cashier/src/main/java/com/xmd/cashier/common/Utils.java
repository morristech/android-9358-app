package com.xmd.cashier.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;

import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by heyangya on 16-8-22.
 */

public class Utils {
    private static String mVersionName;
    private static int mVersionCode;

    public static void setAppVersion(int versionCode, String versionName) {
        mVersionName = versionName;
        mVersionCode = versionCode;
    }

    public static String getAppVersionName() {
        return mVersionName;
    }

    public static int getAppVersionCode() {
        return mVersionCode;
    }

    public static String getStringFromResource(int resourceId) {
        return MainApplication.getInstance().getApplicationContext().getString(resourceId);
    }

    public static boolean matchPhoneNumFormat(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return Pattern.matches("^1\\d{10}$", text);
    }

    public static boolean matchAmountNumFormat(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return Pattern.matches("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$", text);
    }

    public static String getSecretFormatPhoneNumber(String phoneNumber) {
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, 11);
    }

    public static boolean checkUserName(String username) {
        if (TextUtils.isEmpty(username) || username.length() > 45) {
            return false;
        }
        return true;
    }

    public static boolean checkPassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() > 45 || password.length() < 6) {
            return false;
        }
        return true;
    }

    public static boolean checkSearchNumber(String number) {
        if (TextUtils.isEmpty(number) || number.length() < 11) {
            return false;
        }
        return true;
    }

    public static boolean checkJson(String value) {
        try {
            JSONObject object = new JSONObject(value);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public static boolean checkCode(String value) {
        if (!TextUtils.isEmpty(value) && value.length() >= 12) {
            return true;
        }
        return false;
    }

    public static void showAlertDialogMessage(Context context, String message) {
        new CustomAlertDialogBuilder(context)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    public static String moneyToString(int value) {
        if (value % 100 != 0) {
            int d1 = value / 100;
            int d2 = value % 100;
            return String.format(Locale.CHINA, "%d.%02d", d1, d2);
        } else {
            return String.format(Locale.CHINA, "%d", value / 100);
        }
    }

    public static String moneyToStringEx(int value) {
        int d1 = value / 100;
        int d2 = value % 100;
        return String.format(Locale.CHINA, "%d.%02d", d1, d2);
    }

    //将以元为单位的钱，转换成以分为单位的钱
    public static int stringToMoney(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        } else {
            if (!value.contains(".")) {
                return Integer.parseInt(value) * 100;
            } else {
                String[] datas = value.split("\\.");
                int result = Integer.parseInt(datas[0]) * 100;
                if (datas.length > 1) {
                    if (datas[1].length() == 1) {
                        datas[1] += "0"; //1位小数的时候，在后面补上0
                    }
                    result += Integer.parseInt(datas[1]);
                }
                return result;
            }
        }
    }


    public static boolean isNetworkEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isConnected();
        }
        return false;
    }


    public static String generateTradeNumber() {
        return DeviceInfoUtils.getDeviceSN() + System.currentTimeMillis() / 1000;
    }

    public static String getPayTypeString(int payType) {
        switch (payType) {
            case AppConstants.PAY_TYPE_CARD:
                return "银行卡支付";
            case AppConstants.PAY_TYPE_WECHART:
                return "微信支付";
            case AppConstants.PAY_TYPE_ALIPAY:
                return "支付宝支付";
            case AppConstants.PAY_TYPE_CASH:
                return "现金支付";
            default:
                return "其他支付";
        }
    }

    public static String getPayStatusString(int status) {
        switch (status) {
            case AppConstants.PAY_STATUS_SUCCESS:
                return "交易成功";
            case AppConstants.PAY_STATUS_REFUND:
                return "交易成功(有退款)";
            default:
                return "未知状态";
        }
    }

    public static void maskScreen(Activity activity, boolean toMask) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (toMask) {
            lp.alpha = 0.4f;
        } else {
            lp.alpha = 1.0f;
        }
        activity.getWindow().setAttributes(lp);
    }

    public static String getFormatString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getCustomDateString(Context context, long when) {
        String time = getFormatString(new Date(when), "HH:mm");
        if (DateUtils.isToday(when)) {
            return "今天" + " " + time;
        } else {
            return DateUtils.getRelativeTimeSpanString(context, when) + " " + time;
        }
    }

    /**
     * @param source 需要改变颜色的字符串
     * @param color  要改变成的颜色，比如：{@link Color#RED}
     * @param start  字符串改变颜色的开始的位置
     * @param end    字符串改变颜色的结束的位置（包前不包后）
     * @return {@link Spannable}
     * @Title: changeColor
     * @Description: 改变原字符串的原色。
     */
    public static Spannable changeColor(String source, int color, int start,
                                        int end) {
        if (TextUtils.isEmpty(source)) {
            return new SpannableString("");
        }
        Spannable spannable = new SpannableString(source);
        spannable.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    // 月初 yyyy-MM-dd 00:00:00
    public static String getMonthStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        return com.shidou.commonlibrary.util.DateUtils.doDate2String(calendar.getTime(), com.shidou.commonlibrary.util.DateUtils.DF_DEFAULT);
    }

    // 月末 yyyy-MM-dd 23:59:59
    public static String getMonthEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        return com.shidou.commonlibrary.util.DateUtils.doDate2String(calendar.getTime(), com.shidou.commonlibrary.util.DateUtils.DF_DEFAULT);
    }

    public static String getTimePeriodDes(String useTimePeriod) {
        if (useTimePeriod.equals("周一，周二，周三，周四，周五，周六，周日")) {
            return "不限";
        } else if (useTimePeriod.contains("周一，周二，周三，周四，周五，周六，周日") && useTimePeriod.contains("00")) {
            return useTimePeriod.replace("周一，周二，周三，周四，周五，周六，周日", "每天 ");
        } else {
            return useTimePeriod;
        }
    }
}
