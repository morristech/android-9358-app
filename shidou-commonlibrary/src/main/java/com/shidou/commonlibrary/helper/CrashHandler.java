package com.shidou.commonlibrary.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.shidou.commonlibrary.widget.XToast;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {
    public interface Callback {
        void onExitApplication();
    }

    /**
     * Debug Log tag
     */
    public static final String TAG = "com.shidou.commonlibrary.helper.CrashHandler";

    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String VERSION_SDK = "version_sdk";
    private static final String VERSION_SDK_INT = "version_sdk_int";
    private static final String VERSION_RELEASE = "version_release";

    private Map<String, String> mDeviceCrashInfo = new HashMap<String, String>();

    private static Callback mCallback;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx, Callback callback) {
        mContext = ctx;
        mCallback = callback;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {// Sleep一会后结束程序
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                //log.debug(e);
            }
            mCallback.onExitApplication();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        XToast.showInNotUILong("应用出现异常，即将退出");
        // 收集设备信息
        collectCrashDeviceInfo(mContext);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mDeviceCrashInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }
        XLogger.e(TAG, sb.toString());
        // 保存错误报告文件
        XLogger.e(TAG, "", ex);
        return true;
    }

    /**
     * 收集程序崩溃的设备信息
     *
     * @param ctx
     */
    public void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
                mDeviceCrashInfo.put(VERSION_SDK, "" + android.os.Build.VERSION.SDK);
                mDeviceCrashInfo.put(VERSION_RELEASE, "" + android.os.Build.VERSION.RELEASE);
                mDeviceCrashInfo.put(VERSION_SDK_INT, "" + android.os.Build.VERSION.SDK_INT);
            }
        } catch (NameNotFoundException e) {
            //log.debug("Error while collect package info" + e);
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        // 具体信息请参考后面的截图
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), "" + field.get(null));
                //log.debug(field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                //log.debug("Error while collect crash info" + e);
            }
        }
    }

}
