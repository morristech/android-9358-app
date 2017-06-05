package com.xmd.app;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.network.OkHttpUtil;
import com.xmd.app.alive.InitAliveReport;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.net.RetrofitFactory;
import com.xmd.app.user.UserInfoServiceImpl;

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
     * 切换服务器环境时调用
     *
     * @param server 服务器地址 : http://xxx
     */
    public void setServer(String server) {
        if (mServer != null && !mServer.equals(server)) {
            mServer = server;
            RetrofitFactory.clear();
        } else {
            mServer = server;
        }
    }


    public Context getContext() {
        return mApplicationContext;
    }

    public String getServer() {
        return mServer;
    }

    @Subscribe(sticky = true)
    public void onLogin(EventLogin eventLogin) {
        XLogger.i("event login, set token " + eventLogin.getToken());
        OkHttpUtil.getInstance().setCommonHeader("token", eventLogin.getToken());
    }

    @Subscribe(sticky = true)
    public void onLogout(EventLogout eventLogout) {
        XLogger.i("event logout, clear token ");
        OkHttpUtil.getInstance().setCommonHeader("token", "");
    }

    public void setToken(String token) {
        this.mToken = token;
    }
}
