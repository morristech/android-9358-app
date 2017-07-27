package com.xmd.cashier.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

    public static String getPayTypeChannel(int payType) {
        switch (payType) {
            case AppConstants.PAY_TYPE_CARD:
                return "union";
            case AppConstants.PAY_TYPE_WECHART:
                return "wx";
            case AppConstants.PAY_TYPE_ALIPAY:
                return "ali";
            case AppConstants.PAY_TYPE_CASH:
                return "cash";
            default:
                return "other";
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

    public static String getPayChannel(String channel) {
        if (TextUtils.isEmpty(channel)) {
            return null;
        }
        switch (channel) {
            case AppConstants.FAST_PAY_CHANNEL_ALI:
                return "支付宝支付";
            case AppConstants.FAST_PAY_CHANNEL_WX:
                return "微信支付";
            default:
                return null;
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

    public static boolean getCouponValid(String useStartDate, String useEndDate, String useTimePeriod) {
        long now = System.currentTimeMillis();
        if (useStartDate != null && useStartDate.length() != 0) {
            long startDate = com.shidou.commonlibrary.util.DateUtils.doDate2Long(useStartDate);
            if (now < startDate) {
                return false;
            }
        }
        if (useEndDate != null && useEndDate.length() != 0) {
            long endDate = com.shidou.commonlibrary.util.DateUtils.doDate2Long(useEndDate);
            if (now > endDate) {
                return false;
            }
        }
        if (useTimePeriod != null) {
            if (useTimePeriod.equals("不限")) {
                return true;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (!useTimePeriod.contains(getWeekInZh(dayOfWeek))) {
                return false;
            }
            String[] times = useTimePeriod.split(" ");
            String startTime = null;
            String endTime = null;
            for (String s : times) {
                if (s.contains(":")) {
                    if (startTime == null) {
                        startTime = s;
                    } else {
                        endTime = s;
                        break;
                    }
                }
            }
            if (startTime != null && endTime != null) {
                int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
                int nowMinute = calendar.get(Calendar.MINUTE);
                String nowHM = nowHour + ":" + nowMinute;
                startTime = formatTime(startTime);
                endTime = formatTime(endTime);
                if (nowHM.compareTo(startTime) < 0 || nowHM.compareTo(endTime) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static String formatTime(String time) {
        if (time.contains(":")) {
            String hh = time.substring(0, time.lastIndexOf(":"));
            if (hh.length() < 2) {
                return "0" + time;
            }
        }
        return time;
    }

    private static String getWeekInZh(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "周日";
            case Calendar.MONDAY:
                return "周一";
            case Calendar.TUESDAY:
                return "周二";
            case Calendar.WEDNESDAY:
                return "周三";
            case Calendar.THURSDAY:
                return "周四";
            case Calendar.FRIDAY:
                return "周五";
            case Calendar.SATURDAY:
                return "周六";
        }
        return "错误";
    }

    public static Bitmap getQRBitmap(String content) throws WriterException {
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        int width = 350;
        int height = 350;
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    // 保留手机号码前3后4
    public static String formatPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return null;
        } else {
            return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
    }

    // 保留后4位
    public static String formatCode(String code) {
        if (TextUtils.isEmpty(code)) {
            return null;
        } else {
            return code.replaceAll("\\d*(\\d{4})", "****$1");
        }
    }
}
