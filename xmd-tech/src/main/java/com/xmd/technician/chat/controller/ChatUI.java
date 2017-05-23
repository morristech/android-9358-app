package com.xmd.technician.chat.controller;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.Emojicon;
import com.xmd.technician.chat.model.EaseNotifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lhj on 17-3-31.
 */

public class ChatUI {
    private static final String TAG = ChatUI.class.getSimpleName();
    private static ChatUI instance = null;
    private EaseUserProfileProvider userProvider;
    private EaseSettingsProvider settingsProvider;
    private Context appContext = null;
    private boolean sdkInited = false;
    private EaseNotifier notifier = null;

    private List<Activity> activityList = new ArrayList<Activity>();

    public void pushActivity(Activity activity){
        if(!activityList.contains(activity)){
            activityList.add(0,activity);
        }
    }

    public void popActivity(Activity activity){
        activityList.remove(activity);
    }


    private ChatUI(){}
    public synchronized static ChatUI getInstance(){
        if(instance == null){
            instance = new ChatUI();
        }
        return instance;
    }



    /**
     *this function will initialize the SDK and easeUI kit
     *
     * @return boolean true if caller can continue to call SDK related APIs after calling onInit, otherwise false.
     *
     * @param context
     * @param options use default if options is null
     * @return
     */
    public synchronized boolean init(Context context, EMOptions options){
        if(sdkInited){
            return true;
        }
        appContext = context;

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

        Log.d(TAG, "process app name : " + processAppName);

        // if there is application has remote service, application:onCreate() maybe called twice
        // this check is to make sure SDK will initialized only once
        // return if process name is not application's name since the package name is the default process name
        if (processAppName == null || !processAppName.equalsIgnoreCase(appContext.getPackageName())) {
            Log.e(TAG, "enter the service process!");
            return false;
        }
        if(options == null){
            EMClient.getInstance().init(context, initChatOptions());
        }else{
            EMClient.getInstance().init(context, options);
        }
      //  EMClient.getInstance().init(context, initChatOptions());
        initNotifier();
        registerMessageListener();

        if(settingsProvider == null){
            settingsProvider = new DefaultSettingsProvider();
        }

        sdkInited = true;
        return true;
    }


    protected EMOptions initChatOptions(){
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // change to need confirm contact invitation
      //  options.setAcceptInvitationAlways(false);
        // set if need read ack
        options.setRequireAck(true);
        // set if need delivery ack
        options.setRequireDeliveryAck(false);

        return options;
    }

    void initNotifier(){
        notifier = createNotifier();
        notifier.init(appContext);
    }

    private void registerMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
              //  EaseAtMessageHelper.get().parseMessages(messages);
            }
            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }
            @Override
            public void onMessageDelivered(List<EMMessage> messages) {
            }
            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {

            }
        });
    }

    protected EaseNotifier createNotifier(){
        return new EaseNotifier();
    }

    public EaseNotifier getNotifier(){
        return notifier;
    }

    public boolean hasForegroundActivities(){
        return activityList.size() != 0;
    }

    /**
     * set user profile provider
     * @param
     */
    public void setUserProfileProvider(EaseUserProfileProvider userProvider){
        this.userProvider = userProvider;
    }

    /**
     * get user profile provider
     * @return
     */
    public EaseUserProfileProvider getUserProfileProvider(){
        return userProvider;
    }

    public void setSettingsProvider(EaseSettingsProvider settingsProvider){
        this.settingsProvider = settingsProvider;
    }

    public EaseSettingsProvider getSettingsProvider(){
        return settingsProvider;
    }


    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * User profile provider
     * @author wei
     *
     */
    public interface EaseUserProfileProvider {
        /**
         * return EaseUser for input username
         * @param username
         * @return
         */
        ChatUser getUser(String username);
    }

    /**
     * Emojicon provider
     *
     */
    public interface EaseEmojiconInfoProvider {

        Emojicon getEmojiconInfo(String emojiconIdentityCode);

        Map<String, Object> getTextEmojiconMapping();
    }

    private EaseEmojiconInfoProvider emojiconInfoProvider;


    public EaseEmojiconInfoProvider getEmojiconInfoProvider(){
        return emojiconInfoProvider;
    }


    public void setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider){
        this.emojiconInfoProvider = emojiconInfoProvider;
    }


    public interface EaseSettingsProvider {
        boolean isMsgNotifyAllowed(EMMessage message);
        boolean isMsgSoundAllowed(EMMessage message);
        boolean isMsgVibrateAllowed(EMMessage message);
        boolean isSpeakerOpened();
    }

    /**
     * default settings provider
     *
     */
    protected class DefaultSettingsProvider implements EaseSettingsProvider{

        @Override
        public boolean isMsgNotifyAllowed(EMMessage message) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isMsgSoundAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isMsgVibrateAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isSpeakerOpened() {
            return true;
        }
    }

    public Context getContext(){
        return appContext;
    }
}
