package com.xmd.technician.bean;

/**
 * Created by ZR on 17-3-1.
 * 招呼记录信息:客户ID,客户环信ID,客户昵称,客户头像,打招呼时间,客户是否回复
 */

public class HelloRecordInfo {
    public String receiverAvatar;
    public String receiverEmChatId;
    public String receiverId;
    public String receiverName;
    public String replyTime;    //没有回复为空
    public String sendTime;
}
