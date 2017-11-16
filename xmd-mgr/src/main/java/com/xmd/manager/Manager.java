package com.xmd.manager;

import android.content.Context;

import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.msgctrl.ControllerRegister;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by sdcm on 15-10-22.
 */
public class Manager {


    private static final Manager sInstance = new Manager();

    private Context mAppContext;


    private Manager() {
    }

    public static Manager getInstance() {
        return sInstance;
    }

    /**
     * the app start
     *
     * @param appContext
     */
    public void initialize(Context appContext) {

        mAppContext = appContext;

        AppConfig.initialize();

        SettingFlags.initialize();

        SharedPreferenceHelper.initialize();

        ThreadManager.initialize();

        ControllerRegister.initialize();

    }

    public void prepareBeforeUserLogout() {
        EventBus.getDefault().removeStickyEvent(EventLogin.class);
        EventBus.getDefault().postSticky(new EventLogout(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserId()));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGOUT);
        //SharedPreferenceHelper.clearUserInfo();
    }

    /**
     * Check whether there is new version automatically
     *
     * @param autoCheck
     */
    public void checkUpgrade(final boolean autoCheck) {
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, () -> {
                    if (autoCheck) {
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE);
                    } else {
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANUALLY_CHECK_UPGRADE);
                    }
                }
        );
    }
}
