package com.shidou.commonlibrary;

import android.app.Application;
import android.os.Environment;
import android.os.SystemClock;
import android.test.ApplicationTestCase;

import com.shidou.commonlibrary.helper.XLogger;

import java.io.File;
import java.io.IOException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
    }

    public void testXLogger() {
        XLogger.init(2, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
//        Thread thread1=new Thread() {
//            @Override
//            public void run() {
        long now = SystemClock.elapsedRealtime();
        XLogger.v("XLogger", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        for (int i = 0; i < 100; i++) {
            XLogger.e("XLogger", "+++++" + i);
        }
        XLogger.e("XLogger", "+++++write 1000 cost:" + (SystemClock.elapsedRealtime() - now));
//            }
////        };
//        thread1.start();
//        Thread thread2=new Thread() {
//            @Override
//            public void run() {
//                long now = SystemClock.elapsedRealtime();
//                for (int i = 0; i < 3333; i++) {
//                    XLogger.e("XLogger", "--------" + i);
//                }
//                XLogger.e("XLogger", "-----write 1000 cost:" + (SystemClock.elapsedRealtime() - now));
//            }
//        };
//        thread2.start();
//        Thread thread3=new Thread() {
//            @Override
//            public void run() {
//                long now = SystemClock.elapsedRealtime();
//                for (int i = 0; i < 3333; i++) {
//                    XLogger.e("XLogger", "****" + i);
//                }
//                XLogger.e("XLogger", "****write 1000 cost:" + (SystemClock.elapsedRealtime() - now));
//            }
//        };
//        thread3.start();
//        try {
//            thread1.join();
////            thread2.join();
////            thread3.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        File totalLog = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "totalLog");
        if (!totalLog.exists()) {
            try {
                totalLog.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        XLogger.copyLogsToFile(totalLog);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        XLogger.deInit();
    }
}