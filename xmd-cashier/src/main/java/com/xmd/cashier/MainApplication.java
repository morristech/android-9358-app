package com.xmd.cashier;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Process;
import android.os.SystemClock;

import com.shidou.commonlibrary.helper.CrashHandler;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.cashier.activity.BaseActivity;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.LocalPersistenceManager;
import com.xmd.cashier.dal.db.DBManager;
import com.xmd.cashier.dal.net.AuthPayOkHttpUtil;
import com.xmd.cashier.dal.net.AuthPayRetrofit;
import com.xmd.cashier.dal.net.GeneOrderOkHttpUtil;
import com.xmd.cashier.dal.net.GeneOrderRetrofit;
import com.xmd.cashier.dal.net.SpaOkHttp;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.CashierManager;
import com.xmd.cashier.manager.ChannelManager;
import com.xmd.cashier.manager.CustomPushMessageListener;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.MonitorManager;
import com.xmd.cashier.manager.NotifyManager;
import com.xmd.cashier.manager.UmengManager;
import com.xmd.cashier.pos.PosImpl;
import com.xmd.cashier.service.CustomService;
import com.xmd.cashier.service.POSAliveReportService;
import com.xmd.m.network.OkHttpUtil;
import com.xmd.m.network.XmdNetwork;
import com.xmd.m.notify.push.XmdPushManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Request;

public class MainApplication extends Application implements CrashHandler.Callback {
    private static final String TAG = "MainApplication";
    private final Object mActivityListObject = new Object();
    private ArrayList<BaseActivity> mActivityList = new ArrayList<>();
    private static MainApplication instance;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            Utils.setAppVersion(packageInfo.versionCode, packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        XLogger.init(5, getFilesDir().getPath() + File.separator + "logs"); // /data/data/com.xmd.cashier/files
        XLogger.setWriteFileLevel(XLogger.LEVEL_INFO);
        XLogger.setGloableTag("9358");
        printBaseInfo();

        // 初始化旺POS服务
        CashierManager.getInstance().init(getApplicationContext(), null);

        LocalPersistenceManager.init(this);
        DBManager.init(this);
        SPManager.getInstance().init(getSharedPreferences("9358", MODE_PRIVATE));
        CrashHandler.getInstance().init(getApplicationContext(), this);
        XToast.init(getApplicationContext(), 0);

        // 友盟
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);   //场景设置
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);    //初始化
        UMConfigure.setEncryptEnabled(true);    //设置日志加密
        UmengManager.getInstance().init(this);

        ThreadPoolManager.init(this);

        Set<String> functions = new HashSet<>();
        XmdApp.getInstance().init(this, SPManager.getInstance().getSpaServerAddress(), functions);

        // 初始化网络模块
        XmdNetwork.getInstance().init(this, "9358-cashier-" + BuildConfig.POS_TYPE, SPManager.getInstance().getSpaServerAddress());
        XmdNetwork.getInstance().setDebug(true);
        XmdNetwork.getInstance().setHeader("Device-Identifier", PosImpl.getInstance().getPosIdentifierNo());
        XmdNetwork.getInstance().setRequestPreprocess(new OkHttpUtil.RequestPreprocess() {
            @Override
            public Request preProcess(Request request) {
                return SpaOkHttp.checkAndSign(request);
            }
        });

        // 主扫接口网络请求初始化
        AuthPayRetrofit.clear();
        AuthPayOkHttpUtil.init(this.getFilesDir() + File.separator + "xmd-network", 10 * 1024 * 1024, 40000, 40000, 40000);
        AuthPayOkHttpUtil.getInstance().setCommonHeader("User-Agent", "9358-cashier-" + BuildConfig.POS_TYPE);
        AuthPayOkHttpUtil.getInstance().setLog(true);
        AuthPayOkHttpUtil.getInstance().setCommonHeader("Device-Identifier", PosImpl.getInstance().getPosIdentifierNo());
        AuthPayOkHttpUtil.getInstance().setRequestPreprocess(new AuthPayOkHttpUtil.RequestPreprocess() {
            @Override
            public Request preProcess(Request request) {
                return SpaOkHttp.checkAndSign(request);
            }
        });

        // 生成订单请求初始化
        GeneOrderRetrofit.clear();
        GeneOrderOkHttpUtil.init(this.getFilesDir() + File.separator + "xmd-network", 10 * 1024 * 1024, 10000, 10000, 10000);
        GeneOrderOkHttpUtil.getInstance().setCommonHeader("User-Agent", "9358-cashier-" + BuildConfig.POS_TYPE);
        GeneOrderOkHttpUtil.getInstance().setLog(true);
        GeneOrderOkHttpUtil.getInstance().setCommonHeader("Device-Identifier", PosImpl.getInstance().getPosIdentifierNo());
        GeneOrderOkHttpUtil.getInstance().setRequestPreprocess(new GeneOrderOkHttpUtil.RequestPreprocess() {
            @Override
            public Request preProcess(Request request) {
                return SpaOkHttp.checkAndSign(request);
            }
        });

        // 初始化推送
        XmdPushManager.getInstance().init(this, "pos", null);

        MonitorManager.getInstance().setManager((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE),
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE),
                (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE));
        MonitorManager.getInstance().startPollingWifiStatus(SystemClock.elapsedRealtime());

        if (AccountManager.getInstance().isLogin()) {
            XmdPushManager.getInstance().addListener(CustomPushMessageListener.getInstance());
            SPManager.getInstance().initPushTagCount();
            XmdNetwork.getInstance().setHeader("Club-Id", AccountManager.getInstance().getClubId());
            AuthPayOkHttpUtil.getInstance().setCommonHeader("Club-Id", AccountManager.getInstance().getClubId());
            GeneOrderOkHttpUtil.getInstance().setCommonHeader("Club-Id", AccountManager.getInstance().getClubId());
            EventBus.getDefault().removeStickyEvent(EventLogout.class);
            com.xmd.app.user.User user = new com.xmd.app.user.User(AccountManager.getInstance().getUserId());
            EventBus.getDefault().postSticky(new EventLogin(AccountManager.getInstance().getToken(), user));
            MemberManager.getInstance().startGetMemberSetting();
            InnerManager.getInstance().startGetInnerSwitch();
            InnerManager.getInstance().getClubWorkTime();
            NotifyManager.getInstance().startRepeatOrderRecord(SystemClock.elapsedRealtime() + AppConstants.DEFAULT_INTERVAL);
            NotifyManager.getInstance().startRepeatOnlinePay(SystemClock.elapsedRealtime() + AppConstants.DEFAULT_INTERVAL);
            ChannelManager.getInstance().getPayChannelList(null);
        }

        // 开启服务
        CustomService.start();
        POSAliveReportService.start();
    }

    private void printBaseInfo() {
        XLogger.i(TAG, "============================================");
        XLogger.i(TAG, "======            9358收银台           ======");
        XLogger.i(TAG, "============================================");
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "APP VERSION CODE: " + Utils.getAppVersionCode());
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "APP VERSION NAME: " + Utils.getAppVersionName());
    }

    @Override
    public void onExitApplication() {
        exitApplication();
    }

    public void exitApplication() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "退出9358收银台!");
        MobclickAgent.onKillProcess(this);
        synchronized (mActivityListObject) {
            for (int i = 0; i < mActivityList.size(); i++) {
                BaseActivity activity = mActivityList.remove(i);
                activity.finish();
            }
        }
        android.os.Process.killProcess(Process.myPid());
    }

    public void addActivity(BaseActivity activity) {
        synchronized (mActivityListObject) {
            if (!mActivityList.contains(activity)) {
                mActivityList.add(activity);
            }
        }
    }

    public void removeActivity(BaseActivity activity) {
        synchronized (mActivityListObject) {
            mActivityList.remove(activity);
        }
    }
}
