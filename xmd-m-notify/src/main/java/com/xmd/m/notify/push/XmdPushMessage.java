package com.xmd.m.notify.push;

import com.xmd.m.notify.display.XmdDisplay;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by mo on 17-6-27.
 * 小摩豆消息推送协议
 */

public class XmdPushMessage {
    private String businessType;
    private List<XmdDisplay> displayList;

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public List<XmdDisplay> getDisplayList() {
        return displayList;
    }

    public void setDisplayList(List<XmdDisplay> displayList) {
        this.displayList = displayList;
    }

    public void show() {
        for (XmdDisplay display : displayList) {
            display.setBusinessType(businessType);
            EventBus.getDefault().post(display);
        }
    }
}
