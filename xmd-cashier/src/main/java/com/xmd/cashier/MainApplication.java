package com.xmd.cashier;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;

import com.shidou.commonlibrary.helper.CrashHandler;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.umeng.analytics.MobclickAgent;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.cashier.activity.BaseActivity;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.ThreadManager;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.LocalPersistenceManager;
import com.xmd.cashier.dal.db.DBManager;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaOkHttp;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.DataReportManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.NotifyManager;
import com.xmd.cashier.service.CustomService;
import com.xmd.m.network.OkHttpUtil;
import com.xmd.m.network.XmdNetwork;
import com.xmd.m.notify.push.XmdPushManager;
import com.xmd.m.notify.push.XmdPushMessage;
import com.xmd.m.notify.push.XmdPushMessageListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Request;

public class MainApplication extends Application implements CrashHandler.Callback {
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

        XLogger.init(0, null);
        XLogger.setGloableTag("9358");
        LocalPersistenceManager.init(this);
        DBManager.init(this);
        SPManager.getInstance().init(getSharedPreferences("9358", MODE_PRIVATE));
        CrashHandler.getInstance().init(getApplicationContext(), this);
        XToast.init(getApplicationContext(), 0);

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.enableEncrypt(true);
        ThreadPoolManager.init(this);
        ThreadManager.init(this);

        DataReportManager.getInstance().startMonitor();

        // 初始化网络模块
        XmdNetwork.getInstance().init(this, "9358-cashier-" + BuildConfig.POS_TYPE, SPManager.getInstance().getSpaServerAddress());
        XmdNetwork.getInstance().setDebug(true);
        XmdNetwork.getInstance().setRequestPreprocess(new OkHttpUtil.RequestPreprocess() {
            @Override
            public Request preProcess(Request request) {
                return SpaOkHttp.checkAndSign(request);
            }
        });

        // 初始化推送
        XmdPushManager.getInstance().init(this, "pos", new XmdPushMessageListener() {
            @Override
            public void onMessage(XmdPushMessage message) {
                // 按照指定格式处理消息
            }

            @Override
            public void onRawMessage(String message) {
                // 处理原始透传消息
                XLogger.i("MainApplication:" + message);
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    switch (jsonObject.getString(RequestConstant.KEY_BUSINESS_TYPE)) {
                        case AppConstants.PUSH_TAG_FASTPAY:
                            SPManager.getInstance().setFastPayPushTag(jsonObject.getInt(RequestConstant.KEY_COUNT));
                            break;
                        case AppConstants.PUSH_TAG_ORDER:
                            SPManager.getInstance().setOrderPushTag(jsonObject.getInt(RequestConstant.KEY_COUNT));
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if (AccountManager.getInstance().isLogin()) {
            EventBus.getDefault().removeStickyEvent(EventLogout.class);
            com.xmd.app.user.User user = new com.xmd.app.user.User(AccountManager.getInstance().getUserId());
            EventBus.getDefault().postSticky(new EventLogin(AccountManager.getInstance().getToken(), user));
            NotifyManager.getInstance().startGetFastPayCountAsync();
            NotifyManager.getInstance().startGetOrderCountAsync();
            MemberManager.getInstance().startGetMemberSetting();
        }

        // 开启服务
        CustomService.start();
    }


    @Override
    public void onExitApplication() {
        exitApplication();
    }

    public void exitApplication() {
        XLogger.i("exit Application!");
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
