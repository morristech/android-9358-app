package com.xmd.technician.common;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import com.xmd.technician.AppConfig;
import com.xmd.technician.window.WelcomeActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdcm on 15-10-26.
 */
public class AppUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    private Context mContext;

    public AppUncaughtExceptionHandler(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Logger.e("程序出错了", ex);
        // 收集异常
        collectDeviceInfo();
        saveCrashInfo2File(ex);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext,"T_T, 程序崩溃了，将重新启动", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restart();
    }

    /**
     * restart the app
     */
    private void restart(){
        // 重新启动应用
        Intent intent = new Intent(mContext, WelcomeActivity.class);
        // 销毁进程空间所有活动
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        ActivityHelper.getInstance().removeAllActivities();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    /**
     * 收集设备参数信息
     */
    public void collectDeviceInfo() {
        infos.put("versionName", AppConfig.getAppVersionName());
        infos.put("versionCode", AppConfig.getAppVersionNameAndCode());
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Logger.d(field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Logger.e("an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return	返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = AppConfig.getAppFolder() + "/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Logger.e("an error occured while writing file...", e);
        }
        return null;
    }


}
