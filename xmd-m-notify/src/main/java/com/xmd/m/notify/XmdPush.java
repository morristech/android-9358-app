package com.xmd.m.notify;

import android.content.Context;

/**
 * Created by mo on 17-6-23.
 * 小摩豆推送
 */

class XmdPush {

    private static final XmdPush ourInstance = new XmdPush();

    static XmdPush getInstance() {
        return ourInstance;
    }

    private XmdPush() {
    }


    private String getuiAppId;
    private String getuiAppKey;
    private String getuiAppSecret;
    private String getuiMasterSecret;

    public void init(Context context, String appId, String appKey, String appSecret, String masterSecret) {
        this.getuiAppId = appId;
        this.getuiAppKey = appKey;
        this.getuiAppSecret = appSecret;
        this.getuiMasterSecret = masterSecret;


    }
}
