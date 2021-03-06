package com.xmd.cashier.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        if (TextUtils.isEmpty(password) || password.length() > 45) {
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

    public static String moneyToStringEx(long value) {
        long d1 = value / 100;
        long d2 = value % 100;
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

    public static boolean isWifiNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 获取网络类型
     * @param context
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return "broken";
        }
        String netWorkType = networkInfo.getTypeName();
        return netWorkType;
    }

    public static boolean isAboveKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static String generateTradeNumber() {
        return DeviceInfoUtils.getDeviceSN() + System.currentTimeMillis() / 1000;
    }

    public static String getPayTypeString(int payType) {
        switch (payType) {
            case AppConstants.PAY_TYPE_UNION:
                return AppConstants.CASHIER_TYPE_UNION_TEXT;
            case AppConstants.PAY_TYPE_WECHAT:
                return AppConstants.CASHIER_TYPE_WX_TEXT;
            case AppConstants.PAY_TYPE_ALIPAY:
                return AppConstants.CASHIER_TYPE_ALI_TEXT;
            case AppConstants.PAY_TYPE_CASH:
                return AppConstants.CASHIER_TYPE_CASH_TEXT;
            default:
                return AppConstants.CASHIER_TYPE_OTHER_TEXT;
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
            return "未知";
        }
        switch (channel) {
            case AppConstants.PAY_CHANNEL_ALI:
                return AppConstants.CASHIER_TYPE_ALI_TEXT;
            case AppConstants.PAY_CHANNEL_WX:
                return AppConstants.CASHIER_TYPE_WX_TEXT;
            case AppConstants.PAY_CHANNEL_ACCOUNT:
                return AppConstants.CASHIER_TYPE_ACCOUNT_TEXT;
            case AppConstants.PAY_CHANNEL_UNION:
                return AppConstants.CASHIER_TYPE_UNION_TEXT;
            default:
                return "其他";
        }
    }

    public static String getQRPlatform(String qrType) {
        if (TextUtils.isEmpty(qrType)) {
            return "未知";
        }
        switch (qrType) {
            case AppConstants.QR_TYPE_POS:
                return "POS机";
            case AppConstants.QR_TYPE_CLUB:
                return "收银二维码";
            case AppConstants.QR_TYPE_TECH:
                return "技师二维码";
            default:
                return "其他";
        }
    }

    public static String getPlatform(String platform) {
        if (TextUtils.isEmpty(platform)) {
            return "未知平台";
        }
        switch (platform) {
            case AppConstants.PLATFORM_OFFLINE:
                return "管理者后台";
            case AppConstants.PLATFORM_ONLINE:
                return "线上";
            case AppConstants.PLATFORM_CASHIER:
                return "POS机";
            default:
                return "其他平台";
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

    public static String getCustomDateString(Context context, long when) {
        String time = com.shidou.commonlibrary.util.DateUtils.doDate2String(new Date(when), "HH:mm");
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
        return com.shidou.commonlibrary.util.DateUtils.doDate2String(calendar.getTime());
    }

    // 月末 yyyy-MM-dd 23:59:59
    public static String getMonthEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        return com.shidou.commonlibrary.util.DateUtils.doDate2String(calendar.getTime());
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
            return code.replaceAll("[A-Za-z0-9]*([A-Za-z0-9]{4})", "****$1");
        }
    }

    // 保留前5位
    public static String formatName(String name, boolean keep) {
        if (TextUtils.isEmpty(name)) {
            return null;
        } else {
            if (keep) {
                return name.replaceAll("([\\s\\S]{5})[\\s\\S]*", "$1***");
            } else {
                return name.replaceAll("([\\s\\S]{1})[\\s\\S]*", "$1***");
            }
        }
    }

    public static boolean checkWeiPosApp(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> infos = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : infos) {
            if (packageInfo.packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void zipFiles(String folderPath, String filePath, ZipOutputStream zipOut) throws Exception {
        if (zipOut == null) {
            return;
        }

        File file = new File(folderPath + filePath);

        //判断是不是文件
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(filePath);
            FileInputStream inputStream = new FileInputStream(file);
            zipOut.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];

            while ((len = inputStream.read(buffer)) != -1) {
                zipOut.write(buffer, 0, len);
            }

            zipOut.closeEntry();
        } else {
            //文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            //如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(filePath + File.separator);
                zipOut.putNextEntry(zipEntry);
                zipOut.closeEntry();
            }

            //如果有子文件, 遍历子文件
            for (int i = 0; i < fileList.length; i++) {
                zipFiles(folderPath, filePath + File.separator + fileList[i], zipOut);
            }
        }
    }
}
