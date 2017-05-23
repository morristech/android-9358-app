package com.xmd.technician.bean;

import java.io.Serializable;

/**
 * Created by zr on 17-4-1.
 * 消息回复
 */

public class HelloReplyInfo implements Serializable {
    public String receiverAvatar;
    public String receiverEmChatId;
    public String receiverId;
    public String receiverName;
    public String replyTime;
    public String sendTime;
}
