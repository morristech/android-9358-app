package com.xmd.m.notify.display;

import java.io.Serializable;

/**
 * Created by mo on 17-6-27.
 * 小摩豆显示协议
 */

public class XmdDisplay implements Serializable {
    public static final String BUSINESS_TYPE_CHAT_MESSAGE = "chatMessage";

    public static final String ACTION_VIEW_WEB = "viewWeb";
    public static final String ACTION_CHAT_TO = "chatTo";
    public static final String ACTION_VIEW_ORDER = "viewOrder";
    public static final String ACTION_VIEW_FAST_PAY = "viewFastPay";

    public static final int SCENE_FG = 1;
    public static final int SCENE_BG = 2;

    public static final int STYLE_NONE = 1;
    public static final int STYLE_NOTIFICATION = 2;

    public static final int FLAG_RING = 1;
    public static final int FLAG_VIBRATE = 2;
    public static final int FLAG_LIGHT = 4;

    private String businessType;// 用来区分不同业务，同一种业务同一种style只显示一个
    private int scene;
    private int style;
    private int flags;

    private CharSequence title;
    private CharSequence message;
    private CharSequence audioUri;
    private String action;
    private String actionData;


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

    public CharSequence getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(CharSequence audioUri) {
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
