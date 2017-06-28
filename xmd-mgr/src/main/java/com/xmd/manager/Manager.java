package com.xmd.manager;

import android.content.Context;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.igexin.sdk.PushManager;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.manager.beans.EmchatMsgResult;
import com.xmd.manager.chat.EmchatConstant;
import com.xmd.manager.chat.EmchatUser;
import com.xmd.manager.chat.EmchatUserHelper;
import com.xmd.manager.chat.SimpleEMMessageListener;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.database.EmchatUserDao;
import com.xmd.manager.msgctrl.ControllerRegister;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by sdcm on 15-10-22.
 */
public class Manager {


    private static final Manager sInstance = new Manager();

    private Context mAppContext;

    private EMMessageListener mMessageListener = new SimpleEMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            for (EMMessage message : list) {
                try {
                    String name = message.getStringAttribute(EmchatConstant.KEY_NAME);
                    String head = message.getStringAttribute(EmchatConstant.KEY_HEADER);
                    String userName = message.getUserName();
                    EmchatUser user = new EmchatUser(userName, name, head);
                    EmchatUserDao mEmchatUserDao = new EmchatUserDao(ManagerApplication.getAppContext());
                    mEmchatUserDao.saveOrUpdate(user);

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }

            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> RxBus.getInstance().post(new EmchatMsgResult(list)));
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }
    };

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

        PushManager.getInstance().initialize(mAppContext);

        // EaseMob Chatting
//        EMOptions emOptions = new EMOptions();
//        EMClient.getInstance().init(appContext, emOptions);
//        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
//        EMClient.getInstance().setDebugMode(true);
//        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    public void prepareBeforeUserLogout() {
        EventBus.getDefault().removeStickyEvent(EventLogin.class);
        EventBus.getDefault().postSticky(new EventLogout(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserId()));
        EmchatUserHelper.logout();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID);
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
