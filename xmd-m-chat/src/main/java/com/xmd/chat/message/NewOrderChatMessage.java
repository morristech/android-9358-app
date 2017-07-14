package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;

/**
 * Created by mo on 17-7-14.
 * 用户预约后发此消息
 * <p>
 * msg :  "<span>发起预约</span><br>到店时间：<b>"+arriveTime+"</b><br>"+"预约项目：<b>"+(orderItem || "到店选择" )+"</b>"; //消息数据
 */

public class NewOrderChatMessage extends ChatMessage {
    private final static String ATTR_ORDER_ID = "orderId";

    private String itemName;
    private String arriveTime;

    public NewOrderChatMessage(EMMessage emMessage) {
        super(emMessage);
//        String content = super.getOriginContentText().toString();
//        String msg[] = content.split("<b>|</b>");
//        if (msg.length > 1) {
//            itemName = msg[1];
//        }
//        if (msg.length > 3) {
//            arriveTime = msg[3];
//        }
    }

    public String getOrderId() {
        return getSafeStringAttribute(ATTR_ORDER_ID);
    }

    public String getItemName() {
        return itemName;
    }

    public String getArriveTime() {
        return arriveTime;
    }
}
