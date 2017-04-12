package com.xmd.technician.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.TechApplication;
import com.xmd.technician.chat.ChatConstant;

import java.util.HashMap;
import java.util.List;
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
     * @param emchatId
     * @param nickname
     * @param avatar
     * @return
     */
    public static Map<String, Object> wrapChatParams(String emchatId, String nickname, String avatar, String userType) {
        Map<String, Object> params = new HashMap<>();
        params.put(ChatConstant.EMCHAT_ID, emchatId);
        params.put(ChatConstant.EMCHAT_NICKNAME, nickname);
        params.put(ChatConstant.EMCHAT_AVATAR, avatar);
        params.put(ChatConstant.EMCHAT_IS_TECH, userType);
        return params;
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

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }


    public static void makeShortToast(Context context, String str) {
        makeToast(context, str, Toast.LENGTH_SHORT);
    }

    public static void makeToast(Context context, String str, int duration) {
        Toast.makeText(context, TextUtils.isEmpty(str) ? ResourceUtils.getString(R.string.default_tips) : str, duration).show();
    }

    public static void reportRegisterEvent(Context context, String step) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("step", step);
        MobclickAgent.onEvent(context, Constant.REGISTER_EVENT, map);
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
        if (activity == null) {
            activity = ActivityHelper.getInstance().getCurrentActivity();
        }
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

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
        Pattern p = Pattern
                .compile("^((13[0-9])|(14[0-9])|(17[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");

        Matcher m = p.matcher(text);

        return m.matches();
    }

    public static boolean matchLoginPassword(String text) {
        if (text == null) {
            return false;
        }
        if (text.length() < 6 || text.length() > 20) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < '0' || (c > '9' && c < 'A') || (c > 'Z' && c < 'a') || c > 'z') {
                return false;
            }
        }
        return true;
    }

    public static boolean matchVerificationCode(String text) {
        return !TextUtils.isEmpty(text) && (text.length() >= 4 && text.length() <= 6);
    }

    public static boolean matchClubInviteCode(String text) {
        return !TextUtils.isEmpty(text) && (text.length() == 6);
    }

    public static boolean matchTechNo(String text) {
        return !TextUtils.isEmpty(text) && (text.length() >= 1 && text.length() <= 5);
    }

    /**
     * @return VersionName
     */
    public static String getVersionName() {
        PackageManager m = TechApplication.getAppContext().getPackageManager();
        String appVersion = "";
        try {
            appVersion = m.getPackageInfo(TechApplication.getAppContext().getPackageName(), 0).versionName;
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
        PackageManager m = TechApplication.getAppContext().getPackageManager();
        int versionCode = -1;
        try {
            versionCode = m.getPackageInfo(TechApplication.getAppContext().getPackageName(), 0).versionCode;
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

    public static Spannable changeStringNumColor(String source, int color) {
        int lastNum = 0;
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) >= 48 && source.charAt(i) <= 57) {
                lastNum = i;
            }
        }
        return changeColor(source, color, 4, lastNum + 1);
    }

    /**
     * @param text  需要添加中划线的字符串
     * @param start 字符串中划线的开始的位置
     * @param end   字符串中划线的结束的位置（包前不包后）
     */
    public static Spannable textStrikeThrough(String text, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
        spannableString.setSpan(strikethroughSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * @param text  需要添加下划线的字符串
     * @param start 字符串下划线的开始的位置
     * @param end   字符串下划线的结束的位置（包前不包后）
     */
    public static Spannable textUnderLine(String text, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
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

    /**
     * 根据浮点数实际有效位数输出，如2.0输出2；2.33输出2.33
     *
     * @param f
     * @return
     */
    public static String getFloat2Str(float f) {
        String fStr = String.valueOf(f);
        while ((fStr.length() > 1) && fStr.endsWith("0")) {
            fStr = fStr.substring(0, fStr.length() - 1);
        }

        if (fStr.endsWith(".")) {
            fStr = fStr.substring(0, fStr.length() - 1);
        }

        return fStr;
    }

    public static String getFloat2Str(String f) {
        String fStr = String.valueOf(f);
        while ((fStr.length() > 1) && fStr.contains(".")&&fStr.endsWith("0")) {
            fStr = fStr.substring(0, fStr.length() - 1);
        }

        if (fStr.endsWith(".")) {
            fStr = fStr.substring(0, fStr.length() - 1);
        }

        return fStr;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getStringMetaData(String metaDataName) {
        PackageManager pm = TechApplication.getAppContext().getPackageManager();
        ApplicationInfo appInfo;
        String metaDataValue = "";
        try {
            appInfo = pm.getApplicationInfo(TechApplication.getAppContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appInfo.metaData;
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

    public synchronized static boolean isNotFastClick() {

        long time = System.currentTimeMillis();
        if (time - lastClickTime < 2000) {
            return false;
        }
        lastClickTime = time;
        return true;
    }
    public static String listToString(List<String> list) {
        if(list.size()>0){
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                buffer.append(list.get(i) + "、");
            }
            return buffer.toString().substring(0, buffer.length() - 1);
        }else{
            return "";
        }

    }
}

