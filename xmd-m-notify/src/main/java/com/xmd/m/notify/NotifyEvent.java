package com.xmd.m.notify;

import android.app.PendingIntent;

/**
 * Created by mo on 17-6-20.
 * 通知事件
 */

public class NotifyEvent {
    public static final int TYPE_DEFAULT = 0x7601;
    public static final int TYPE_PAY_NOTIFY = 0x7602; //客户买单
    public static final int TYPE_JOIN_VERIFY = 0x7603; // 申请加入会所审核
    public static final int TYPE_CHAT_MESSAGE = 0x7604; //聊天
    public static final int TYPE_ORDER = 0x7605; //订单



    private int type; //通知类型
    private CharSequence title;
    private CharSequence message;
    private PendingIntent pendingIntent;

    public NotifyEvent(int type) {
        this.type = type;
    }

    public NotifyEvent(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public NotifyEvent(int type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
    }

    public NotifyEvent(int type, String title, String message, PendingIntent pendingIntent) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.pendingIntent = pendingIntent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }


    @Override
    public String toString() {
        return "NotifyEvent{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", pendingIntent=" + pendingIntent +
                '}';
    }
}
