package com.xmd.m.notify.push;

import com.xmd.m.notify.display.XmdDisplay;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by mo on 17-6-27.
 * 小摩豆消息推送协议
 */

public class XmdPushMessage {
    public static final String BUSINESS_TYPE_CHAT_MESSAGE = "chat_message";
    public static final String BUSINESS_TYPE_FAST_PAY = "fast_pay";
    public static final String BUSINESS_TYPE_JOIN_CLUB = "join_club";
    public static final String BUSINESS_TYPE_ORDER = "order";
    public static final String BUSINESS_TYPE_NEW_CUSTOMER = "new_customer";
    public static final String BUSINESS_TYPE_COMMENT = "comment";
    public static final String BUSINESS_TYPE_POSITION_INVITE = "position_invite";

    private String businessType; //业务类型
    private List<XmdDisplay> displayList; //展示详情
    private String data; //业务数据

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

    public String getData() {
        return data;
    }


    @Override
    public String toString() {
        return "XmdPushMessage{" +
                "businessType='" + businessType + '\'' +
                ", displayList=" + displayList +
                ", data='" + data + '\'' +
                '}';
    }
}
