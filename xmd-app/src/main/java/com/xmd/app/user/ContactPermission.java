package com.xmd.app.user;

import java.io.Serializable;

/**
 * Created by mo on 17-7-27.
 * 联系权联
 */

public class ContactPermission implements Serializable {


    private boolean hello;//打招呼
    private boolean echat;//网络聊天
    private boolean call;//打电话

    public boolean isHello() {
        return hello;
    }

    public void setHello(boolean hello) {
        this.hello = hello;
    }

    public boolean isEchat() {
        return echat;
    }

    public void setEchat(boolean echat) {
        this.echat = echat;
    }

    public boolean isCall() {
        return call;
    }

    public void setCall(boolean call) {
        this.call = call;
    }

    @Override
    public String toString() {
        return "ContactPermission{" +
                "hello=" + hello +
                ", echat=" + echat +
                ", call=" + call +
                '}';
    }
}
