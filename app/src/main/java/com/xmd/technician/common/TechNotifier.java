/************************************************************
 *  * EaseMob CONFIDENTIAL 
 * __________________ 
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved. 
 *  
 * NOTICE: All information contained herein is, and remains 
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package com.xmd.technician.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.hyphenate.util.EasyUtils;
import com.xmd.technician.bean.UserInfo;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.UserUtils;

import java.util.HashSet;


/**
 * 新消息提醒class
 * 
 * this class is subject to be inherited and implement the relative APIs
 */
public class TechNotifier {
    public static final int CHAT_TEXT = 0;
    public static final int CHAT_ORDER = 1;
    public static final int CHAT_REWARD = 3;
    public static final int CHAT_PAID_COUPON = 2;

    private final static String TAG = "notify";
    Ringtone ringtone = null;
    
    protected final static String[] msgs = { "发来一条消息", "给你下单了", "购买了你的点钟券", "打赏了你", "系统消息", "%1个联系人发来%2条消息"};

    protected static int notifyID = 0525; // start notification id
    protected static int foregroundNotifyID = 0555;

    protected NotificationManager notificationManager = null;

    protected HashSet<String> fromUsers = new HashSet<String>();
    protected int notificationNum = 0;

    protected Context appContext;
    protected String packageName;
    protected long lastNotifyTime;
    protected AudioManager audioManager;
    protected Vibrator vibrator;
    //protected TechNotificationInfoProvider notificationInfoProvider;

    public TechNotifier() {
    }
    
    /**
     * 开发者可以重载此函数
     * this function can be override
     * @param context
     * @return
     */
    public TechNotifier init(Context context){
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        packageName = appContext.getApplicationInfo().packageName;


        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        
        return this;
    }
    
    /**
     * 开发者可以重载此函数
     * this function can be override
     */
    public void reset(){
        resetNotificationCount();
        cancelNotificaton();
    }

    void resetNotificationCount() {
        notificationNum = 0;
        fromUsers.clear();
    }
    
    void cancelNotificaton() {
        if (notificationManager != null)
            notificationManager.cancelAll();
    }

    /**
     * 处理新收到的消息，然后发送通知
     * 
     * 开发者可以重载此函数
     * this function can be override
     * 
     * @param
     */
    public synchronized void showNotification(int msgType, String emchatId, String nick) {

        /*EaseSettingsProvider settingsProvider = EaseUI.getInstance().getSettingsProvider();
        if(!settingsProvider.isMsgNotifyAllowed(message)){
            return;
        }*/
        
        // 判断app是否在后台
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            sendNotification(msgType, emchatId, nick);
        }
        viberateAndPlayTone();
    }
    
    /**
     * 发送通知栏提示
     * This can be override by subclass to provide customer implementation
     * @param
     */
    protected void sendNotification(int msgType, String emchatId, String nick) {
        try {
            String username = nick;
            if(TextUtils.isEmpty(nick)) username = UserUtils.getUserNick(emchatId);
            /*ChatUser user = UserUtils.getUserInfo(emchatId);
            if(user != null){
                username = user.getNick();
            }*/
            CharSequence notifyText = TextUtils.concat(username, " ", msgs[msgType % msgs.length]);
            
            PackageManager packageManager = appContext.getPackageManager();
            // notification titile
            String contentTitle = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());

            // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                                                                        .setSmallIcon(appContext.getApplicationInfo().icon)
                                                                        .setWhen(System.currentTimeMillis())
                                                                        .setAutoCancel(true);

            Intent msgIntent = defaultLaunchIntent(emchatId);
            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            notificationNum++;
            fromUsers.add(emchatId);

            int fromUsersNum = fromUsers.size();
            String summaryBody = msgs[msgs.length - 1].replaceFirst("%1", Integer.toString(fromUsersNum)).replaceFirst("%2",Integer.toString(notificationNum));

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setTicker(notifyText);
            mBuilder.setContentText(summaryBody);
            mBuilder.setContentIntent(pendingIntent);
            // mBuilder.setNumber(notificationNum);
            Notification notification = mBuilder.build();

            notificationManager.notify(notifyID, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Intent defaultLaunchIntent(String emchatId){
        Intent intent = new Intent("com.xmd.technician.action.START_CHAT");
        intent.putExtra(ChatConstant.EMCHAT_ID, emchatId);
        return intent;
    }

    /**
     * 手机震动和声音提示
     */
    public void viberateAndPlayTone() {

        if (System.currentTimeMillis() - lastNotifyTime < 1000) {
            // received new messages within 2 seconds, skip play ringtone
            return;
        }
        
        try {
            lastNotifyTime = System.currentTimeMillis();

            // 判断是否处于静音模式
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                Logger.e(TAG, "in slient mode now");
                return;
            }
            /*EaseSettingsProvider settingsProvider = EaseUI.getInstance().getSettingsProvider();
            if(settingsProvider.isMsgVibrateAllowed(message))*/{
                long[] pattern = new long[] { 0, 180, 80, 120 };
                vibrator.vibrate(pattern, -1);
            }

            /*if(settingsProvider.isMsgSoundAllowed(message))*/{
                if (ringtone == null) {
                    Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                    if (ringtone == null) {
                        Logger.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
                        return;
                    }
                }
                
                if (!ringtone.isPlaying()) {
                    String vendor = Build.MANUFACTURER;
                    
                    ringtone.play();
                    // for samsung S3, we meet a bug that the phone will
                    // continue ringtone without stop
                    // so add below special handler to stop it after 3s if
                    // needed
                    if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                        Thread ctlThread = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    if (ringtone.isPlaying()) {
                                        ringtone.stop();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        };
                        ctlThread.run();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
