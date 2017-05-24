package com.xmd.app;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.alive.InitAliveReport;
import com.xmd.app.appointment.InitAppointment;

import java.util.Set;

/**
 * Created by heyangya on 17-5-23.
 * 模块初始化入口
 */

public class Init {
    public static final String FUNCTION_ALIVE_REPORT = "function_alive_report";//在线汇报
    private static boolean FUNCTION_ALIVE_REPORT_INIT;

    public static final String FUNCTION_APPOINTMENT = "function_appointment"; //预约
    private static boolean FUNCTION_APPOINTMENT_INIT;

    private static Context sApplicationContext;

    public static void init(Context applicationContext, Set<String> functions) {
        sApplicationContext = applicationContext;

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


    public static Context getContext() {
        return sApplicationContext;
    }
}
