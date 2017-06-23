package com.xmd.m.notify;

import android.net.Uri;
import android.support.v7.app.NotificationCompat;

/**
 * Created by heyangya on 17-4-28.
 * 通知预先设置
 */

public class NotifySetting {
    public static final int NOTIFY_SCENE_BACKGROUND = 1;
    public static final int NOTIFY_SCENE_FOREGROUND = 2;

    private int notifyId; //通知ID
    private int notifyIcon=R.mipmap.ic_launcher;
    private int priority= NotificationCompat.PRIORITY_DEFAULT; //优先级
    private boolean sound=true; //是否播放声音
    private boolean light=true; //是否有灯光
    private boolean vibrate=true; //是否振动
    private Uri soundUri;
    private int notifyScene=NOTIFY_SCENE_BACKGROUND;

    private CharSequence title;
    private CharSequence message;

    public NotifySetting(int notifyId,CharSequence title, CharSequence message) {
        this.notifyId = notifyId;
        this.title = title;
        this.message = message;
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public int getNotifyIcon() {
        return notifyIcon;
    }

    public void setNotifyIcon(int notifyIcon) {
        this.notifyIcon = notifyIcon;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public CharSequence getMessage() {
        return message;
    }

    public void setMessage(CharSequence message) {
        this.message = message;
    }

    public Uri getSoundUri() {
        return soundUri;
    }

    public void setSoundUri(Uri soundUri) {
        this.soundUri = soundUri;
    }

    public int getNotifyScene() {
        return notifyScene;
    }

    public void setNotifyScene(int notifyScene) {
        this.notifyScene = notifyScene;
    }
}
