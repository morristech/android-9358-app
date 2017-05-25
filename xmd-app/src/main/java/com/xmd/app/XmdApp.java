package com.xmd.app;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.alive.InitAliveReport;
import com.xmd.app.appointment.InitAppointment;
import com.xmd.app.net.RetrofitFactory;

import java.util.Set;

/**
 * Created by heyangya on 17-5-23.
 * 模块初始化入口
 */

public class XmdApp {
    public static final String FUNCTION_ALIVE_REPORT = "function_alive_report";//在线汇报
    private static boolean FUNCTION_ALIVE_REPORT_INIT;

    public static final String FUNCTION_APPOINTMENT = "function_appointment"; //预约
    private static boolean FUNCTION_APPOINTMENT_INIT;

    private static Context sApplicationContext;
    private static String sServer;

    /**
     * 初始化模块
     *
     * @param applicationContext app application contex
     * @param server             服务器地址 ， http://xxx
     * @param functions          需要初始化的功能模块
     */
    public static void init(Context applicationContext, String server, Set<String> functions) {
        sApplicationContext = applicationContext;
        sServer = server;

        //初始化心跳功能
        if (functions.contains(FUNCTION_ALIVE_REPORT) && !FUNCTION_ALIVE_REPORT_INIT) {
            XLogger.i("---init " + FUNCTION_ALIVE_REPORT);
            new InitAliveReport().init(sApplicationContext);
            FUNCTION_ALIVE_REPORT_INIT = true;
        }

        //初始化预约功能
        if (functions.contains(FUNCTION_APPOINTMENT) && !FUNCTION_APPOINTMENT_INIT) {
            XLogger.i("---init " + FUNCTION_APPOINTMENT);
            new InitAppointment().init(sApplicationContext);
            FUNCTION_APPOINTMENT_INIT = true;
        }
    }

    /**
     * 切换服务器环境时调用
     * @param server 服务器地址 : http://xxx
     */
    public static void setServer(String server) {
        if (sServer != null && !sServer.equals(server)) {
            sServer = server;
            RetrofitFactory.clear();
        } else {
            sServer = server;
        }
    }


    public static Context getContext() {
        return sApplicationContext;
    }

    public static String getServer() {
        return sServer;
    }
}
