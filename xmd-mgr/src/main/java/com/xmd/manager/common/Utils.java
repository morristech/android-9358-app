package com.xmd.manager.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xmd.app.XmdActivityManager;
import com.xmd.manager.AppConfig;
import com.xmd.manager.ManagerApplication;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdcm on 15-10-26.
 */
public class Utils {
    private static long lastClickTime;

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static ProgressDialog getSpinnerProgressDialog(Context context, int resId) {
        return getSpinnerProgressDialog(context, ResourceUtils.getString(resId));
    }

    public static ProgressDialog getSpinnerProgressDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context, ProgressDialog.THEME_TRADITIONAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    /**
     * 遮罩效果
     *
     * @param activity
     * @param toMask
     */
    public static void maskScreen(Activity activity, boolean toMask) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (toMask) {
            lp.alpha = 0.4f;
        } else {
            lp.alpha = 1.0f;
        }
        activity.getWindow().setAttributes(lp);
    }


    public static boolean isNotEmpty(String str) {
        return str != null && str.length() != 0;
    }


    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }


    public static void makeShortToast(Context context, String str) {
        ToastUtils.showToastShort(context, str);
    }

    /**
     * http://xxx:port/path
     * http://xxx:port/path#
     * http://xxx:port/path?parama=x
     * http://xxx:port/path?parama=x#1
     * http://xxx:port/path?parama=x&paramb=y
     * http://xxx:port/path?parama=x&paramb=y#1
     * <p>
     * Result:
     * http://xxx:port/path?test=b
     * http://xxx:port/path?test=b#
     * http://xxx:port/path?parama=x&test=b
     * http://xxx:port/path?parama=x&test=b#1
     * http://xxx:port/path?parama=x&paramb=y&test=b
     * http://xxx:port/path?parama=x&paramb=y&test=b#1
     *
     * @param url
     * @param param
     * @param value
     * @return
     */
    public static String addParams(String url, String param, String value) {
        if (isEmpty(param)) {
            return url;
        }
        String tempUrl = url;
        int anchor = tempUrl.indexOf("#");
        String anchorStr = "";
        if (anchor > 0) {
            anchorStr = tempUrl.substring(anchor);
            tempUrl = tempUrl.substring(0, anchor);
        }
        int qIndex = tempUrl.indexOf("?");
        tempUrl += (qIndex == -1 ? "?" : "&") + param + "=" + value + anchorStr;
        return tempUrl;
    }

    /**
     * 获取当前屏幕的高度宽度
     *
     * @param activity
     * @return
     */
    public static int[] getScreenWidthHeight(Activity activity) {
        if (AppConfig.sScreenWidth < 0) {
            if (activity == null) {
                activity = XmdActivityManager.getInstance().getCurrentActivity();
            }
            if (activity == null) {
                return new int[]{0, 0};
            }
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            AppConfig.sScreenWidth = metrics.widthPixels;
            AppConfig.sScreenHeight = metrics.heightPixels;
        }
        return new int[]{AppConfig.sScreenWidth, AppConfig.sScreenHeight};
    }

    /**
     * 获取当前屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return getScreenWidthHeight(null)[0];
    }

    /**
     * @param url
     * @param param
     * @param value
     * @return
     */
    public static String appendParams(String url, String param, String value) {
        if (isEmpty(param)) {
            return url;
        }
        String tempUrl = url;
        tempUrl += "&" + param + "=" + value;
        return tempUrl;
    }

    /**
     * @param text
     * @return
     */
    public static boolean matchPhoneNumFormat(String text) {
        if (isEmpty(text)) {
            return false;
        }
        return Pattern.matches("^1\\d{10}$", text);
    }

    public static int getIntMetaData(String metaDataName) {
        PackageManager pm = ManagerApplication.getAppContext().getPackageManager();
        ApplicationInfo appinfo;
        int metaDataValue = -1;
        try {
            appinfo = pm.getApplicationInfo(ManagerApplication.getAppContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getInt(metaDataName);
            return metaDataValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataValue;
    }

    public static String getStringMetaData(String metaDataName) {
        PackageManager pm = ManagerApplication.getAppContext().getPackageManager();
        ApplicationInfo appinfo;
        String metaDataValue = "";
        try {
            appinfo = pm.getApplicationInfo(ManagerApplication.getAppContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getString(metaDataName);
            return metaDataValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataValue;
    }

    public static String StrSubstring(int length, String s, Boolean end) {
        if (TextUtils.isEmpty(s) || length <= 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        int sum = 0;
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (sum >= (length * 3)) {
                if (end) {
                    stringBuffer.append("...");
                }
                break;
            }
            char bt = chars[i];
            if (bt > 64 && bt < 123) {
                stringBuffer.append(String.valueOf(bt));
                sum += 2;
            } else {
                stringBuffer.append(String.valueOf(bt));
                sum += 3;
            }
        }
        return stringBuffer.toString();
    }


    /**
     * @return VersionName
     */
    public static String getVersionName() {
        PackageManager m = ManagerApplication.getAppContext().getPackageManager();
        String appVersion = "";
        try {
            appVersion = m.getPackageInfo(ManagerApplication.getAppContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Exception won't be thrown as the current package name is
            // safe to exist on the system.
            throw new AssertionError();
        }

        return appVersion;
    }

    /**
     * @return
     */
    public static int getVersionCode() {
        PackageManager m = ManagerApplication.getAppContext().getPackageManager();
        int versionCode = -1;
        try {
            versionCode = m.getPackageInfo(ManagerApplication.getAppContext().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Exception won't be thrown as the current package name is
            // safe to exist on the system.
            throw new AssertionError();
        }

        return versionCode;
    }

    public static void printHashMap(String desc, Map<String, String> map) {
        Logger.v("/******************* " + desc + " ************************/");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Logger.v(entry.getKey() + " : " + entry.getValue());
        }
        Logger.v("/*********************************************/");
    }


    /**
     * @param changeStr  要改变颜色的字符串
     * @param colorStr   要改变成的颜色的十六进制表示 如#ffffff
     * @param startIndex 开始下标
     * @param endIndex   结束下标
     * @return
     */
    public static Spannable changeColor(String changeStr, String colorStr,
                                        int startIndex, int endIndex) {
        if (changeStr == null) {
            return new SpannableString("");
        }
        Spannable ss = new SpannableString(changeStr);
        //make sure the endIndex is less than the length of the change string.
        int len = changeStr.length();
        if (endIndex >= len) {
            endIndex = len - 1;
        }
        ss.setSpan(new ForegroundColorSpan(Color.parseColor(colorStr)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }


    /**
     * @param changeStr 要改变颜色的字符串
     * @param colorStr  要改变成的颜色的十六进制表示 如#ffffff
     * @return
     */
    public static Spannable changeColor(String changeStr, String colorStr) {
        Spannable ss = new SpannableString(changeStr);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor(colorStr)), 0, changeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * @param source 需要改变颜色的字符串
     * @param color  要改变成的颜色，比如：{@link Color#RED}
     * @return {@link Spannable}
     * @Title: changeColor
     * @Description: 改变原字符串的原色。
     */
    public static Spannable changeColor(String source, int color) {
        if (isEmpty(source)) {
            return new SpannableString("");
        }
        return changeColor(source, color, 0, source.length());
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
        if (isEmpty(source)) {
            return new SpannableString("");
        }
        Spannable spannable = new SpannableString(source);
        spannable.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * @param tag
     */
    public static void printThreadName(String tag) {
        String threadName = Thread.currentThread().getName();
        Logger.v(tag + " in thread : " + threadName);
    }


    /**
     * @param context
     * @return
     * @Description: Returns the unique device ID, for example, the IMEI for GSM
     * and the MEID for CDMA phones. Return "null" if device ID is
     * not available.
     */
    public static String getDeviceImei(Context context) {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tManager.getDeviceId();
        return imei;
    }

    /**
     * @param context
     * @return
     * @Description: Returns the unique subscriber ID, for example, the IMSI for
     * a GSM phone. Return "null" if it is unavailable.
     */
    public static String getDeviceImsi(Context context) {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = tManager.getSubscriberId();
        return imsi;
    }

    /**
     * @param context
     * @return
     * @Description: 返回Wifi的物理地址，或者"null"
     */
    public static String getDeviceWifiMac(Context context) {
        String wifiMac = "";
        WifiManager wManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wManager.getConnectionInfo();
        if (info != null) {
            wifiMac = info.getMacAddress();
        }
        return wifiMac;
    }

    /**
     * shorten the string with ... to express if it's too long
     *
     * @param text
     * @param length
     * @return
     */
    public static String briefString(String text, int length) {
        if (isEmpty(text)) {
            return "";
        }

        if (text.length() <= length) {
            return text;
        }

        int startIndex = length / 2;
        String prefix = text.substring(0, startIndex);
        String subfix = text.substring(text.length() - startIndex, text.length());

        return prefix + "..." + subfix;

    }

    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static boolean matchSecurityCodeFormat(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        return Pattern.matches("^\\d{6}$", text);
    }

    public static boolean matchPassWordFormat(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }

        if (password.length() < 6 || password.length() > 20) {
            return false;
        }

        Pattern p = Pattern.compile("[^a-zA-Z0-9]");
        Matcher m = p.matcher(password);
        return password.equals(m.replaceAll("".trim()));
    }

    /**
     * 从图库获取图片
     */
    public static void selectPicFromLocal(Activity activity, int requestCode) {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * bitmap转换
     *
     * @param bitmap
     * @return
     */
    public static String bitmap2base64(Bitmap bitmap) {
        String imgFile = null;
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baoStream);
        byte[] bytes = baoStream.toByteArray();
        imgFile = "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
        return imgFile;
    }

    public static String bitmap2base64(Bitmap bitmap, boolean needRecycle) {
        String imgFile = null;
        /*ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baoStream);
        byte[] bytes = baoStream.toByteArray();*/

        byte[] bytes = bmpToByteArray(bitmap, needRecycle);
        imgFile = "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
        return imgFile;
    }

    public static byte[] bmpToByteArray(Bitmap bmp, boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    /**
     * @param activity
     * @param phoneNum
     * @param body
     */
    public static void invokeAndroidSms(Activity activity, String phoneNum, String body) {
        Uri smsToUri = Uri.parse("smsto:" + phoneNum);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", body);
        activity.startActivity(intent);
    }

    /******************************************************** Main *************************/

    /**
     * @param args
     */
    public static void main(String[] args) {
        String[] urls = {
                "http://xxx:port/path",
                "http://xxx:port/path#",
                "http://xxx:port/path?parama=x",
                "http://xxx:port/path?parama=x#1",
                "http://xxx:port/path?parama=x&paramb=y",
                "http://xxx:port/path?parama=x&paramb=y#1"
        };

        for (String url : urls) {
            System.out.println(addParams(url, "test", "b"));
        }
    }

    public static int dateToInt(String str) {
        if (!str.isEmpty() && str.length() == 10) {
            String year = str.substring(0, 4);
            String mounth = str.substring(5, 7);
            String day = str.substring(8, 10);
            String newStr = year + mounth + day;
            int result = Integer.parseInt(newStr);
            return result;
        }
        return -1;
    }

    public static String getCurrentActivityName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String currentClassName = componentInfo.getClassName();
        String className = currentClassName.substring(currentClassName.lastIndexOf(".") + 1);
        return className;
    }

    public synchronized static boolean isNotFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return false;
        }
        lastClickTime = time;
        return true;
    }

    public static String getNumToString(int num, boolean isElement) {
        float moneyFloat = 0.00f;
        int moneyInt = 0;
        if (isElement) {
            moneyInt = num;
        } else {
            moneyFloat = num / 100f;
        }
        if (moneyInt != 0) {
            if (moneyInt > 10000) {
                float wan = moneyInt / 10000f;
                return String.format(Locale.getDefault(), "%1.2f万", wan);
            } else {
                return String.valueOf(moneyInt);
            }
        } else if (moneyFloat != 0) {
            if (moneyFloat > 10000) {
                float wan = moneyFloat / 10000f;
                return String.format(Locale.getDefault(), "%1.2f万", wan);
            } else {
                return String.valueOf(moneyFloat);
            }
        } else {
            return String.valueOf(num);
        }

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String listToString(List<String> list) {
        if (list.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                buffer.append(list.get(i) + ",");
            }
            return buffer.toString().substring(0, buffer.length() - 1);
        } else {
            return "";
        }

    }

    public static boolean couponIsCanUse(String useStartDate, String useEndDate, String useTimePeriod) {
        long now = System.currentTimeMillis();
        if (Utils.isNotEmpty(useStartDate)) {
            long startDate = DateUtil.dateToLong(useStartDate);
            if (now < startDate) {
                return false;
            }
        }
        if (Utils.isNotEmpty(useEndDate)) {
            long endDate = DateUtil.dateToLong(useEndDate);
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
            if (!useTimePeriod.contains(DateUtil.getWeekInZh(dayOfWeek))) {
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


}
