package com.xmd.technician.notify;

/**
 * Created by heyangya on 17-4-28.
 * 通知设置
 */

public class NotifySetting {
    private int notifyId; //通知ID
    private int priority; //优先级
    private boolean sound; //是否播放声音
    private boolean light; //是否有灯光
    private boolean vibrate; //是否振动

    private CharSequence title;
    private CharSequence message;
    private Class targetActivity;

    public NotifySetting(int notifyId, int priority, boolean sound, boolean light, boolean vibrate, CharSequence title, CharSequence message, Class targetActivity) {
        this.notifyId = notifyId;
        this.priority = priority;
        this.sound = sound;
        this.light = light;
        this.vibrate = vibrate;
        this.title = title;
        this.message = message;
        this.targetActivity = targetActivity;
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
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

    public Class getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(Class targetActivity) {
        this.targetActivity = targetActivity;
    }
}
