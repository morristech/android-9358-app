package com.xmd.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.alive.InitAliveReport;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Set;

/**
 * Created by heyangya on 17-5-23.
 * 模块初始化入口
 */

public class XmdApp {

    private static final XmdApp ourInstance = new XmdApp();

    public static XmdApp getInstance() {
        return ourInstance;
    }

    private XmdApp() {
    }


    public static final String FUNCTION_ALIVE_REPORT = "function_alive_report";//在线汇报
    private static boolean FUNCTION_ALIVE_REPORT_INIT;

    public static final String FUNCTION_USER_INFO = "function_user_info";//用户信息
    private static boolean FUNCTION_USER_INFO_INIT;

    private boolean MODULE_INIT; //模块初始化
    private Context mApplicationContext;
    private String mServer;
    private String mToken;

    private SharedPreferences sharedPreferences;
    private Boolean appFirstStart; //app是否为第一次启动

    /**
     * 初始化模块
     *
     * @param applicationContext app application contex
     * @param server             服务器地址 ， http://xxx
     * @param functions          需要初始化的功能模块
     */
    public void init(Context applicationContext, String server, Set<String> functions) {
        mApplicationContext = applicationContext;
        mServer = server;

        if (!MODULE_INIT) {
            MODULE_INIT = true;
            EventBus.getDefault().register(this);
            sharedPreferences = applicationContext.getSharedPreferences("xmd-app", Context.MODE_PRIVATE);
        }

        //初始化心跳功能
        if (functions.contains(FUNCTION_ALIVE_REPORT) && !FUNCTION_ALIVE_REPORT_INIT) {
            XLogger.i("---init " + FUNCTION_ALIVE_REPORT);
            new InitAliveReport().init(mApplicationContext);
            FUNCTION_ALIVE_REPORT_INIT = true;
        }

        //初始化用户信息
        if (functions.contains(FUNCTION_USER_INFO) && !FUNCTION_USER_INFO_INIT) {
            XLogger.i("---init " + FUNCTION_USER_INFO);
            UserInfoServiceImpl.getInstance().init(mApplicationContext);
            FUNCTION_USER_INFO_INIT = true;
        }
    }

    /**
     * 登录事件，设置token到header
     */
    @Subscribe(sticky = true)
    public void onLogin(EventLogin eventLogin) {
        XLogger.i("event login, set token " + eventLogin.getToken());
        XmdNetwork.getInstance().setToken(eventLogin.getToken());
    }

    /**
     * 登出事件 ，清除token
     */
    @Subscribe(sticky = true, priority = -5)
    public void onLogout(EventLogout eventLogout) {
        XLogger.i("event logout");
        XmdNetwork.getInstance().setToken(null);
    }


    public Context getContext() {
        return mApplicationContext;
    }

    public String getServer() {
        return mServer;
    }


    public void setToken(String token) {
        this.mToken = token;
    }

    //返回sp
    public SharedPreferences getSp() {
        return sharedPreferences;
    }

    //app是否为首次启动
    public boolean isAppFirstStart() {
        if (appFirstStart == null) {
            appFirstStart = getSp().getBoolean(SpConstants.KEY_IS_APP_FIRST_START, true);
            if (appFirstStart) {
                getSp().edit().putBoolean(SpConstants.KEY_IS_APP_FIRST_START, false).apply();
            }
        }
        return appFirstStart;
    }

    public boolean isDevelopMode() {
        return getSp().getBoolean(SpConstants.KEY_DEV_MODE, false);
    }

    public void setDevelopMode(boolean debugMode) {
        getSp().edit().putBoolean(SpConstants.KEY_DEV_MODE, debugMode).apply();
    }

}
