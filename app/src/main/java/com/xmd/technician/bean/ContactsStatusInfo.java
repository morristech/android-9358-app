package com.xmd.technician.bean;

/**
 * Created by ZR on 17-3-3.
 * 技师同客户之间的关系,关系到同客户之间的互动
 */

public class ContactsStatusInfo {
    public boolean call;    //是否可以打电话
    public boolean echat;   //是否可以通过APP聊天
    public boolean hello;   //是否可以打招呼
    public boolean sms;     //是否可以发短信
}
