package com.xmd.m.notify.display;

import java.io.Serializable;

/**
 * Created by mo on 17-6-27.
 * 小摩豆显示协议
 */

public class XmdDisplay implements Serializable {

    public static final String ACTION_VIEW_WEB = "viewWeb";
    public static final String ACTION_CHAT_TO = "chatTo";
    public static final String ACTION_VIEW_ORDER = "viewOrder";
    public static final String ACTION_VIEW_ORDER_DETAIL = "viewOrderDetail";
    public static final String ACTION_VIEW_FAST_PAY = "viewFastPay";
    public static final String ACTION_VIEW_COMMENT = "viewComment";
    public static final String ACTION_VIEW_COMMENT_DETAIL = "viewBadCommentDetail";

    public static final int SCENE_FG = 1;
    public static final int SCENE_BG = 2;

    public static final int STYLE_NONE = 1;
    public static final int STYLE_NOTIFICATION = 2;
    public static final int STYLE_FLOAT_TOAST = 100;

    public static final int FLAG_RING = 1;
    public static final int FLAG_VIBRATE = 2;
    public static final int FLAG_LIGHT = 4;

    private String businessType;// 用来区分不同业务，同一种业务同一种style只显示一个
    private int scene;
    private int style;
    private int flags;

    private String title;
    private String message;
    private String audioUri;
    private String action;
    private String actionData;

    private int x;
    private int y;
    private int duration;

    public XmdDisplay() {
        scene = SCENE_BG;
        style = STYLE_NOTIFICATION;
        flags = FLAG_LIGHT | FLAG_RING | FLAG_VIBRATE;
    }


    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "XmdDisplay{" +
                "businessType='" + businessType + '\'' +
                ", scene=" + scene +
                ", style=" + style +
                ", flags=" + flags +
                ", title=" + title +
                ", message=" + message +
                ", audioUri=" + audioUri +
                ", action=" + action +
                ", actionData=" + actionData +
                '}';
    }
}
