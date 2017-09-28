package com.xmd.app.user;

import java.io.Serializable;

/**
 * Created by mo on 17-7-27.
 * 联系权联
 */
public class ContactPermission implements Serializable {
    private static final long serialVersionUID = 88750009432719L;

    private boolean hello;//打招呼
    private boolean echat;//网络聊天
    private boolean call;//打电话


    public ContactPermission(boolean hello, boolean echat, boolean call) {
        this.hello = hello;
        this.echat = echat;
        this.call = call;
    }

    public ContactPermission() {
    }

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

    public boolean getHello() {
        return this.hello;
    }

    public boolean getEchat() {
        return this.echat;
    }

    public boolean getCall() {
        return this.call;
    }

    @Override
    public int hashCode() {
        return 49 + hashCode(hello) + hashCode(echat) + hashCode(call);
    }

    private int hashCode(boolean b) {
        return b ? 1 : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ContactPermission)) {
            return false;
        }
        ContactPermission o = (ContactPermission) obj;
        return hello == o.hello && echat == o.echat && call == o.call;
    }
}
